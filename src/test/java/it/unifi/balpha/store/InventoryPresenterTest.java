package it.unifi.balpha.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryPresenterTest {

    private InventoryService inventoryService;
    private InventoryPresenterImpl presenter;

    @BeforeEach
    void setUp() {
        inventoryService = Mockito.mock(InventoryService.class);
        presenter = new InventoryPresenterImpl(inventoryService);
    }

    @Test
    void testAddProductShouldCallServiceToSaveWithCategory() {
        Category testCategory = Mockito.mock(Category.class);
        when(testCategory.getId()).thenReturn(1L);

        presenter.addProduct("Apple", 1.50, testCategory);

        Product expectedProduct = new Product("Apple", 1.50);
        verify(inventoryService).addProductToCategory(Mockito.refEq(expectedProduct), Mockito.eq(1L));
    }
    
    @Test
    void testAddProductShouldRefreshViewProductList() {
        InventoryView mockView = Mockito.mock(InventoryView.class);
        presenter.setView(mockView);

        java.util.List<Product> updatedList = java.util.Arrays.asList(new Product("Apple", 1.50));
        when(inventoryService.getAllProducts()).thenReturn(updatedList);

        Category testCategory = Mockito.mock(Category.class);
        when(testCategory.getId()).thenReturn(1L);
        presenter.addProduct("Apple", 1.50, testCategory);

        verify(inventoryService).getAllProducts();
        verify(mockView).showProducts(updatedList);
    }
    
    @Test
    void testDeleteProductShouldCallServiceAndRefreshView() {
        InventoryView mockView = Mockito.mock(InventoryView.class);
        presenter.setView(mockView);

        Product productToDelete = Mockito.mock(Product.class);
        when(productToDelete.getId()).thenReturn(42L);

        java.util.List<Product> remainingProducts = java.util.Arrays.asList(new Product[] {}); 
        when(inventoryService.getAllProducts()).thenReturn(remainingProducts);

        presenter.deleteProduct(productToDelete);

        verify(inventoryService).deleteProduct(42L);
        verify(mockView).showProducts(remainingProducts);
    }
}