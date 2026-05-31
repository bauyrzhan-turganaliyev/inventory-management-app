package it.unifi.balpha.store;

import jakarta.persistence.EntityManager;

@FunctionalInterface
public interface JpaTransactionCode<T> {
    T execute(EntityManager em);
}