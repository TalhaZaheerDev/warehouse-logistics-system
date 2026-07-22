package com.talha.slwms.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {
    private String customerId;
    private String name;
    private String address;
    private String email;
    private String phone;
    private LocalDateTime registerdAt;

    public  Customer(String name, String address, String email, String phone) {
        if(name ==null || name.isBlank()){
            throw new IllegalArgumentException("Customer name can't be empty");
        }
        if(email == null || !email.contains("@")){
            throw new IllegalArgumentException("Invalid email address : " +email);
        }

        this.customerId = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.registerdAt = LocalDateTime.now();
    }

    public String getCustomerId() {return customerId;}
    public String getName() {return name;}
    public String getAddress() {return address;}
    public String getEmail() {return email;}
    public String getPhone() {return phone;}
    public LocalDateTime getRegisterdAt() {return registerdAt;}

    public void setName(String name) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Customer name can't be empty");
        }

        this.name = name;
    }

    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }


    @Override
    public String toString() {
        return "Customer{id=" +customerId+", name"+name+", email"+email+", phone"+phone+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId.equals(customer.customerId);
    }

    @Override
    public int hashCode() {
        return customerId.hashCode();
    }
}
