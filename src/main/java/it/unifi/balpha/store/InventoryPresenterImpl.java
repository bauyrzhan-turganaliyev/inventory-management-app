package it.unifi.balpha.store;

public class InventoryPresenterImpl implements InventoryPresenter {

    private final InventoryService inventoryService;
    private InventoryView view;

    public InventoryPresenterImpl(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setView(InventoryView view) {
        this.view = view;
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
        }
    }
}