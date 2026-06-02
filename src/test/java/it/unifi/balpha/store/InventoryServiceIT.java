package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class InventoryServiceIT {
    private static PostgreSQLContainer<?> postgres;
    private static EntityManagerFactory emf;
    private EntityManager em;
    private JpaTransactionManager transactionManager;
    private InventoryService inventoryService;
    
    @BeforeAll
    static void beforeAll() {
    	System.setProperty("api.version", "1.44");
    	
        postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
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
        em = emf.createEntityManager();
        transactionManager = new JpaTransactionManager(emf);
        inventoryService = new InventoryService(transactionManager);
        
        transactionManager.doInTransaction(em -> {
            em.createQuery("DELETE FROM Product").executeUpdate();
            em.createQuery("DELETE FROM Category").executeUpdate();
            return null;
        });
    }

    @AfterEach
    void tearDown() {
        if (em != null) em.close();
    }

    @Test
    void testAddProductSuccessfully() {
        Product product = new Product("Keyboard", 45.0);
        inventoryService.addProduct(product);

        List<Product> products = inventoryService.getAllProducts();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Keyboard");
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

        assertThat(inventoryService.getAllProducts()).isEmpty();
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
        
        Product updatedProduct = inventoryService.getProductById(product.getId());
        
        assertThat(updatedProduct.getCategory()).isNotNull();
        assertThat(updatedProduct.getCategory().getName()).isEqualTo("Gadgets");
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
}