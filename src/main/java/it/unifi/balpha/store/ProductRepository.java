package it.unifi.balpha.store;

public interface ProductRepository {
	void save(Product product);
	Product findById(Long id);
}
