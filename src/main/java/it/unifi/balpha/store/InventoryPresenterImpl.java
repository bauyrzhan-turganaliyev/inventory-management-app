package it.unifi.balpha.store;

public class InventoryPresenterImpl implements InventoryPresenter {

    private final InventoryService inventoryService;
    private final InventoryView view;

    public InventoryPresenterImpl(InventoryView view, InventoryService inventoryService) {
        this.view = view;
        this.inventoryService = inventoryService;
    }

    @Override
    public void initialize() {
        if (view != null) {
            view.showCategories(inventoryService.getAllCategories());
            view.showProducts(inventoryService.getAllProducts());
        }
    }
    
    @Override
    public void addProduct(String name, double price, Category category) {
        Product product = new Product(name, price);
        
        if (category != null && category.getId() != null) {
            inventoryService.addProductToCategory(product, category.getId());
        } else {
            inventoryService.addProduct(product);
        }
        
        if (view != null) {
            view.showProducts(inventoryService.getAllProducts());
        }
    }
    
    @Override
    public void deleteProduct(Product product) {
        if (product != null && product.getId() != null) {
            inventoryService.deleteProduct(product.getId());
            if (view != null) {
                view.showProducts(inventoryService.getAllProducts());
            }
        } else {
            System.out.println("Error: Try to delete a product without id or null!");
        }
    }
}