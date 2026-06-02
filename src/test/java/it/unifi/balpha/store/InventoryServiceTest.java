package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private JpaTransactionManager transactionManager;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(transactionManager);
    }

    @Test
    void testAddProductWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> 
            inventoryService.addProduct(null)
        );
    }

    @Test
    void testDeleteProductWithNullIdShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> 
            inventoryService.deleteProduct(null)
        );
    }

    @Test
    void testAddProductToCategoryWithNullProductShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> 
            inventoryService.addProductToCategory(null, 1L)
        );
    }

    @Test
    void testAddProductToCategoryWithNullCategoryIdShouldThrow() {
        Product product = new Product("Mouse", 25.0);
        assertThrows(IllegalArgumentException.class, () -> 
            inventoryService.addProductToCategory(product, null)
        );
    }
}