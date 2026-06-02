package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void testProductAttributes() {
        Product product = new Product("Laptop", 1200.50);
        
        assertEquals("Laptop", product.getName());
        assertEquals(1200.50, product.getPrice());
        
        assertEquals(null, product.getId()); 
        assertEquals(null, product.getCategory());
    }
    
    @Test
    void testProductConstructorThrowsWhenNameIsEmpty() {
    	IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Product("", 500.0));
    	
    	assertEquals("Product name cannot be empty", exception.getMessage());
    }
    
    @Test
    void testProductConstructorThrowsWhenPriceIsNegative() {
    	IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Product("Laptop", -10.0));
    
    	assertEquals("Product price cannot be negative", exception.getMessage());
    }
    
    @Test
    void testProductPriceIsZeroIsAllowed() {
        Product product = new Product("Freebie", 0.0);
        assertEquals(0.0, product.getPrice());
    }
    
    @Test
    void testSetAndGetCategory() {
        Product product = new Product("Laptop", 1200.50);
        Category category = new Category("Electronics");
        
        product.setCategory(category);
        assertEquals(category, product.getCategory());
    }
}