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
}