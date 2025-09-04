package io.github.abbassizied.customer_service.service;

import io.github.abbassizied.customer_service.domain.Address;
import io.github.abbassizied.customer_service.domain.Customer;
import io.github.abbassizied.customer_service.kafka.CustomerEvent;
import io.github.abbassizied.customer_service.kafka.CustomerProducer;
import io.github.abbassizied.customer_service.model.AddressDTO;
import io.github.abbassizied.customer_service.model.CustomerDTO;
import io.github.abbassizied.customer_service.repos.CustomerRepository;
import io.github.abbassizied.customer_service.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerProducer producer;

    public CustomerService(CustomerRepository customerRepository, CustomerProducer producer) {
        this.customerRepository = customerRepository;
        this.producer = producer;
    }

    public List<CustomerDTO> findAll() {
        final List<Customer> customers = customerRepository.findAll(Sort.by("id"));
        return customers.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CustomerDTO get(final Long id) {
        return customerRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CustomerDTO customerDTO) {
        final Customer customer = new Customer();
        mapToEntity(customerDTO, customer);
        // 1️⃣ Save first so ID and audit fields are generated
        Customer saved = customerRepository.save(customer);

        // 2️⃣ Publish event with the saved entity
        producer.sendEvent(new CustomerEvent(
                "CREATED",
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getShippingAddress() != null ? saved.getShippingAddress() : null,
                saved.getBillingAddress() != null ? saved.getBillingAddress() : null));

        return saved.getId();
    }

    public void update(final Long id, final CustomerDTO customerDTO) {
        final Customer customer = customerRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        mapToEntity(customerDTO, customer);
        Customer updated = customerRepository.save(customer);

        producer.sendEvent(new CustomerEvent(
                "UPDATED",
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                updated.getPhone(),
                updated.getShippingAddress() != null ? updated.getShippingAddress() : null,
                updated.getBillingAddress() != null ? updated.getBillingAddress() : null));
    }

    public void delete(final Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customerRepository.delete(customer);

        producer.sendEvent(new CustomerEvent(
                "DELETED",
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getShippingAddress() != null ? customer.getShippingAddress() : null,
                customer.getBillingAddress() != null ? customer.getBillingAddress() : null));
    }

    private CustomerDTO mapToDTO(final Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setShippingAddress(mapAddressToDTO(customer.getShippingAddress()));
        customerDTO.setBillingAddress(mapAddressToDTO(customer.getBillingAddress()));
        return customerDTO;
    }

    private AddressDTO mapAddressToDTO(final Address address) {
        if (address == null) {
            return null;
        }
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet(address.getStreet());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setPostalCode(address.getPostalCode());
        addressDTO.setCountry(address.getCountry());
        return addressDTO;
    }

    private Customer mapToEntity(final CustomerDTO customerDTO, final Customer customer) {
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setShippingAddress(mapDTOToAddress(customerDTO.getShippingAddress()));
        customer.setBillingAddress(mapDTOToAddress(customerDTO.getBillingAddress()));
        return customer;
    }

    private Address mapDTOToAddress(final AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCountry(addressDTO.getCountry());
        return address;
    }

    public boolean emailExists(final String email) {
        return customerRepository.existsByEmailIgnoreCase(email);
    }

    public boolean phoneExists(final String phone) {
        return customerRepository.existsByPhoneIgnoreCase(phone);
    }
}