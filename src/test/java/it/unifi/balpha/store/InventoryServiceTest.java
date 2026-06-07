package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class InventoryServiceTest {

    private static EntityManagerFactory emf;
    private JpaTransactionManager transactionManager;
    private InventoryService inventoryService;

    @BeforeAll
    static void setUpFactory() {
        emf = Persistence.createEntityManagerFactory("inventory-pu", Map.of(
            "jakarta.persistence.jdbc.url",      "jdbc:h2:mem:servicedb;DB_CLOSE_DELAY=-1",
            "jakarta.persistence.jdbc.driver",   "org.h2.Driver",
            "jakarta.persistence.jdbc.user",     "sa",
            "jakarta.persistence.jdbc.password", "",
            "hibernate.hbm2ddl.auto",            "create-drop",
            "hibernate.dialect",                 "org.hibernate.dialect.H2Dialect"
        ));
    }

    @AfterAll
    static void tearDownFactory() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void setUp() {
        transactionManager = new JpaTransactionManager(emf);
        inventoryService = new InventoryService(transactionManager);
        transactionManager.doInTransaction(em -> {
            em.createQuery("DELETE FROM Product").executeUpdate();
            em.createQuery("DELETE FROM Category").executeUpdate();
            return null;
        });
    }

    @Test
    void testAddProductWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.addProduct(null));
    }

    @Test
    void testDeleteProductWithNullIdShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.deleteProduct(null));
    }

    @Test
    void testAddProductToCategoryWithNullProductShouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.addProductToCategory(null, 1L));
    }

    @Test
    void testAddProductToCategoryWithNullCategoryIdShouldThrow() {
        Product product = new Product("Test", 5.50);
        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.addProductToCategory(product, null));
    }

    @Test
    void testGetProductByIdReturnsProduct() {
        Product product = new Product("Mouse", 25.0);
        transactionManager.doInTransaction(em -> {
            em.persist(product);
            return null;
        });

        assertThat(inventoryService.getProductById(product.getId())).isNotNull();
    }

    @Test
    void testGetProductByIdReturnsNullWhenNotFound() {
        assertThat(inventoryService.getProductById(999L)).isNull();
    }

    @Test
    void testGetAllProductsReturnsProducts() {
        transactionManager.doInTransaction(em -> { 
        	em.persist(new Product("A", 1.0)); 
        	return null; 
        });

        List<Product> result = inventoryService.getAllProducts();
        assertThat(result).extracting(Product::getName).containsExactly("A");
    }

    @Test
    void testGetAllCategoriesReturnsCategories() {
        transactionManager.doInTransaction(em -> { 
        	em.persist(new Category("Electronics")); 
        	return null; 
        });

        List<Category> result = inventoryService.getAllCategories();
        assertThat(result).extracting(Category::getName).containsExactly("Electronics");
    }

    @Test
    void testAddProductReturnsPersistedProduct() {
        Product product = new Product("Keyboard", 45.0);

        Product result = inventoryService.addProduct(product);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
    }

    @Test
    void testDeleteProductRemovesIt() {
        Product product = new Product("ToDelete", 10.0);
        transactionManager.doInTransaction(em -> { 
        	em.persist(product); 
        	return null; 
        });

        inventoryService.deleteProduct(product.getId());

        assertThat(inventoryService.getProductById(product.getId())).isNull();
    }

    @Test
    void testAddProductToCategorySetsCategory() {
        Product product = new Product("Smartphone", 800.0);
        Category category = new Category("Gadgets");
        transactionManager.doInTransaction(em -> {
            em.persist(product);
            em.persist(category);
            return null;
        });

        inventoryService.addProductToCategory(product, category.getId());

        Product updated = inventoryService.getProductById(product.getId());
        assertThat(updated.getCategory()).isNotNull();
        assertThat(updated.getCategory().getName()).isEqualTo("Gadgets");
    }

    @Test
    void testAddProductToCategoryThrowsWhenCategoryNotFound() {
        Product product = new Product("Mouse", 25.0);
        transactionManager.doInTransaction(em -> { 
        	em.persist(product); 
        	return null; 
        });

        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.addProductToCategory(product, 999L));
    }
}