package it.unifi.balpha.store;

import java.util.List;

public interface ProductRepository {
    Product findById(Long id);
    List<Product> findAll();
    Product save(Product product);
    void deleteById(Long id);
}