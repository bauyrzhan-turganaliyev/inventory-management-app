package it.unifi.balpha.store;

import java.util.List;

public class InventoryService {
    private final JpaTransactionManager transactionManager;

    public InventoryService(JpaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Product getProductById(Long id) {
        return transactionManager.doInTransaction(em ->
            new ProductJpaRepository(em).findById(id)
        );
    }

    public List<Product> getAllProducts() {
        return transactionManager.doInTransaction(em ->
            new ProductJpaRepository(em).findAll()
        );
    }

    public List<Category> getAllCategories() {
        return transactionManager.doInTransaction(em ->
            new CategoryJpaRepository(em).findAll()
        );
    }

    public Product addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        return transactionManager.doInTransaction(em ->
            new ProductJpaRepository(em).save(product)
        );
    }

    public void deleteProduct(Long id) {
        if (id == null) throw new IllegalArgumentException("Product ID cannot be null");
        transactionManager.doInTransaction(em -> {
            new ProductJpaRepository(em).deleteById(id);
            return null;
        });
    }

    public void addProductToCategory(Product product, Long categoryId) {
        if (product == null) throw new IllegalArgumentException("Product cannot be null");
        if (categoryId == null) throw new IllegalArgumentException("Category ID cannot be null");
        transactionManager.doInTransaction(em -> {
            Category category = new CategoryJpaRepository(em).findById(categoryId);
            if (category == null)
                throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
            product.setCategory(category);
            new ProductJpaRepository(em).save(product);
            return null;
        });
    }
}