package it.unifi.balpha.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.List;

class CategoryTest {

    @Test
    void testCategoryAttributesAndProducts() {
        Category category = new Category("Electronics");
        assertEquals("Electronics", category.getName());

        assertTrue(category.getProducts().isEmpty());

        Product laptop = new Product("Laptop", 1200.0);
        category.addProduct(laptop);

        List<Product> products = category.getProducts();
        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }
}