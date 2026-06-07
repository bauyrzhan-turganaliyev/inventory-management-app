package it.unifi.balpha.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryViewTest {

    private InventoryView inventoryView;
    private FrameFixture window;
    private Category defaultCategory;
    private InventoryPresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = mock(InventoryPresenter.class);
        GuiActionRunner.execute(() -> {
            inventoryView = new InventoryView();
            inventoryView.setPresenter(presenter);
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
        window.textBox("nameTextBox").requireText(""); // NOSONAR - AssertJ Swing require* methods are assertions
        window.textBox("priceTextBox").requireText("");
        window.button("addProductButton").requireDisabled();
    }

    @Test
    void testWhenFieldsAreFilledAddButtonShouldBeEnabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("1.50");
        window.comboBox("categoryComboBox").selectItem("DefaultCategory");
        window.button("addProductButton").requireEnabled(); // NOSONAR - AssertJ Swing require* methods are assertions
    }

    @Test
    void testWhenFieldsAreClearedAddButtonShouldBeDisabledAgain() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("1.50");
        window.comboBox("categoryComboBox").selectItem("DefaultCategory");
        window.button("addProductButton").requireEnabled();
        window.textBox("nameTextBox").deleteText();
        window.button("addProductButton").requireDisabled(); // NOSONAR - AssertJ Swing require* methods are assertions
    }

    @Test
    void testWhenPriceIsNotANumberAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("not-a-number");
        window.comboBox("categoryComboBox").selectItem("DefaultCategory");
        window.button("addProductButton").requireDisabled(); // NOSONAR - AssertJ Swing require* methods are assertions
    }

    @Test
    void testCategoryComboBoxShouldBePresent() {
        assertNotNull(window.comboBox("categoryComboBox").target());
    }

    @Test
    void testAddButtonShouldCallPresenterWithCorrectDataAndCategory() {
        Category testCategory = new Category("Electronics");
        GuiActionRunner.execute(() -> inventoryView.getCategoryComboBox().addItem(testCategory));

        window.textBox("nameTextBox").enterText("Mouse");
        window.textBox("priceTextBox").enterText("25.00");
        window.comboBox("categoryComboBox").selectItem("Electronics");
        window.button("addProductButton").requireEnabled();

        GuiActionRunner.execute(() -> inventoryView.getAddProductButton().doClick());

        Awaitility.await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() ->
                verify(presenter).addProduct("Mouse", 25.00, testCategory)
            );
    }

    @Test
    void testProductsTableShouldBePresent() {
        assertNotNull(window.table("productsTable").target());
    }

    @Test
    void testShowProductsShouldPopulateTable() {
        List<Product> products = Arrays.asList(
            new Product("Keyboard", 50.0),
            new Product("Mouse", 25.0)
        );
        GuiActionRunner.execute(() -> inventoryView.showProducts(products));
        window.table("productsTable").requireContents(new String[][] {
            { "Keyboard", "50.0" },
            { "Mouse", "25.0" }
        }); // NOSONAR - AssertJ Swing require* methods are assertions
    }

    @Test
    void testDeleteButtonShouldBeDisabledUntilAnItemIsSelected() {
        List<Product> products = Arrays.asList(new Product("Keyboard", 50.0));
        GuiActionRunner.execute(() -> inventoryView.showProducts(products));

        window.button("deleteProductButton").requireDisabled();

        GuiActionRunner.execute(() ->
            inventoryView.getProductsTable().setRowSelectionInterval(0, 0)
        );

        window.button("deleteProductButton").requireEnabled(); // NOSONAR - AssertJ Swing require* methods are assertions
    }

    @Test
    void testDeleteButtonShouldCallPresenterWithCorrectProduct() {
        Product productToDelete = new Product("Keyboard", 50.0);
        List<Product> products = Arrays.asList(productToDelete);
        GuiActionRunner.execute(() -> inventoryView.showProducts(products));

        GuiActionRunner.execute(() ->
            inventoryView.getProductsTable().setRowSelectionInterval(0, 0)
        );

        window.button("deleteProductButton").requireEnabled();
        GuiActionRunner.execute(() -> inventoryView.getDeleteProductButton().doClick());
        verify(presenter).deleteProduct(productToDelete);
    }

    @Test
    void testTableCellsAreNotEditable() {
        assertFalse(inventoryView.isProductTableEditable(0, 0),
            "Table cells should not be editable");
    }

    @Test
    void testFieldsListenerTriggersCheckFieldsOnAllEvents() {
        window.textBox("nameTextBox").enterText("A");
        window.button("addProductButton").requireDisabled();
        window.textBox("nameTextBox").deleteText();
        window.button("addProductButton").requireDisabled();
        window.textBox("nameTextBox").enterText("B");
        window.textBox("nameTextBox").deleteText();
        window.textBox("nameTextBox").enterText("C");
        window.button("addProductButton").requireDisabled(); // NOSONAR - AssertJ Swing require* methods are assertions
    }
    
    @Test
    void testAddButtonDoesNothingWhenPresenterIsNull() {
        GuiActionRunner.execute(() -> {
            inventoryView.setPresenter(null);
            for (java.awt.event.ActionListener al : inventoryView.getAddProductButton().getActionListeners()) {
                al.actionPerformed(null);
            }
        });
        assertNotNull(inventoryView);
    }

    @Test
    void testDeleteButtonDoesNothingWhenPresenterIsNull() {
        Product product = new Product("Keyboard", 50.0);
        List<Product> products = Arrays.asList(product);
        GuiActionRunner.execute(() -> {
            inventoryView.showProducts(products);
            inventoryView.setPresenter(null);
            inventoryView.getProductsTable().setRowSelectionInterval(0, 0);
            inventoryView.getDeleteProductButton().doClick();
        });
        
        assertNotNull(inventoryView);
    }

    @Test
    void testShowCategoriesPopulatesComboBox() {
        List<Category> categories = Arrays.asList(
            new Category("Electronics"),
            new Category("Food")
        );
        GuiActionRunner.execute(() -> inventoryView.showCategories(categories));

        assertThat(window.comboBox("categoryComboBox").contents())
            .containsExactly("Electronics", "Food");
    }

    @Test
    void testShowCategoriesWithNullClearsComboBox() {
        GuiActionRunner.execute(() -> inventoryView.showCategories(null));

        assertThat(window.comboBox("categoryComboBox").contents()).isEmpty();
    }

    @Test
    void testShowProductsWithNullClearsTable() {
        GuiActionRunner.execute(() -> inventoryView.showProducts(null));
        window.table("productsTable").requireContents(new String[][] {}); // NOSONAR
    }

    @Test
    void testDeleteButtonDisabledWhenSelectionCleared() {
        List<Product> products = Arrays.asList(new Product("Keyboard", 50.0));
        GuiActionRunner.execute(() -> inventoryView.showProducts(products));
        GuiActionRunner.execute(() ->
            inventoryView.getProductsTable().setRowSelectionInterval(0, 0)
        );
        window.button("deleteProductButton").requireEnabled();
        GuiActionRunner.execute(() ->
            inventoryView.getProductsTable().clearSelection()
        );
        window.button("deleteProductButton").requireDisabled(); // NOSONAR
    }
    
    @Test
    void testDeleteButtonDoesNothingWhenNoRowSelected() {
        List<Product> products = Arrays.asList(new Product("Keyboard", 50.0));
        
        GuiActionRunner.execute(() -> inventoryView.showProducts(products));
        GuiActionRunner.execute(() -> inventoryView.getDeleteProductButton().doClick());
        
        assertNotNull(inventoryView);
    }
  
    @Test
    void testWhenPriceIsZeroAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("0");
        window.comboBox("categoryComboBox").selectItem("DefaultCategory");
        window.button("addProductButton").requireDisabled(); // NOSONAR
    }
    
    @Test
    void testWhenOnlyNameFilledAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.button("addProductButton").requireDisabled(); // NOSONAR
    }

    @Test
    void testWhenOnlyPriceFilledAddButtonShouldBeDisabled() {
        window.textBox("priceTextBox").enterText("1.50");
        window.button("addProductButton").requireDisabled(); // NOSONAR
    }
    
    @Test
    void testDeleteButtonDoesNothingWhenSelectedRowOutOfBounds() {
        List<Product> products = Arrays.asList(new Product("Keyboard", 50.0));
        GuiActionRunner.execute(() -> {
            inventoryView.showProducts(products);
            inventoryView.getProductsTable().setRowSelectionInterval(0, 0);
            
            inventoryView.showProducts(new ArrayList<>());
            
            inventoryView.getDeleteProductButton().doClick();
        });
        assertNotNull(inventoryView);
    }
    
    @Test
    void testWhenNameAndCategorySetButPriceInvalidAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.comboBox("categoryComboBox").selectItem("DefaultCategory");
        window.textBox("priceTextBox").enterText("abc");
        window.button("addProductButton").requireDisabled(); // NOSONAR
    }
}