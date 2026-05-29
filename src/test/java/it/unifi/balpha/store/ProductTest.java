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
}