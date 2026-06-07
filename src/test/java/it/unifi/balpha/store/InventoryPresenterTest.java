package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryPresenterTest {

    @Mock private InventoryService inventoryService;
    @Mock private InventoryView mockView;
    @InjectMocks private InventoryPresenterImpl presenter;

    @Test
    void testInitializeWithNullViewDoesNotCallService() {
        InventoryPresenterImpl presenterNullView = new InventoryPresenterImpl(null, inventoryService);
        presenterNullView.initialize();
        verifyNoInteractions(inventoryService);
    }

    @Test
    void testInitializeShouldPopulateCategoriesAndProductsOnView() {
        List<Category> categories = Arrays.asList(new Category("Electronics"));
        List<Product> products = Arrays.asList(new Product("Mouse", 25.0));
        when(inventoryService.getAllCategories()).thenReturn(categories);
        when(inventoryService.getAllProducts()).thenReturn(products);

        presenter.initialize();

        verify(mockView).showCategories(categories);
        verify(mockView).showProducts(products);
    }

    @Test
    void testAddProductWithCategoryShouldCallAddProductToCategory() {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L);

        presenter.addProduct("Apple", 1.50, category);

        verify(inventoryService).addProductToCategory(any(Product.class), eq(1L));
    }

    @Test
    void testAddProductWithCategoryNullIdShouldCallAddProduct() {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(null);

        presenter.addProduct("Name", 10.0, category);

        verify(inventoryService).addProduct(any(Product.class));
    }

    @Test
    void testAddProductWithNullCategoryShouldCallAddProduct() {
        presenter.addProduct("Name", 10.0, null);

        verify(inventoryService).addProduct(any(Product.class));
    }

    @Test
    void testAddProductShouldRefreshViewProductList() {
        List<Product> updatedList = Arrays.asList(new Product("Apple", 1.50));
        when(inventoryService.getAllProducts()).thenReturn(updatedList);
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L);

        presenter.addProduct("Apple", 1.50, category);

        verify(mockView).showProducts(updatedList);
    }

    @Test
    void testDeleteProductShouldCallServiceAndRefreshView() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(42L);
        List<Product> remaining = Collections.emptyList();
        when(inventoryService.getAllProducts()).thenReturn(remaining);

        presenter.deleteProduct(product);

        verify(inventoryService).deleteProduct(42L);
        verify(mockView).showProducts(remaining);
    }

    @Test
    void testDeleteProductWithNullIdShouldThrow() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> presenter.deleteProduct(product));
    }

    @Test
    void testDeleteNullProductShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> presenter.deleteProduct(null));

        verifyNoInteractions(inventoryService);
    }
}