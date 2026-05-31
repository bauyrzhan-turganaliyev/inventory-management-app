package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private JpaTransactionManager transactionManager;

    private EntityManager em;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(transactionManager);
        em = mock(EntityManager.class);
    }

    private void setupActiveTransaction() {
        when(transactionManager.doInTransaction(any())).thenAnswer(invocation -> {
            JpaTransactionCode<?> code = invocation.getArgument(0);
            return code.execute(em);
        });
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
        setupActiveTransaction();
        Product product = new Product("Mouse", 25.0);
        Long nonExistingCategoryId = 99L;

        when(em.find(Category.class, nonExistingCategoryId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> 
            inventoryService.addProductToCategory(product, nonExistingCategoryId)
        );
    }


    @Test
    void testGetAllProductsSuccessfully() {
        setupActiveTransaction();
        Product product = new Product("Laptop", 1200.0);
        List<Product> expectedProducts = Arrays.asList(product);
        
        TypedQuery<Product> query = mock(TypedQuery.class);
 
        when(em.createQuery("FROM Product", Product.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedProducts);

        List<Product> result = inventoryService.getAllProducts();

        assertThat(result).containsExactly(product);
    }

    @Test
    void testGetAllCategoriesSuccessfully() {
        setupActiveTransaction();
        Category category = new Category("Electronics");
        List<Category> expectedCategories = Arrays.asList(category);

        TypedQuery<Category> query = mock(TypedQuery.class);
        when(em.createQuery("FROM Category", Category.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedCategories);

        List<Category> result = inventoryService.getAllCategories();

        assertThat(result).containsExactly(category);
    }

    @Test
    void testAddProductSuccessfully() {
        setupActiveTransaction();
        Product product = new Product("Keyboard", 45.0);

        inventoryService.addProduct(product);

        verify(em).persist(product);
    }

    @Test
    void testDeleteProductSuccessfully() {
        setupActiveTransaction();
        Long productId = 1L;
        Product product = new Product("To Delete", 10.0);
        when(em.find(Product.class, productId)).thenReturn(product);

        inventoryService.deleteProduct(productId);

        verify(em).remove(product);
    }

    @Test
    void testAddProductToCategorySuccessfully() {
        setupActiveTransaction();
        Product product = spy(new Product("Smartphone", 800.0));
        Category category = new Category("Gadgets");
        Long categoryId = 5L;

        when(em.find(Category.class, categoryId)).thenReturn(category);

        inventoryService.addProductToCategory(product, categoryId);

        verify(product).setCategory(category);
        verify(em).persist(product);
    }
}