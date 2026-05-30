package it.unifi.balpha.store;

public class InventoryPresenterImpl implements InventoryPresenter {

    private final InventoryService inventoryService;

    public InventoryPresenterImpl(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void addProduct(String name, double price, Category category) {
        Product product = new Product(name, price);
        
        if (category != null && category.getId() != null) {
            inventoryService.addProductToCategory(product, category.getId());
        } else {
            inventoryService.addProduct(product);
        }
    }
}