package it.unifi.balpha.store;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "category_id")
	private List<Product> products;
	
	protected Category() {}
	
	public Category(String name) {
		validate(name);
		
		this.name = name;
		this.products = new ArrayList<>();
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	@Override
    public String toString() {
        return this.name;
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
