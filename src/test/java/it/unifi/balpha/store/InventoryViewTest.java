package it.unifi.balpha.store;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

class InventoryViewTest {

    private InventoryView inventoryView;
    private FrameFixture window;
    private Category defaultCategory;

    @BeforeEach
    void setUp() throws InterruptedException, InvocationTargetException {
        GuiActionRunner.execute(() -> {
            inventoryView = new InventoryView();
            
            defaultCategory = new Category("DefaultCategory");
            inventoryView.getCategoryComboBox().addItem(defaultCategory);
        });
        
        window = new FrameFixture(inventoryView);
        window.show();
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test
    void testWhenFormIsEmptyAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").requireText("");
        window.textBox("priceTextBox").requireText("");
        window.button("addProductButton").requireDisabled();
    }
    
    @Test
    void testWhenFieldsAreFilledAddButtonShouldBeEnabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("1.50");
        
        window.comboBox("categoryComboBox").selectItem("DefaultCategory");

        window.button("addProductButton").requireEnabled();
    }

    @Test
    void testWhenFieldsAreClearedAddButtonShouldBeDisabledAgain() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("1.50");
        window.comboBox("categoryComboBox").selectItem("DefaultCategory");
        window.button("addProductButton").requireEnabled();

        window.textBox("nameTextBox").deleteText();
        window.button("addProductButton").requireDisabled();
    }

    @Test
    void testWhenPriceIsNotANumberAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("not-a-number");
        window.comboBox("categoryComboBox").selectItem("DefaultCategory");

        window.button("addProductButton").requireDisabled();
    }

    @Test
    void testCategoryComboBoxShouldBePresent() {
        assertNotNull(window.comboBox("categoryComboBox").target());
    }

    @Test
    void testAddButtonShouldCallPresenterWithCorrectDataAndCategory() {
        InventoryPresenter presenter = Mockito.mock(InventoryPresenter.class);
        inventoryView.setPresenter(presenter);

        Category testCategory = new Category("Electronics");
        
        GuiActionRunner.execute(() -> inventoryView.getCategoryComboBox().addItem(testCategory));
        
        window.comboBox("categoryComboBox").selectItem("Electronics");
        window.textBox("nameTextBox").enterText("Mouse");
        window.textBox("priceTextBox").enterText("25.00");

        window.button("addProductButton").click();

        verify(presenter).addProduct("Mouse", 25.00, testCategory);
    }
    
    @Test
    void testProductsTableShouldBePresent() {
        assertNotNull(window.table("productsTable").target());
    }

    @Test
    void testShowProductsShouldPopulateTable() {
        List<Product> products = java.util.Arrays.asList(
            new Product("Keyboard", 50.0),
            new Product("Mouse", 25.0)
        );

        GuiActionRunner.execute(() -> inventoryView.showProducts(products));

        window.table("productsTable").requireContents(new String[][] {
            { "Keyboard", "50.0" },
            { "Mouse", "25.0" }
        });
    }
    
    @Test
    void testDeleteButtonShouldBeDisabledUntilAnItemIsSelected() {
        java.util.List<Product> products = java.util.Arrays.asList(new Product("Keyboard", 50.0));
        GuiActionRunner.execute(() -> inventoryView.showProducts(products));

        window.button("deleteProductButton").requireDisabled();

        window.table("productsTable").selectRows(0);

        window.button("deleteProductButton").requireEnabled();
    }

    @Test
    void testDeleteButtonShouldCallPresenterWithCorrectProduct() {
        InventoryPresenter presenter = Mockito.mock(InventoryPresenter.class);
        inventoryView.setPresenter(presenter);

        Product productToDelete = new Product("Keyboard", 50.0);
        java.util.List<Product> products = java.util.Arrays.asList(productToDelete);
        GuiActionRunner.execute(() -> inventoryView.showProducts(products));

        window.table("productsTable").selectRows(0);
        window.button("deleteProductButton").click();

        verify(presenter).deleteProduct(productToDelete);
    }
}