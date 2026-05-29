package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void testProductAttributes() {
        Product product = new Product("Laptop", 1200.50);
        
        assertEquals("Laptop", product.getName());
        assertEquals(1200.50, product.getPrice());
    }
}