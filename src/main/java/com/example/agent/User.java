package com.example.agent;

public class User {
    public String name;
    public String company;

    public int price;

    public User(String name, String company, int price) {
        this.name = name;
        this.company = company;
        this.price = price;

    }


    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public int getPrice() {
        return price;
    }
}
