package it.unifi.balpha.store;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.awt.EventQueue;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("inventory-pu");
        EntityManager em = emf.createEntityManager();

        EventQueue.invokeLater(() -> {
            try {
                ProductRepository productRepository = new ProductJpaRepository(em);
                CategoryRepository categoryRepository = new CategoryJpaRepository(em);

                InventoryService inventoryService = new InventoryService(productRepository, categoryRepository);

                em.getTransaction().begin();
                try {
                    Category electronics = new Category("Electronics");
                    Category food = new Category("Food");
                    
                    categoryRepository.save(electronics);
                    categoryRepository.save(food);

                    Product sampleProduct = new Product("Mechanical Keyboard", 89.99);
                    inventoryService.addProduct(sampleProduct);
                    
                    if (electronics.getId() != null) {
                        inventoryService.addProductToCategory(sampleProduct, electronics.getId());
                    }
                    
                    em.getTransaction().commit();
                } catch (Exception ex) {
                    em.getTransaction().rollback();
                    ex.printStackTrace();
                }

                InventoryView view = new InventoryView();
                InventoryPresenterImpl presenter = new InventoryPresenterImpl(inventoryService);

                view.setPresenter(presenter);
                presenter.setView(view);

                presenter.initialize();
                view.setVisible(true);

                view.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        em.close();
                        emf.close();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}