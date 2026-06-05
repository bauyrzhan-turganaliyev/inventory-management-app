package it.unifi.balpha.store;

import java.util.List;

public class InventoryService {
    private final JpaTransactionManager transactionManager;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public InventoryService(JpaTransactionManager transactionManager, 
                            ProductRepository productRepository, 
                            CategoryRepository categoryRepository) {
        this.transactionManager = transactionManager;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product getProductById(Long id) {
        return transactionManager.doInTransaction(em -> productRepository.findById(id));
    }
    
    public List<Product> getAllProducts() {
        return transactionManager.doInTransaction(em -> productRepository.findAll());
    }

    public List<Category> getAllCategories() {
        return transactionManager.doInTransaction(em -> categoryRepository.findAll());
    }

    public Product addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");

        return transactionManager.doInTransaction(em -> {
            return productRepository.save(em, product);
        });
    }

    public void addProductToCategory(Product product, Long categoryId) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        if (categoryId == null) throw new IllegalArgumentException("Category ID cannot be null");

        transactionManager.doInTransaction(em -> {
            Category category = categoryRepository.findById(categoryId);
            if (category != null) {
                product.setCategory(category);
                productRepository.save(em, product);
            } else {
                throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
            }
            return null;
        });
    }

    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        transactionManager.doInTransaction(em -> {
            productRepository.deleteById(id);
            return null;
        });
    }
}