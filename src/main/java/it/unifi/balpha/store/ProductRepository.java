package it.unifi.balpha.store;

import jakarta.persistence.EntityManager;
import java.util.List;

public interface ProductRepository {
    Product findById(Long id);
    List<Product> findAll();
    Product save(EntityManager em, Product product);
    void deleteById(Long id);
}