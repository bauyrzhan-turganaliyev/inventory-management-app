package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(productRepository, categoryRepository);
    }

    @Test
    void testAddProductCallsRepository() {
        Product product = new Product("Keyboard", 50.0);

        inventoryService.addProduct(product);

        verify(productRepository).save(product);
    }
    
    @Test
    void testAddProductToCategoryThrowsWhenCategoryDoesNotExist() {
        Product product = new Product("Mouse", 25.0);
        Long nonExistingCategoryId = 99L;

        when(categoryRepository.findById(nonExistingCategoryId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> 
            inventoryService.addProductToCategory(product, nonExistingCategoryId)
        );
    }
    
    @Test
    void testGetAllProductsReturnsList() {
        java.util.List<Product> expectedProducts = java.util.Arrays.asList(
            new Product("Keyboard", 50.0),
            new Product("Mouse", 25.0)
        );

        when(productRepository.findAll()).thenReturn(expectedProducts);

        java.util.List<Product> actualProducts = inventoryService.getAllProducts();

        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).findAll();
    }

    @Test
    void testDeleteProductCallsRepository() {
        Long productId = 1L;

        inventoryService.deleteProduct(productId);

        verify(productRepository).deleteById(productId);
    }
}