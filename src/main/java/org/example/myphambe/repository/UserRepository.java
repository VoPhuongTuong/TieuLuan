package org.example.myphambe.repository;

import org.example.myphambe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "u.phone LIKE CONCAT('%', :search, '%')")
    List<User> searchUsers(@Param("search") String search);
    List<User> findByRole(Integer role);
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countTotalOrdersByUserId(@Param("userId") Integer userId);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o WHERE o.user.id = :userId AND o.status = 'DELIVERED'")
    Double sumTotalSpentByUserId(@Param("userId") Integer userId);
}
