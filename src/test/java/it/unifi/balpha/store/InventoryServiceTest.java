package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import jakarta.persistence.EntityManager;
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

    @Test
    void testAddProductToCategoryThrowsWhenCategoryDoesNotExist() {
        Product product = new Product("Mouse", 25.0);
        Long nonExistingCategoryId = 99L;

        EntityManager em = mock(EntityManager.class);
        when(transactionManager.doInTransaction(any())).thenAnswer(invocation -> {
            JpaTransactionCode<?> code = invocation.getArgument(0);
            return code.execute(em);
        });

        when(em.find(Category.class, nonExistingCategoryId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> 
            inventoryService.addProductToCategory(product, nonExistingCategoryId)
        );
    }
}