package it.unifi.balpha.store;

import jakarta.persistence.EntityManager;

public class ProductJpaRepository implements ProductRepository {

    private final EntityManager em;

    public ProductJpaRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Product product) {
        em.persist(product);
    }

    @Override
    public Product findById(Long id) {
        return em.find(Product.class, id);
    }
}