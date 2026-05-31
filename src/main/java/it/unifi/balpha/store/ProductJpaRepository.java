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
        if (product.getId() == null) {
            em.persist(product);
        } else {
            em.merge(product);
        }
    }

    @Override
    public void deleteById(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }
}