package it.unifi.balpha.store;

public interface CategoryRepository {
	void save(Category category);
	Category findById(Long id);
}
