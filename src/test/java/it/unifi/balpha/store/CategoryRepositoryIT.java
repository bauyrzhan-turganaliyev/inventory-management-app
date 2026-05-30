package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryRepositoryIT {
    private EntityManagerFactory emf;
    private EntityManager em;
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("inventory-pu");
        em = emf.createEntityManager();
        
        categoryRepository = new CategoryJpaRepository(em);
        
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
    void testSaveAndFindCategoryWithProducts() {
        Category category = new Category("Electronics");
        Product laptop = new Product("Laptop", 1200.0);
        category.addProduct(laptop);

        categoryRepository.save(category);

        em.getTransaction().commit();
        em.clear();

        Category foundCategory = categoryRepository.findById(category.getId());

        assertNotNull(foundCategory);
        assertEquals("Electronics", foundCategory.getName());
        
        assertEquals(1, foundCategory.getProducts().size());
        assertEquals("Laptop", foundCategory.getProducts().get(0).getName());
    }
}