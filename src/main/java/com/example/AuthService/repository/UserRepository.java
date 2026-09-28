package com.example.AuthService.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AuthService.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    List<User> findByMandalIdAndIsActiveTrue(Long mandalId);

    List<User> findByMandalIdAndDepartmentIdAndIsActiveTrue(Long mandalId, Long departmentId);

    
}
