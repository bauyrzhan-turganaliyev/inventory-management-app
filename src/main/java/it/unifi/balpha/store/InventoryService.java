package it.unifi.balpha.store;

public class InventoryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public InventoryService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        productRepository.save(product);
    }
}