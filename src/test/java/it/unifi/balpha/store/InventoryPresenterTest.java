package it.unifi.balpha.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

class InventoryPresenterTest {

    private InventoryService inventoryService;
    private InventoryPresenterImpl presenter;

    @BeforeEach
    void setUp() {
        inventoryService = Mockito.mock(InventoryService.class);
        
        presenter = new InventoryPresenterImpl(inventoryService);
    }

    @Test
    void testAddProductShouldCallServiceToSave() {
        presenter.addProduct("Apple", 1.50);

        Product expectedProduct = new Product("Apple", 1.50);
        verify(inventoryService).addProduct(Mockito.refEq(expectedProduct));
    }
}