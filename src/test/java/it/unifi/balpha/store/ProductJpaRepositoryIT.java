package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductJpaRepositoryTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private ProductJpaRepository repository;

    @Test
    void testFindById() {
        Product p = new Product("Test", 1.0);
        when(em.find(Product.class, 1L)).thenReturn(p);
        
        assertEquals(p, repository.findById(1L));
    }

    @Test
    void testFindAll() {
        TypedQuery<Product> query = mock(TypedQuery.class);
        List<Product> products = List.of(new Product("A", 1.0));
        when(em.createQuery("FROM Product", Product.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(products);
        
        assertEquals(products, repository.findAll());
    }

    @Test
    void testSaveNewProduct() {
        Product p = new Product("New Product", 10.0);
        
        repository.save(em, p);
        
        verify(em).persist(p);
        verify(em).flush();
    }

    @Test
    void testSaveExistingProduct() {
        Product p = mock(Product.class);
        when(p.getId()).thenReturn(1L);
        
        repository.save(em, p);
        
        verify(em).merge(p);
        verify(em, never()).persist(any());
    }

    @Test
    void testDeleteByIdExists() {
        Product p = new Product("DeleteMe", 1.0);
        when(em.find(Product.class, 1L)).thenReturn(p);
        
        repository.deleteById(1L);
        
        verify(em).remove(p);
    }

    @Test
    void testDeleteByIdNotExists() {
        when(em.find(Product.class, 1L)).thenReturn(null);
        
        repository.deleteById(1L);
        
        verify(em, never()).remove(any());
    }
}