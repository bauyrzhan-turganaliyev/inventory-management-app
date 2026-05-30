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
}