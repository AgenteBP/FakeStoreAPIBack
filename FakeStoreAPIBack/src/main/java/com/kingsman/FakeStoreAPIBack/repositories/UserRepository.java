package com.kingsman.FakeStoreAPIBack.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kingsman.FakeStoreAPIBack.Enum.Role;
import com.kingsman.FakeStoreAPIBack.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByEmail(String email);

    boolean existsByRole(Role role);
}
