package io.github.abbassizied.product_service.service;

import io.github.abbassizied.product_service.domain.Product;
import io.github.abbassizied.product_service.model.ProductDTO;
import io.github.abbassizied.product_service.repos.ProductRepository;
import io.github.abbassizied.product_service.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.abbassizied.product_service.kafka.ProductEvent;
import io.github.abbassizied.product_service.kafka.ProductProducer;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductProducer producer;

    public ProductService(final ProductRepository productRepository, ProductProducer producer) {
        this.productRepository = productRepository;
        this.producer = producer;
    }

    public List<ProductDTO> findAll() {
        final List<Product> products = productRepository.findAll(Sort.by("id"));
        return products.stream()
                .map(product -> mapToDTO(product, new ProductDTO()))
                .toList();
    }

    public ProductDTO get(final Long id) {
        return productRepository.findById(id)
                .map(product -> mapToDTO(product, new ProductDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ProductDTO productDTO) {
        final Product product = new Product();
        mapToEntity(productDTO, product);

        // 1️⃣ Save first
        Product saved = productRepository.save(product);

        // 2️⃣ Publish with correct values
        producer.sendEvent(new ProductEvent(
                "CREATED",
                saved.getId(),
                saved.getName(),
                saved.getQuantity(),
                saved.getPrice()));

        return saved.getId();
    }

    public void update(final Long id, final ProductDTO productDTO) {
        final Product product = productRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        mapToEntity(productDTO, product);

        // 1️⃣ Save updated product
        Product updated = productRepository.save(product);

        // 2️⃣ Publish updated event
        producer.sendEvent(new ProductEvent(
                "UPDATED",
                updated.getId(),
                updated.getName(),
                updated.getQuantity(),
                updated.getPrice()));
    }

    public void delete(final Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 1️⃣ Delete entity
        productRepository.delete(product);

        // 2️⃣ Publish deleted event
        producer.sendEvent(new ProductEvent(
                "DELETED",
                product.getId(),
                product.getName(),
                product.getQuantity(),
                product.getPrice()));
    }

    private ProductDTO mapToDTO(final Product product, final ProductDTO productDTO) {
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setPrice(product.getPrice());
        return productDTO;
    }

    private Product mapToEntity(final ProductDTO productDTO, final Product product) {
        product.setName(productDTO.getName());
        product.setQuantity(productDTO.getQuantity());
        product.setPrice(productDTO.getPrice());
        return product;
    }

}
