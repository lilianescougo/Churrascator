package com.liliane.assigment.churrascator;

public class GroceryItem {
    private int id = 0; //key
    private String name = ""; //Name of the item
    private boolean isUnitary = false; //If the item is unitary os per kilogram
    private double price = 0; //price per unit or kilogram, depending on the isUnitary
    private double volume = 0; //Volume in ml
    private boolean checked = false; //If the item will be present on the grocery shop list, if the user checked this item on the system.
    private int session = 0; //1 = meats, 2 = drinks, 3 = others

    public static final int SESSION_MEATS = 1;
    public static final int SESSION_DRINKS = 2;
    public static final int SESSION_OTHERS = 3;

    public GroceryItem(String name, boolean isUnitary, double price, int session) {
        this.name = name;
        this.isUnitary = isUnitary;
        this.price = price;
        this.session = session;
    }

    public int getId () {
        return this.id;
    }

    public void setId  (int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnitary() {
        return isUnitary;
    }

    public void setUnitary(boolean unitary) {
        isUnitary = unitary;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getSession() {
        return this.session;
    }

    public void setSession(int session) {
        this.session = session;
    }
}
