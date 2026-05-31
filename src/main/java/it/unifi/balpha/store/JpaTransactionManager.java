package it.unifi.balpha.store;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class JpaTransactionManager {
    private final EntityManagerFactory emf;

    public JpaTransactionManager(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public <T> T doInTransaction(JpaTransactionCode<T> code) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T result = code.execute(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}