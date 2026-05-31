package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductRepositoryIT {
	private static EntityManagerFactory emf;
    private EntityManager em;
    private ProductJpaRepository repository;

    @BeforeAll
    static void setupFactory() {
        emf = Persistence.createEntityManagerFactory("inventory-pu");
    }

    @AfterAll
    static void closeFactory() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        repository = new ProductJpaRepository(em);
        
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        em.close();
    }

    @Test
    void testSaveAndFindById() {
        Product product = new Product("Integration Test Keyboard", 120.00);
        
        repository.save(product);
        
        em.flush();
        em.clear();

        Product found = repository.findById(product.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Integration Test Keyboard");
        assertThat(found.getPrice()).isEqualTo(120.00);
    }

    @Test
    void testDeleteById() {
        Product product = new Product("To Be Deleted", 10.00);
        repository.save(product);
        
        em.flush();
        em.clear();

        repository.deleteById(product.getId());
        
        Product found = repository.findById(product.getId());
        assertThat(found).isNull();
    }
}