package it.unifi.balpha.store;

public interface TransactionManager {
    <T> T doInTransaction(TransactionWork<T> work);
}
