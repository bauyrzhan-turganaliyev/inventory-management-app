package it.unifi.balpha.store;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;
import java.util.List;

public class InventoryView extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private JTextField nameTextBox;
    private JTextField priceTextBox;
    private JComboBox<Category> categoryComboBox;
    private JButton addProductButton;
    
    private JTable productsTable;
    private DefaultTableModel tableModel;
    
    private InventoryPresenter presenter;

    public InventoryView() {
        setupWindow();
        createComponents();
        setupListeners();
    }
    
    private void setupWindow() {
        setTitle("Inventory Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
    }

    private void createComponents() {
        nameTextBox = new JTextField(10);
        nameTextBox.setName("nameTextBox");
        add(nameTextBox);
        
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setName("categoryComboBox");
        add(categoryComboBox);

        priceTextBox = new JTextField(5);
        priceTextBox.setName("priceTextBox");
        add(priceTextBox);

        addProductButton = new JButton("Add Product");
        addProductButton.setName("addProductButton");
        addProductButton.setEnabled(false); 
        add(addProductButton);
        
        String[] columnNames = { "Product Name", "Price" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        productsTable = new JTable(tableModel);
        productsTable.setName("productsTable");
        
        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        add(scrollPane);
    }

    private void setupListeners() {
    	addProductButton.addActionListener(e -> {
            if (presenter != null) {
                String name = nameTextBox.getText().trim();
                double price = Double.parseDouble(priceTextBox.getText().trim());
                Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
                
                presenter.addProduct(name, price, selectedCategory);
            }
        });

        DocumentListener fieldsListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { checkFields(); }
            @Override public void removeUpdate(DocumentEvent e) { checkFields(); }
            @Override public void changedUpdate(DocumentEvent e) { checkFields(); }
        };
        nameTextBox.getDocument().addDocumentListener(fieldsListener);
        priceTextBox.getDocument().addDocumentListener(fieldsListener);
    }
    
    public void showProducts(List<Product> products) {
        tableModel.setRowCount(0);
        if (products != null) {
            for (Product p : products) {
                tableModel.addRow(new Object[] { p.getName(), String.valueOf(p.getPrice()) });
            }
        }
    }
    
    public void setPresenter(InventoryPresenter presenter) {
        this.presenter = presenter;
    }

    private void checkFields() {
        String name = nameTextBox.getText().trim();
        String priceStr = priceTextBox.getText().trim();
        
        boolean hasCategory = categoryComboBox.getSelectedItem() != null;
        
        addProductButton.setEnabled(!name.isEmpty() && isValidPrice(priceStr) && hasCategory);
    }

    private boolean isValidPrice(String priceStr) {
        try {
            double price = Double.parseDouble(priceStr);
            return price > 0; 
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            InventoryView view = new InventoryView();
            view.pack();
            view.setLocationRelativeTo(null);
            view.setVisible(true);
        });
    }
    
    public JComboBox<Category> getCategoryComboBox() {
        return categoryComboBox;
    }

    public JTextField getNameTextBox() { return nameTextBox; }
    public JTextField getPriceTextBox() { return priceTextBox; }
    public JButton getAddProductButton() { return addProductButton; }
}