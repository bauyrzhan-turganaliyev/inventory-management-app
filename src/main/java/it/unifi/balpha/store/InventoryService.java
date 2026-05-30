package it.unifi.balpha.store;

import java.util.List;

public class InventoryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public InventoryService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        productRepository.save(product);
    }
    
    public void addProductToCategory(Product product, Long categoryId) {
        Category category = categoryRepository.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category with inside ID " + categoryId + " does not exist");
        }
        category.addProduct(product);
        categoryRepository.save(category);
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
}