package it.unifi.balpha.store;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class InventoryViewTest {

    private InventoryView inventoryView;

    @BeforeEach
    void setUp() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            inventoryView = new InventoryView();
            inventoryView.setVisible(true);
        });
    }

    @AfterEach
    void tearDown() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            if (inventoryView != null) {
                inventoryView.dispose();
            }
        });
    }

    @Test
    void testWhenFormIsEmptyAddButtonShouldBeDisabled() {
        assertEquals("", inventoryView.getNameTextBox().getText());
        assertEquals("", inventoryView.getPriceTextBox().getText());
        
        assertFalse(inventoryView.getAddProductButton().isEnabled(), 
            "Кнопка добавления должна быть заблокирована, если поля пусты");
    }
}