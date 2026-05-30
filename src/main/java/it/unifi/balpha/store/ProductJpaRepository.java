package it.unifi.balpha.store;

import jakarta.persistence.EntityManager;
import java.util.List;

public class ProductJpaRepository implements ProductRepository {

    private final EntityManager em;

    public ProductJpaRepository(EntityManager em) {
        this.em = em;
    }


    @Override
    public Product findById(Long id) {
        return em.find(Product.class, id);
    }
    
    @Override
    public List<Product> findAll() {
        return em.createQuery("FROM Product", Product.class).getResultList();
    }

    @Override
    public void save(Product product) {
        boolean isTransactionActive = em.getTransaction().isActive();
        if (!isTransactionActive) {
            em.getTransaction().begin();
        }
        
        em.persist(product);
        
        if (!isTransactionActive) {
            em.getTransaction().commit();
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            return;
        }

        boolean isTransactionActive = em.getTransaction().isActive();
        if (!isTransactionActive) {
            em.getTransaction().begin();
        }

        try {
            em.createQuery("DELETE FROM Product p WHERE p.id = :id")
              .setParameter("id", id)
              .executeUpdate();

            em.clear(); 

            if (!isTransactionActive) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (!isTransactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }
}