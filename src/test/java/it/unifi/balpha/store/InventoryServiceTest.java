package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private JpaTransactionManager transactionManager;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(transactionManager, productRepository, categoryRepository);
    }

    private <T> T executeTransaction(TransactionWork<T> work) {
        return work.execute(null);
    }

    @Test
    void testAddProductWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.addProduct(null));
    }

    @Test
    void testGetProductById() {
        Long id = 1L;
        Product expected = new Product("Mouse", 25.0);
        when(transactionManager.doInTransaction(any())).thenAnswer(inv -> executeTransaction(inv.getArgument(0)));
        when(productRepository.findById(id)).thenReturn(expected);

        assertEquals(expected, inventoryService.getProductById(id));
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(new Product("A", 1.0));
        when(transactionManager.doInTransaction(any())).thenAnswer(inv -> executeTransaction(inv.getArgument(0)));
        when(productRepository.findAll()).thenReturn(products);

        assertEquals(products, inventoryService.getAllProducts());
    }

    @Test
    void testAddProductToCategorySuccess() {
        Product product = mock(Product.class);
        Long catId = 1L;
        Category category = new Category("Electronics");

        when(transactionManager.doInTransaction(any())).thenAnswer(inv -> executeTransaction(inv.getArgument(0)));
        when(categoryRepository.findById(catId)).thenReturn(category);

        inventoryService.addProductToCategory(product, catId);

        verify(product).setCategory(category);
        verify(productRepository).save(null, product);
    }

    @Test
    void testAddProductSuccess() {
        Product product = new Product("Test Product", 10.0);

        when(transactionManager.doInTransaction(any())).thenAnswer(inv -> executeTransaction(inv.getArgument(0)));

        inventoryService.addProduct(product);

        verify(productRepository).save(null, product);
    }

    @Test
    void testAddProductToCategoryWhenCategoryNotFoundShouldThrow() {
        Product product = new Product("Mouse", 25.0);
        when(transactionManager.doInTransaction(any())).thenAnswer(inv -> executeTransaction(inv.getArgument(0)));
        when(categoryRepository.findById(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> inventoryService.addProductToCategory(product, 1L));
    }

    @Test
    void testDeleteProductSuccess() {
        when(transactionManager.doInTransaction(any())).thenAnswer(inv -> executeTransaction(inv.getArgument(0)));

        inventoryService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }
    
    @Test
    void testGetAllCategories() {
        List<Category> categories = List.of(new Category("Electronics"));
        when(transactionManager.doInTransaction(any())).thenAnswer(inv -> executeTransaction(inv.getArgument(0)));
        when(categoryRepository.findAll()).thenReturn(categories);
        
        List<Category> result = inventoryService.getAllCategories();
        assertEquals(categories, result);
    }
    
    @Test
    void testDeleteProductWithNullIdShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.deleteProduct(null));
    }
    
    @Test
    void testAddNullProduct() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.addProductToCategory(null, null));
    }
    
    @Test
    void testAddProductToNullCategory() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.addProductToCategory(new Product("Test", 5.50), null));
    }
}