package com.example.MediFlow.repository;

import com.example.MediFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE email = :v1", nativeQuery = true)
    public Optional<User> findUserByEmail(@Param("v1") String email);

    @Query(value = "SELECT id_user FROM users WHERE email = :email", nativeQuery = true)
    Optional<Long> findUserIdByEmail(String email);

    Optional<User> findByTokenToValidate(Long token);
}
