package it.unifi.balpha.store;

import java.util.List;

public class InventoryService {
    private final JpaTransactionManager transactionManager;

    public InventoryService(JpaTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public List<Product> getAllProducts() {
        return transactionManager.doInTransaction(em -> {
            ProductJpaRepository productRepo = new ProductJpaRepository(em);
            return productRepo.findAll();
        });
    }

    public List<Category> getAllCategories() {
        return transactionManager.doInTransaction(em -> {
            CategoryJpaRepository categoryRepo = new CategoryJpaRepository(em);
            return categoryRepo.findAll();
        });
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        transactionManager.doInTransaction(em -> {
            ProductJpaRepository productRepo = new ProductJpaRepository(em);
            productRepo.save(product);
            return null;
        });
    }

    public void deleteProduct(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        transactionManager.doInTransaction(em -> {
            ProductJpaRepository productRepo = new ProductJpaRepository(em);
            productRepo.deleteById(id);
            return null;
        });
    }

    public void addProductToCategory(Product product, Long categoryId) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        transactionManager.doInTransaction(em -> {
            CategoryJpaRepository categoryRepo = new CategoryJpaRepository(em);
            ProductJpaRepository productRepo = new ProductJpaRepository(em);

            Category category = categoryRepo.findById(categoryId);
            if (category != null) {
                product.setCategory(category);
                productRepo.save(product);
            } else {
                throw new IllegalArgumentException("Category with ID " + categoryId + " not found");
            }
            return null;
        });
    }
}