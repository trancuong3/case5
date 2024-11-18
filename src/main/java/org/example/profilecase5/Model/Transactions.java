package org.example.profilecase5.Model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    private RentalHistory rentalHistory; // Nếu giao dịch liên quan đến một lịch sử thuê nhà cụ thể

    @Column(name = "amount", nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", columnDefinition = "ENUM('payment', 'refund', 'deposit')", nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('pending', 'completed', 'failed')", nullable = false)
    private TransactionStatus status;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    public enum TransactionType {
        payment, refund, deposit
    }

    public enum TransactionStatus {
        pending, completed, failed
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RentalHistory getRentalHistory() {
        return rentalHistory;
    }

    public void setRentalHistory(RentalHistory rentalHistory) {
        this.rentalHistory = rentalHistory;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

