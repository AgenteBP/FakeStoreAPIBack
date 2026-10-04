package com.kingsman.FakeStoreAPIBack.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kingsman.FakeStoreAPIBack.DTO.ProductRequest;
import com.kingsman.FakeStoreAPIBack.DTO.ProductResponse;
import com.kingsman.FakeStoreAPIBack.DTO.ProductView;
import com.kingsman.FakeStoreAPIBack.exception.BadRequestException;
import com.kingsman.FakeStoreAPIBack.exception.ConflictException;
import com.kingsman.FakeStoreAPIBack.exception.NotFoundException;
import com.kingsman.FakeStoreAPIBack.models.Category;
import com.kingsman.FakeStoreAPIBack.models.Inventory;
import com.kingsman.FakeStoreAPIBack.models.Product;
import com.kingsman.FakeStoreAPIBack.repositories.CategoryRepository;
import com.kingsman.FakeStoreAPIBack.repositories.InventoryRepository;
import com.kingsman.FakeStoreAPIBack.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    // Lista solo los productos activos, con su categoría y stock disponible.
    // Si llega categoryId, filtra por esa categoría.
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll(Long categoryId) {
        List<ProductView> products = categoryId == null
                ? productRepository.findAllActive()
                : productRepository.findActiveByCategory(categoryId);
        return products.stream().map(ProductResponse::fromView).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productRepository.findViewById(id)
                .map(ProductResponse::fromView)
                .orElseThrow(() -> new NotFoundException("No existe el producto con id " + id));
    }

    // Crea el producto activo y, en la misma transacción, su fila de inventario con stock 0.
    // El stock se carga después con PUT /api/products/{id}/inventory.
    @Transactional
    public ProductResponse create(ProductRequest request) {
        validate(request);
        if (productRepository.existsBySku(request.sku())) {
            throw new ConflictException("Ya existe un producto con el SKU " + request.sku());
        }

        Product product = new Product();
        copyFields(request, product);
        product.setActive(true);
        productRepository.save(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setAvailableQuantity(0);
        inventory.setReservedQuantity(0);
        inventoryRepository.save(inventory);

        return ProductResponse.fromEntity(product, 0);
    }

    // Actualiza los datos del producto. Si el SKU cambia, se verifica que no lo tenga otro producto.
    // active es opcional: si viene, permite reactivar (o desactivar) el producto.
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        validate(request);
        Product product = findProduct(id);

        boolean skuChanged = !product.getSku().equals(request.sku());
        if (skuChanged && productRepository.existsBySku(request.sku())) {
            throw new ConflictException("Ya existe un producto con el SKU " + request.sku());
        }

        copyFields(request, product);
        if (request.active() != null) {
            product.setActive(request.active());
        }

        int availableQuantity = inventoryRepository.findByProduct_IdProduct(id)
                .map(Inventory::getAvailableQuantity)
                .orElse(0);
        return ProductResponse.fromEntity(product, availableQuantity);
    }

    // Borrado lógico: el producto queda con active = false y deja de aparecer en el listado.
    // No se borra de la tabla porque las compras anteriores lo referencian en order_items.
    @Transactional
    public void delete(Long id) {
        Product product = findProduct(id);
        product.setActive(false);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el producto con id " + id));
    }

    private void validate(ProductRequest request) {
        RequestValidator.requireText(request.name(), "name");
        RequestValidator.requireText(request.sku(), "sku");
        RequestValidator.requireNotNull(request.price(), "price");
        RequestValidator.requireNotNull(request.idCategory(), "idCategory");
        if (request.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("El precio no puede ser negativo");
        }
    }

    // Copia los campos del request al producto, incluida la categoría (404 si no existe)
    private void copyFields(ProductRequest request, Product product) {
        Category category = categoryRepository.findById(request.idCategory())
                .orElseThrow(() -> new NotFoundException("No existe la categoría con id " + request.idCategory()));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setSku(request.sku());
        product.setImageUrl(request.imageUrl());
        product.setCategory(category);
    }
}
