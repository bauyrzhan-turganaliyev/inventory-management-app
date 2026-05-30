package it.unifi.balpha.store;

public class InventoryPresenterImpl implements InventoryPresenter {

    private final InventoryService inventoryService;

    public InventoryPresenterImpl(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void addProduct(String name, double price) {
        Product product = new Product(name, price);
        
        inventoryService.addProduct(product);
    }
}