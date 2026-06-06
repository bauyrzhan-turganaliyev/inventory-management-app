package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class InventoryServiceIT {
    @SuppressWarnings("resource") // lifecycle managed by @Container
    private static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    private static EntityManagerFactory emf;
    private JpaTransactionManager transactionManager;
    private InventoryService inventoryService;
    
    @BeforeAll
    static void beforeAll() {
    	// Override Docker API version: Testcontainers bundles an older client (1.32),
    	// but Docker Desktop requires minimum 1.40. This property forces the correct version.
    	System.setProperty("api.version", "1.44");
    	
        postgres.start();
        Map<String, String> configOverrides = new HashMap<>();
        configOverrides.put("jakarta.persistence.jdbc.url", postgres.getJdbcUrl());
        configOverrides.put("jakarta.persistence.jdbc.user", postgres.getUsername());
        configOverrides.put("jakarta.persistence.jdbc.password", postgres.getPassword());
        configOverrides.put("jakarta.persistence.jdbc.driver", postgres.getDriverClassName());
        configOverrides.put("hibernate.hbm2ddl.auto", "create-drop");

        emf = Persistence.createEntityManagerFactory("inventory-pu", configOverrides);
    }

    @AfterAll
    static void afterAll() {
        if (emf != null) emf.close();
        if (postgres != null) postgres.stop();
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
    void testAddProductSuccessfully() {
        Product product = new Product("Keyboard", 45.0);
        Product savedProduct = inventoryService.addProduct(product);
        
        assertNotNull(savedProduct, "The returned product should not be null");
        
        assertThat(savedProduct.getId()).isNotNull(); 
    }
    
    @Test
    void testGetAllProductsSuccessfully() {
        transactionManager.doInTransaction(em -> {
            em.persist(new Product("Laptop", 1200.0));
            return null;
        });

        List<Product> result = inventoryService.getAllProducts();
        assertThat(result).extracting(Product::getName).containsExactly("Laptop");
    }

    @Test
    void testDeleteProductSuccessfully() {
        Product product = new Product("To Delete", 10.0);
        transactionManager.doInTransaction(em -> {
            em.persist(product);
            return null;
        });

        inventoryService.deleteProduct(product.getId());

        Product deletedProduct = inventoryService.getProductById(product.getId());
        assertThat(deletedProduct).isNull();
    }

    @Test
    void testAddProductToCategorySuccessfully() {
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
    void testAddProductToCategoryThrowsWhenCategoryDoesNotExist() {
        Product product = new Product("Mouse", 25.0);
        transactionManager.doInTransaction(em -> {
            em.persist(product);
            return null;
        });

        assertThrows(IllegalArgumentException.class, () -> 
            inventoryService.addProductToCategory(product, 999L)
        );
    }
    
    @Test
    void testGetAllCategoriesSuccessfully() {
        transactionManager.doInTransaction(em -> {
            em.persist(new Category("Electronics"));
            return null;
        });

        List<Category> result = inventoryService.getAllCategories();
        
        assertThat(result).extracting(Category::getName).containsExactly("Electronics");
    }
    
    @Test
    void testGetProductByIdSuccessfully() {
        Product product = new Product("Smartphone", 800.0);
        transactionManager.doInTransaction(em -> {
            em.persist(product);
            return null;
        });
        
        Product found = inventoryService.getProductById(product.getId());
        
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Smartphone");
    }


}