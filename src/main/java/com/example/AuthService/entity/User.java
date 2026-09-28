package com.example.AuthService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;
    
    @Column(name = "password", nullable = false, length = 255)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Password must be 8+ chars, include uppercase and number")

    private String password;
    
    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    
    
    @Column(name = "district_id")
    private Long districtId;
    
    @Column(name = "mandal_id")
    private Long mandalId;
    
    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Column(name = "department_id")
    private Long departmentId;
    
    // Getters and Setters

    

    public Long getId() {
        return id;
    }
    
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Long getDistrictId() {
        return districtId;
    }
    
    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }
    
    public Long getMandalId() {
        return mandalId;
    }
    
    public void setMandalId(Long mandalId) {
        this.mandalId = mandalId;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Role> getRoles() { 
        return roles; 
    }
    public void setRoles(Set<Role> roles) { 
        this.roles = roles; 
    }

      //MANY-TO-MANY
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
    if (this.roles == null) {
        this.roles = new HashSet<>();
    }
    this.roles.add(role);
}

    public User() {}
}
