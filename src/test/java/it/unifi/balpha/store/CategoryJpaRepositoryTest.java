package it.unifi.balpha.store;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryJpaRepositoryTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private CategoryJpaRepository repository;

    @Test
    void testSaveNewCategoryCallsPersist() {
        Category category = new Category("Test");
        
        repository.save(category);
        
        verify(em).persist(category);
        verify(em, never()).merge(any());
    }

    @Test
    void testSaveExistingCategoryCallsMerge() {
        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L); 
        
        repository.save(category); 
        
        verify(em).merge(category);
        verify(em, never()).persist(any());
    }
    
    @Test
    void testFindById() {
        Long id = 1L;
        Category expectedCategory = new Category("Electronics");
        
        when(em.find(Category.class, id)).thenReturn(expectedCategory);
        
        Category actualCategory = repository.findById(id);
        
        assertEquals(expectedCategory, actualCategory);
    }
    
    @Test
    void testFindAll() {
        List<Category> expectedList = List.of(new Category("Books"), new Category("Electronics"));
        TypedQuery<Category> mockQuery = mock(TypedQuery.class);
        
        when(em.createQuery("FROM Category", Category.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(expectedList);
        
        List<Category> actualList = repository.findAll();
        
        assertEquals(expectedList, actualList);
        assertEquals(2, actualList.size());
    }
}