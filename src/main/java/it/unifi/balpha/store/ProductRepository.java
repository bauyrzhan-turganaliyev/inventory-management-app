package it.unifi.balpha.store;

import java.util.List;

public interface ProductRepository {
	void save(Product product);
	Product findById(Long id);
	List<Product> findAll();
    void deleteById(Long id);
}
