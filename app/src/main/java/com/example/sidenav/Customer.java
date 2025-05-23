package com.example.sidenav;

public class Customer {
    private String id;
    private String name;
    private String contact;
    private String email;
    private String address;
    private String balance;

    public Customer() {
        // Required for Firebase
    }

    public Customer(String name, String contact, String email, String address, String balance) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.balance = balance;
    }

    public void setId(String id) { this.id = id; }
    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDebt() { return balance; }
    public void setDebt(String debt) { this.balance = debt; }
}
