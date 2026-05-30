package it.unifi.balpha.store;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.FlowLayout;

public class InventoryView extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private JTextField nameTextBox;
    private JTextField priceTextBox;
    private JButton addProductButton;

    public InventoryView() {
        setTitle("Inventory Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLayout(new FlowLayout());

        nameTextBox = new JTextField(10);
        add(nameTextBox);

        priceTextBox = new JTextField(5);
        add(priceTextBox);

        addProductButton = new JButton("Add Product");
        addProductButton.setEnabled(false);
        add(addProductButton);
    }

    public JTextField getNameTextBox() { return nameTextBox; }
    public JTextField getPriceTextBox() { return priceTextBox; }
    public JButton getAddProductButton() { return addProductButton; }
}