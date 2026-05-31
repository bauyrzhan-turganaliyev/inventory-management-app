package it.unifi.balpha.store;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.awt.EventQueue;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("inventory-pu");
        
        JpaTransactionManager transactionManager = new JpaTransactionManager(emf);

        InventoryService inventoryService = new InventoryService(transactionManager);

        try {
            transactionManager.doInTransaction(em -> {
                ProductRepository productRepository = new ProductJpaRepository(em);
                CategoryRepository categoryRepository = new CategoryJpaRepository(em);

                Category electronics = new Category("Electronics");
                Category food = new Category("Food");
                categoryRepository.save(electronics);
                categoryRepository.save(food);

                Product sampleProduct = new Product("Mechanical Keyboard", 89.99);
                productRepository.save(sampleProduct);

                if (electronics.getId() != null) {
                    sampleProduct.setCategory(electronics);
                    productRepository.save(sampleProduct);
                }
                
                return null;
            });
        } catch (Exception ex) {
            System.err.println("Error in initialize database");
            ex.printStackTrace();
        }

        EventQueue.invokeLater(() -> {
            try {
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