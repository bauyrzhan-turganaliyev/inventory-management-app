package it.unifi.balpha.store;

import jakarta.persistence.EntityManager;

@FunctionalInterface
public interface TransactionWork<T> {
    T execute(EntityManager em);
}