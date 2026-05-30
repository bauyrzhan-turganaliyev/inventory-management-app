package it.unifi.balpha.store;

public interface InventoryPresenter {
    void addProduct(String name, double price, Category category);
    void deleteProduct(Product product);
}