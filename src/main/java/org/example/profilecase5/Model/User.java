package org.example.profilecase5.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "username")
    @NotEmpty(message = "Tên người dùng không được để trống")
    private String username;

    @Column(name = "email", unique = true)
    @NotEmpty(message = "Email không được để trống")
    private String email;

    @Column(name = "password", nullable = false)
    @NotEmpty(message = "Password không được để trống")
    @Size(min = 6, max = 32, message = "Mật khẩu phải có độ dài từ 6 đến 32 ký tự")
    private String password;
    @Column(name = "confirm_password")
    @NotEmpty(message = "Xác nhận mật khẩu không được để trống")
//    @Size(min = 6, max = 32, message = "Xác nhận mật khẩu phải có độ dài từ 6 đến 32 ký tự")
    private String passwordConfirm;

    @Column(name = "phone")
    @NotEmpty(message = "Số điện thoại không được để trống")
    private String phone;
    @Column(name="fullname")
    @NotEmpty(message = "Tên không được để trống")
    private String fullname;

    @Column(name = "address")
    @NotEmpty(message = "Địa chỉ không được để trống")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('Active', 'Locked') DEFAULT 'Active'")
    private Status status = Status.Active;


    @Column(name = "created_at", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updatedAt;


    public enum Status {
        ACTIVE,   
        Active, Locked
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Owner owner;

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

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
