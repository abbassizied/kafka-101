package io.github.abbassizied.customer_service.service;

import io.github.abbassizied.customer_service.domain.Customer;
import io.github.abbassizied.customer_service.model.CustomerDTO;
import io.github.abbassizied.customer_service.repos.CustomerRepository;
import io.github.abbassizied.customer_service.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDTO> findAll() {
        final List<Customer> customers = customerRepository.findAll(Sort.by("id"));
        return customers.stream()
                .map(customer -> mapToDTO(customer, new CustomerDTO()))
                .toList();
    }

    public CustomerDTO get(final Long id) {
        return customerRepository.findById(id)
                .map(customer -> mapToDTO(customer, new CustomerDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CustomerDTO customerDTO) {
        final Customer customer = new Customer();
        mapToEntity(customerDTO, customer);
        return customerRepository.save(customer).getId();
    }

    public void update(final Long id, final CustomerDTO customerDTO) {
        final Customer customer = customerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(customerDTO, customer);
        customerRepository.save(customer);
    }

    public void delete(final Long id) {
        customerRepository.deleteById(id);
    }

    private CustomerDTO mapToDTO(final Customer customer, final CustomerDTO customerDTO) {
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setShippingAddress(customer.getShippingAddress());
        customerDTO.setBillingAddress(customer.getBillingAddress());
        return customerDTO;
    }

    private Customer mapToEntity(final CustomerDTO customerDTO, final Customer customer) {
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhone(customerDTO.getPhone());
        customer.setShippingAddress(customerDTO.getShippingAddress());
        customer.setBillingAddress(customerDTO.getBillingAddress());
        return customer;
    }

    public boolean emailExists(final String email) {
        return customerRepository.existsByEmailIgnoreCase(email);
    }

    public boolean phoneExists(final String phone) {
        return customerRepository.existsByPhoneIgnoreCase(phone);
    }

}
