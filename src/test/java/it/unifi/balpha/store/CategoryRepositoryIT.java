package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterAll;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryRepositoryIT {
	private static EntityManagerFactory emf;
    private EntityManager em;
    private CategoryJpaRepository repository;

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
        repository = new CategoryJpaRepository(em);
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
        Category category = new Category("Groceries");
        repository.save(category);
        
        em.flush();
        em.clear();

        Category found = repository.findById(category.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Groceries");
    }

    @Test
    void testFindAll() {
        Category cat1 = new Category("Books");
        Category cat2 = new Category("Clothing");
        repository.save(cat1);
        repository.save(cat2);

        em.flush();
        em.clear();

        List<Category> categories = repository.findAll();
        assertThat(categories)
            .extracting(Category::getName)
            .contains("Books", "Clothing");
    }
}