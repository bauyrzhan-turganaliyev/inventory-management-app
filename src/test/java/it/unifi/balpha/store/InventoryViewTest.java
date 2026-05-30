package it.unifi.balpha.store;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

class InventoryViewTest extends AssertJSwingJUnitTestCase {
    private FrameFixture window;
    private InventoryView inventoryView;

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(() -> {
            inventoryView = new InventoryView();
            return inventoryView;
        });
        
        window = new FrameFixture(robot(), inventoryView);
        window.show();
    }

    @Test
    void testWhenFormIsEmptyAddButtonShouldBeDisabled() {
        window.textBox("nameTextBox").requireEmpty();
        window.textBox("priceTextBox").requireEmpty();
        
        window.button("addProductButton").requireDisabled();
    }
}