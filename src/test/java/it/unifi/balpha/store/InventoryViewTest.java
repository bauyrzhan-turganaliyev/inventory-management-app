package it.unifi.balpha.store;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.core.Robot;
import org.assertj.swing.core.BasicRobot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryViewTest {

    private FrameFixture window;
    private Robot robot;
    private InventoryView inventoryView;

    @BeforeEach
    void setUp() {
        robot = BasicRobot.robotWithNewAwtHierarchy();
        
        inventoryView = GuiActionRunner.execute(() -> new InventoryView());
        
        window = new FrameFixture(robot, inventoryView);
        window.show(); 
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Test
    void testWhenFormIsEmptyAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").requireEmpty();
        window.textBox("priceTextBox").requireEmpty();
        window.button("addProductButton").requireDisabled();
    }

    @Test
    void testWhenFieldsAreFilledAddButtonShouldBeEnabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("1.50");
        
        window.button("addProductButton").requireEnabled();
    }
    
    @Test
    void testWhenFieldsAreClearedAddButtonShouldBeDisabledAgain() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("1.50");
        window.button("addProductButton").requireEnabled();

        window.textBox("nameTextBox").deleteText();

        window.button("addProductButton").requireDisabled();
    }

    @Test
    void testWhenPriceIsNotANumberAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").enterText("Apple");
        window.textBox("priceTextBox").enterText("not-a-number");

        window.button("addProductButton").requireDisabled();
    }
}