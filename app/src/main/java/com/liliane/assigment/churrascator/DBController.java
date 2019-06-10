package com.liliane.assigment.churrascator;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBController {
    private String TAG = "DBController";
    DBHelper dbHelper;


    public DBController (DBHelper dbHelper){
        this.dbHelper = dbHelper;
    }

    public void deleteAllGroceriesAndPeople() {
        dbHelper.deleteAllGroceriesAndPeople();
    }

    public void resetData(Context context) {
        deleteAllGroceriesAndPeople();
        initializeData(context);
    }

    private void initializeData(Context context) {
        //prepare default people and groceries
        Resources res = context.getResources();
        People man = new People(0, res.getString(R.string.man), 0, 0.5, 1.5);
        People woman = new People(1, res.getString(R.string.woman), 0, 0.5, 1.5);
        People children = new People(2, res.getString(R.string.children), 0, 0.25, 0.6);

        ArrayList<People> people = new ArrayList<>();
        people.add(man);
        people.add(woman);
        people.add(children);

        GroceryItem maminha = new GroceryItem(res.getString(R.string.maminha), false, 22.67, GroceryItem.SESSION_MEATS);
        GroceryItem picanha = new GroceryItem(res.getString(R.string.picanha), false, 33.5, GroceryItem.SESSION_MEATS);
        GroceryItem soda = new GroceryItem(res.getString(R.string.soda), true, 6, GroceryItem.SESSION_DRINKS);
        soda.setVolume(2000);
        GroceryItem beer = new GroceryItem(res.getString(R.string.beer), true, 8, GroceryItem.SESSION_DRINKS);
        beer.setVolume(1000);
        GroceryItem manioc = new GroceryItem(res.getString(R.string.manioc), false, 7, GroceryItem.SESSION_OTHERS);
        GroceryItem rice = new GroceryItem(res.getString(R.string.rice), false, 3.59, GroceryItem.SESSION_OTHERS);

        ArrayList<GroceryItem> groceryItems = new ArrayList<>();
        groceryItems.add(maminha);
        groceryItems.add(picanha);
        groceryItems.add(soda);
        groceryItems.add(beer);
        groceryItems.add(manioc);
        groceryItems.add(rice);


        dbHelper.addGroceryList(groceryItems);
        dbHelper.insertPeople(people);

    }


    public List<GroceryItem> getAllGroceryItemsfromSession(int session) {
        // Get all posts from database
        List<GroceryItem> groceryItems = dbHelper.getAllGroceriesFromSession(session);
        for (GroceryItem groceryItem : groceryItems) {
            Log.i(TAG, "item recuperado do BD: " + groceryItem.getName());
        }
        return groceryItems;
    }

    public List<People> getPeopleList() {
        List<People> peopleList= dbHelper.getPeople();
        for (People person : peopleList) {
            Log.i(TAG, "item recuperado do BD: " + person.getName());
        }

        return peopleList;
    }

    public void updatePeopleQuantity(int id, int qtd){
        dbHelper.updatePeopleQuantity(id, qtd);
    }

    public void updateGroceryItemIsChecked(int id, boolean isChecked) {
        dbHelper.updateGroceryItemIsChecked(id, isChecked);
    }
}
