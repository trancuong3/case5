package org.example.profilecase5.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;


import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "User")
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", unique = true)
    @NotEmpty(message = "Email không được để trống")
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NotEmpty(message = "Password không được để trống")
    private String password;
    @Column(name="confirm_password_hash")
    @NotEmpty(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
    @Column(name = "phone")
    @NotEmpty(message = "Số điện thoại không được để trống")
    private String phone;
    @Column(name="fullname")
    @NotEmpty(message = "Tên không được để trống")
    private String fullname;
    @Column(name = "avatar", columnDefinition = "TEXT")
    private String avatar;


    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('Active', 'Locked') DEFAULT 'Active'")
    private Status status = Status.Active;



    @Column(name = "created_at", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updatedAt;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Owner owner;
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public enum Status {
        ACTIVE,   // Make sure the enum constant matches the value being passed.
        Active, LOCKED, Locked
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }


    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }



    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )

    private Set<Role> roles;

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RentalHistory> rentalHistories = new HashSet<>();

    // Getter and Setter for rentalHistories
    public Set<RentalHistory> getRentalHistories() {
        return rentalHistories;
    }

    public void setRentalHistories(Set<RentalHistory> rentalHistories) {
        this.rentalHistories = rentalHistories;
    }

}