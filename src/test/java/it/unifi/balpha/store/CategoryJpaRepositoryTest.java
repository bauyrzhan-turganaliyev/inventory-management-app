package it.unifi.balpha.store;

import static org.mockito.Mockito.*;
import jakarta.persistence.EntityManager;
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
}