package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductRepositoryIT {
    private static EntityManagerFactory emf;
    private TransactionManager transactionManager;

    @BeforeAll
    static void setupFactory() {
        emf = Persistence.createEntityManagerFactory("inventory-pu");
    }

    @AfterAll
    static void closeFactory() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void setUp() {
        transactionManager = new JpaTransactionManager(emf);
    }

    
    @Test
    void testSaveProduct() {
        Product product = new Product("New Product", 10.0);
        transactionManager.doInTransaction(em -> {
            new ProductJpaRepository(em).save(product);
            return null;
        });
        Product found = transactionManager.doInTransaction(em ->
            em.find(Product.class, product.getId())
        );
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("New Product");
    }

    @Test
    void testDeleteById() {
        Product product = new Product("To Be Deleted", 10.00);
        transactionManager.doInTransaction(em -> {
            new ProductJpaRepository(em).save(product);
            return null;
        });
        transactionManager.doInTransaction(em -> {
            new ProductJpaRepository(em).deleteById(product.getId());
            return null;
        });
        Product found = transactionManager.doInTransaction(em ->
            em.find(Product.class, product.getId())
        );
        assertThat(found).isNull();
    }
}