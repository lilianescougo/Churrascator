package com.liliane.assigment.churrascator;

public class People {
    private int id = 0;
    private String name = "";
    private int quantity = 0;
    private double eats = 0;
    private double drinks = 0;

    public People(int id, String name, int quantity, double eats, double drinks) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.eats = eats;
        this.drinks = drinks;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getEats() {
        return eats;
    }

    public void setEats(double eats) {
        this.eats = eats;
    }

    public double getDrinks() {
        return drinks;
    }

    public void setDrinks(double drinks) {
        this.drinks = drinks;
    }
}
