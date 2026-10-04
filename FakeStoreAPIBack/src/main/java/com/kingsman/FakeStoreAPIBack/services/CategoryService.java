package com.kingsman.FakeStoreAPIBack.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kingsman.FakeStoreAPIBack.DTO.CategoryRequest;
import com.kingsman.FakeStoreAPIBack.DTO.CategoryResponse;
import com.kingsman.FakeStoreAPIBack.exception.ConflictException;
import com.kingsman.FakeStoreAPIBack.exception.NotFoundException;
import com.kingsman.FakeStoreAPIBack.models.Category;
import com.kingsman.FakeStoreAPIBack.repositories.CategoryRepository;
import com.kingsman.FakeStoreAPIBack.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(CategoryResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return CategoryResponse.fromEntity(findCategory(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        RequestValidator.requireText(request.name(), "name");
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        RequestValidator.requireText(request.name(), "name");
        Category category = findCategory(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return CategoryResponse.fromEntity(category);
    }

    // Solo se borra una categoría vacía. Si tiene productos (aunque estén desactivados)
    // se responde 409: primero hay que mover esos productos a otra categoría.
    @Transactional
    public void delete(Long id) {
        findCategory(id);
        if (productRepository.existsByCategory_IdCategory(id)) {
            throw new ConflictException("La categoría tiene productos y no se puede borrar");
        }
        categoryRepository.deleteById(id);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la categoría con id " + id));
    }
}
