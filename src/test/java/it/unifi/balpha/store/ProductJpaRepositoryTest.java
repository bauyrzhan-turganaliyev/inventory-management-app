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

class ProductJpaRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private ProductJpaRepository repository;

    @BeforeAll
    static void setUpFactory() {
        emf = Persistence.createEntityManagerFactory("inventory-pu", Map.of(
            "jakarta.persistence.jdbc.url",      "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
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
        repository = new ProductJpaRepository(em);
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
    void testFindByIdReturnsProduct() {
        Product p = new Product("Test", 1.0);
        em.getTransaction().begin();
        em.persist(p);
        em.flush();
        em.getTransaction().commit();

        Product found = repository.findById(p.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test");
    }

    @Test
    void testFindByIdReturnsNullWhenNotFound() {
        assertThat(repository.findById(999L)).isNull();
    }

    @Test
    void testFindAllReturnsAllProducts() {
        em.getTransaction().begin();
        em.persist(new Product("A", 1.0));
        em.persist(new Product("B", 2.0));
        em.getTransaction().commit();

        List<Product> result = repository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Product::getName).containsExactlyInAnyOrder("A", "B");
    }

    @Test
    void testSaveNewProductPersistsAndAssignsId() {
        Product p = new Product("New Product", 10.0);

        em.getTransaction().begin();
        repository.save(p);
        em.getTransaction().commit();

        assertThat(p.getId()).isNotNull();
        assertThat(em.find(Product.class, p.getId())).isNotNull();
    }

    @Test
    void testSaveExistingProductMergesAndRemainsFound() {
        Product p = new Product("Original", 10.0);
        em.getTransaction().begin();
        em.persist(p);
        em.flush();
        em.getTransaction().commit();

        em.getTransaction().begin();
        repository.save(p);
        em.getTransaction().commit();

        assertThat(em.find(Product.class, p.getId())).isNotNull();
    }

    @Test
    void testDeleteByIdRemovesProduct() {
        Product p = new Product("DeleteMe", 1.0);
        em.getTransaction().begin();
        em.persist(p);
        em.flush();
        em.getTransaction().commit();

        em.getTransaction().begin();
        repository.deleteById(p.getId());
        em.getTransaction().commit();

        assertThat(em.find(Product.class, p.getId())).isNull();
    }

    @Test
    void testDeleteByIdDoesNothingWhenNotFound() {
        em.getTransaction().begin();
        repository.deleteById(999L);
        em.getTransaction().commit();
    }
    
    @Test
    void testSaveNewProductAssignsIdAfterFlush() {
        Product p = new Product("Flush Test", 5.0);

        em.getTransaction().begin();
        repository.save(p);
        em.getTransaction().commit();

        EntityManager em2 = emf.createEntityManager();
        try {
            assertThat(em2.find(Product.class, p.getId())).isNotNull();
        } finally {
            em2.close();
        }
    }
    
    @Test
    void testSaveNewProductReturnsPersistedProduct() {
        Product p = new Product("Return Test", 5.0);

        em.getTransaction().begin();
        Product returned = repository.save(p);
        em.getTransaction().commit();

        assertThat(returned).isNotNull();
        assertThat(returned).isSameAs(p);
    }
    
    @Test
    void testSaveExistingProductReturnsMergedProduct() {
        Product p = new Product("Merge Return Test", 10.0);
        em.getTransaction().begin();
        em.persist(p);
        em.flush();
        em.getTransaction().commit();

        em.getTransaction().begin();
        Product returned = repository.save(p);
        em.getTransaction().commit();

        assertThat(returned).isNotNull();
    }
}