package it.unifi.balpha.store;

import jakarta.persistence.EntityManager;

public class CategoryJpaRepository implements CategoryRepository {

    private final EntityManager em;

    public CategoryJpaRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Category category) {
        em.persist(category);
    }

    @Override
    public Category findById(Long id) {
        return em.find(Category.class, id);
    }
}