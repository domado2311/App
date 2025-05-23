package com.example.sidenav;

public class Product {
    public String id;       // Firebase ID
    public String name;
    public String price;
    public String quantity;

    public Product() {} // Required for Firebase

    public Product(String name, String price, String quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }
}