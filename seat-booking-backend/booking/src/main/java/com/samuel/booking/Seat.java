package com.samuel.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;


@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String status;
    private double price;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("seat")
    private User user;

    public Seat() {}

    public Seat(String id, String status, double price, User user) {
        this.id = id;
        this.status = status;
        this.price = price;
        this.user = user;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }


    public void setPrice(double price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", price=" + price +
                ", user=" + user +
                '}';
    }
}
