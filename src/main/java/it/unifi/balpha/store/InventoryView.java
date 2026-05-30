package it.unifi.balpha.store;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.FlowLayout;

public class InventoryView extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private JTextField nameTextBox;
    private JTextField priceTextBox;
    private JButton addProductButton;
    
    private InventoryPresenter presenter;

    public InventoryView() {
        setTitle("Inventory Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLayout(new FlowLayout());

        nameTextBox = new JTextField(10);
        nameTextBox.setName("nameTextBox");
        add(nameTextBox);

        priceTextBox = new JTextField(5);
        priceTextBox.setName("priceTextBox");
        add(priceTextBox);

        addProductButton = new JButton("Add Product");
        addProductButton.setName("addProductButton");
        addProductButton.setEnabled(false); 
        add(addProductButton);
        
        addProductButton.addActionListener(e -> {
            if (presenter != null) {
                String name = nameTextBox.getText().trim();
                double price = Double.parseDouble(priceTextBox.getText().trim());
                presenter.addProduct(name, price);
            }
        });

        DocumentListener fieldsListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { checkFields(); }
            @Override
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            @Override
            public void changedUpdate(DocumentEvent e) { checkFields(); }
        };

        nameTextBox.getDocument().addDocumentListener(fieldsListener);
        priceTextBox.getDocument().addDocumentListener(fieldsListener);
    }
    
    public void setPresenter(InventoryPresenter presenter) {
        this.presenter = presenter;
    }

    private void checkFields() {
        String nameText = nameTextBox.getText().trim();
        String priceText = priceTextBox.getText().trim();

        boolean nameValid = !nameText.isEmpty();
        boolean priceValid = false;

        if (!priceText.isEmpty()) {
            try {
                double price = Double.parseDouble(priceText);
                if (price > 0) {
                    priceValid = true;
                }
            } catch (NumberFormatException e) {
                priceValid = false;
            }
        }
        
        addProductButton.setEnabled(nameValid && priceValid);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            InventoryView view = new InventoryView();
            view.pack();
            view.setLocationRelativeTo(null);
            view.setVisible(true);
        });
    }

    public JTextField getNameTextBox() { return nameTextBox; }
    public JTextField getPriceTextBox() { return priceTextBox; }
    public JButton getAddProductButton() { return addProductButton; }
}