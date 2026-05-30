package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductRepositoryTest {
    private EntityManagerFactory emf;
    private EntityManager em;

    private ProductRepository productRepository; 

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("inventory-pu");
        em = emf.createEntityManager();
        
        productRepository = new ProductJpaRepository(em);
        
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
        emf.close();
    }

    @Test
    void testSaveAndFindProduct() {
        Product product = new Product("Smartphone", 800.0);
        
        productRepository.save(product);
        
        em.getTransaction().commit();
        em.clear();

        Product foundProduct = productRepository.findById(product.getId());
        
        assertNotNull(foundProduct);
        assertEquals("Smartphone", foundProduct.getName());
        assertEquals(800.0, foundProduct.getPrice());
    }
}