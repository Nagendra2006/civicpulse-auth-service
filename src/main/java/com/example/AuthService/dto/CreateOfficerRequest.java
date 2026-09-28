package com.example.AuthService.dto;

public class CreateOfficerRequest {

    private String name;
    private String email;
    private String password;
    private String phone;

    private Long departmentId;
    private Long mandalId;
    private Long districtId;
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
    public Long getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
    public Long getMandalId() {
        return mandalId;
    }
    public void setMandalId(Long mandalId) {
        this.mandalId = mandalId;
    }
    public Long getDistrictId() {
        return districtId;
    }
    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }


    
    // getters & setters
}