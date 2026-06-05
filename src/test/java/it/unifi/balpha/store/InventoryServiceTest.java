package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
    private EntityManager em;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(transactionManager);
    }

    private void stubTransaction() {
        when(transactionManager.doInTransaction(any()))
            .thenAnswer(inv -> ((TransactionWork<?>) inv.getArgument(0)).execute(em));
    }

    // --- null guard clauses (no transaction involved) ---

    @Test
    void testAddProductWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.addProduct(null));
    }

    @Test
    void testDeleteProductWithNullIdShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> inventoryService.deleteProduct(null));
    }

    @Test
    void testAddProductToCategoryWithNullProductShouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.addProductToCategory(null, 1L));
    }

    @Test
    void testAddProductToCategoryWithNullCategoryIdShouldThrow() {
        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.addProductToCategory(new Product("Test", 5.50), null));
    }

    // --- getProductById ---

    @Test
    void testGetProductByIdReturnsProduct() {
        stubTransaction();
        Product expected = new Product("Mouse", 25.0);
        when(em.find(Product.class, 1L)).thenReturn(expected);

        assertThat(inventoryService.getProductById(1L)).isEqualTo(expected);
    }

    @Test
    void testGetProductByIdReturnsNullWhenNotFound() {
        stubTransaction();
        when(em.find(Product.class, 99L)).thenReturn(null);

        assertThat(inventoryService.getProductById(99L)).isNull();
    }

    // --- getAllProducts ---

    @Test
    void testGetAllProductsReturnsProducts() {
        stubTransaction();
        List<Product> products = List.of(new Product("A", 1.0));
        TypedQuery<Product> query = mock(TypedQuery.class);
        when(em.createQuery("FROM Product", Product.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(products);

        assertThat(inventoryService.getAllProducts()).isEqualTo(products);
    }

    // --- getAllCategories ---

    @Test
    void testGetAllCategoriesReturnsCategories() {
        stubTransaction();
        List<Category> categories = List.of(new Category("Electronics"));
        TypedQuery<Category> query = mock(TypedQuery.class);
        when(em.createQuery("FROM Category", Category.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(categories);

        assertThat(inventoryService.getAllCategories()).isEqualTo(categories);
    }

    // --- addProduct ---

    @Test
    void testAddProductReturnsPersistedProduct() {
        stubTransaction();
        Product product = new Product("Keyboard", 45.0);

        Product result = inventoryService.addProduct(product);

        verify(em).persist(product);
        verify(em).flush();
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(product);
    }

    // --- deleteProduct ---

    @Test
    void testDeleteProductCallsRemove() {
        stubTransaction();
        Product product = new Product("ToDelete", 10.0);
        when(em.find(Product.class, 1L)).thenReturn(product);

        inventoryService.deleteProduct(1L);

        verify(em).remove(product);
    }

    // --- addProductToCategory ---

    @Test
    void testAddProductToCategorySetsCategory() {
        stubTransaction();
        Product product = new Product("Smartphone", 800.0);
        Category category = new Category("Gadgets");
        when(em.find(Category.class, 1L)).thenReturn(category);

        inventoryService.addProductToCategory(product, 1L);

        assertThat(product.getCategory()).isEqualTo(category);
        verify(em).persist(product);
        verify(em).flush();
    }

    @Test
    void testAddProductToCategoryThrowsWhenCategoryNotFound() {
        stubTransaction();
        when(em.find(Category.class, 999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
            () -> inventoryService.addProductToCategory(new Product("Mouse", 25.0), 999L));
    }
}