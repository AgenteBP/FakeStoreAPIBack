package com.kingsman.FakeStoreAPIBack.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kingsman.FakeStoreAPIBack.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
