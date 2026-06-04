package it.unifi.balpha.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class InventoryPresenterTest {
    @Mock private InventoryService inventoryService;
    @Mock private InventoryView mockView;
    @InjectMocks private InventoryPresenterImpl presenter;

    @Test
    void testInitializeWithNullView() {
        InventoryPresenterImpl presenterNullView = new InventoryPresenterImpl(null, inventoryService);
        presenterNullView.initialize();
        verifyNoInteractions(inventoryService);
    }

    @Test
    void testInitializeFull() {
        List<Category> cats = Collections.emptyList();
        List<Product> prods = Collections.emptyList();
        when(inventoryService.getAllCategories()).thenReturn(cats);
        when(inventoryService.getAllProducts()).thenReturn(prods);

        presenter.initialize();

        verify(mockView).showCategories(cats);
        verify(mockView).showProducts(prods);
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
        Product productToDelete = Mockito.mock(Product.class);
        when(productToDelete.getId()).thenReturn(42L);

        java.util.List<Product> remainingProducts = java.util.Arrays.asList(); 
        when(inventoryService.getAllProducts()).thenReturn(remainingProducts);

        presenter.deleteProduct(productToDelete);

        verify(inventoryService).deleteProduct(42L);
        verify(mockView).showProducts(remainingProducts);
    }
    
    @Test
    void testInitializeShouldPopulateCategoriesAndProductsOnView() {
        java.util.List<Category> categories = java.util.Arrays.asList(new Category("Electronics"));
        java.util.List<Product> products = java.util.Arrays.asList(new Product("Mouse", 25.0));

        when(inventoryService.getAllCategories()).thenReturn(categories);
        when(inventoryService.getAllProducts()).thenReturn(products);

        presenter.initialize();

        verify(inventoryService).getAllCategories();
        verify(inventoryService).getAllProducts();
        verify(mockView).showCategories(categories);
        verify(mockView).showProducts(products);
    }
    
    @Test
    void testAddProductWithoutCategory() {
        presenter.addProduct("Laptop", 1000.0, null);
        
        verify(inventoryService).addProduct(any(Product.class));
        verify(mockView).showProducts(anyList());
    }
    
    @Test
    void testDeleteProductNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            presenter.deleteProduct(null);
        });
        
        verifyNoInteractions(inventoryService);
    }
    
    @Test
    void testInitializeWithNullViewDoesNotCrash() {
        InventoryPresenterImpl presenterNullView = new InventoryPresenterImpl(null, inventoryService);
        presenterNullView.initialize();
        verifyNoInteractions(inventoryService);
    }
    
    @Test
    void testAddProductWithFullCategoryInfo() {
        Category cat = mock(Category.class);
        when(cat.getId()).thenReturn(1L);
        presenter.addProduct("Name", 10.0, cat);
        verify(inventoryService).addProductToCategory(any(Product.class), eq(1L));
    }

    @Test
    void testAddProductWithCategoryButNullId() {
        Category cat = mock(Category.class);
        when(cat.getId()).thenReturn(null);
        presenter.addProduct("Name", 10.0, cat);
        verify(inventoryService).addProduct(any(Product.class));
    }

    @Test
    void testAddProductWithNullCategory() {
        presenter.addProduct("Name", 10.0, null);
        verify(inventoryService).addProduct(any(Product.class));
    }

    @Test
    void testDeleteProductValid() {
        Product p = mock(Product.class);
        when(p.getId()).thenReturn(1L);
        presenter.deleteProduct(p);
        verify(inventoryService).deleteProduct(1L);
        verify(mockView).showProducts(any());
    }

    @Test
    void testDeleteProductNullIdThrowsException() {
        Product p = mock(Product.class);
        when(p.getId()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> presenter.deleteProduct(p));
    }

    @Test
    void testDeleteProductNullObjectThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> presenter.deleteProduct(null));
    }
}