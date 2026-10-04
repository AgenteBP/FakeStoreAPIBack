package com.kingsman.FakeStoreAPIBack.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kingsman.FakeStoreAPIBack.models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(value = "SELECT * FROM addresses WHERE id_user = :userId", nativeQuery = true)
    List<Address> findByUserId(@Param("userId") Long userId);

    boolean existsByUser_IdUser(Long userId);
}
