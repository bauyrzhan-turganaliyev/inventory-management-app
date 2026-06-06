package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class CategoryJpaRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private CategoryJpaRepository repository;

    @BeforeAll
    static void setUpFactory() {
        emf = Persistence.createEntityManagerFactory("inventory-pu", Map.of(
            "jakarta.persistence.jdbc.url",      "jdbc:h2:mem:categorydb;DB_CLOSE_DELAY=-1",
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
        em = emf.createEntityManager();
        repository = new CategoryJpaRepository(em);
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Product").executeUpdate();
        em.createQuery("DELETE FROM Category").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em != null) em.close();
    }

    @Test
    void testSaveNewCategoryPersistsAndAssignsId() {
        Category category = new Category("Test");

        em.getTransaction().begin();
        repository.save(category);
        em.getTransaction().commit();

        assertThat(category.getId()).isNotNull();
        assertThat(em.find(Category.class, category.getId())).isNotNull();
    }

    @Test
    void testSaveExistingCategoryMergesAndRemainsFound() {
        Category category = new Category("Electronics");
        em.getTransaction().begin();
        em.persist(category);
        em.flush();
        em.getTransaction().commit();

        em.getTransaction().begin();
        repository.save(category);
        em.getTransaction().commit();

        assertThat(em.find(Category.class, category.getId())).isNotNull();
    }

    @Test
    void testFindByIdReturnsCategory() {
        Category category = new Category("Electronics");
        em.getTransaction().begin();
        em.persist(category);
        em.flush();
        em.getTransaction().commit();

        assertThat(repository.findById(category.getId())).isNotNull();
        assertThat(repository.findById(category.getId()).getName()).isEqualTo("Electronics");
    }

    @Test
    void testFindByIdReturnsNullWhenNotFound() {
        assertThat(repository.findById(999L)).isNull();
    }

    @Test
    void testFindAllReturnsAllCategories() {
        em.getTransaction().begin();
        em.persist(new Category("Books"));
        em.persist(new Category("Electronics"));
        em.getTransaction().commit();

        List<Category> result = repository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Category::getName)
            .containsExactlyInAnyOrder("Books", "Electronics");
    }
}