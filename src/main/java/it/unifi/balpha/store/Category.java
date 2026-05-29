package it.unifi.balpha.store;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private final String name;
	private final List<Product> products;
	
	public Category(String name) {
		validate(name);
		
		this.name = name;
		this.products = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	public void addProduct(Product product) {
		if (product == null) {
			throw new IllegalArgumentException("Cannot add null product to category");
		}
		
		this.products.add(product);
	}
	
	private void validate(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Category name cannot be empty");
		}
	}
}
