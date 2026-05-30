package it.unifi.balpha.store;

import java.util.List;

public interface CategoryRepository {
	void save(Category category);
	Category findById(Long id);
	List<Category> findAll();
}
