package it.unifi.balpha.store;

import java.awt.EventQueue;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("inventory-pu");
        
        JpaTransactionManager transactionManager = new JpaTransactionManager(emf);

        try {
            transactionManager.doInTransaction(em -> {
                ProductRepository productRepository = new ProductJpaRepository(em);
                CategoryRepository categoryRepository = new CategoryJpaRepository(em);

                Category electronics = new Category("Electronics");
                Category food = new Category("Food");
                categoryRepository.save(electronics);
                categoryRepository.save(food);

                Product sampleProduct = new Product("Mechanical Keyboard", 89.99);
                productRepository.save(em, sampleProduct);

                sampleProduct.setCategory(electronics);
                productRepository.save(em, sampleProduct);
                
                return null;
            });
        } catch (Exception ex) {
            System.err.println("Error in initialize database");
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            try {
                InventoryService inventoryService = new InventoryService(
                    transactionManager, 
                    null,
                    null
                );

                InventoryView view = new InventoryView();
                InventoryPresenterImpl presenter = new InventoryPresenterImpl(view, inventoryService);

                view.setPresenter(presenter);
                presenter.initialize();
                view.setVisible(true);

                view.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        if (emf.isOpen()) {
                            emf.close();
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("Error in start Java Swing");
                e.printStackTrace();
            }
        });
    }
}