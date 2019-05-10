package com.liliane.assigment.churrascator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Main";

    private LinearLayout linearLayout;

    DBController dbController;

    int currentMenuItemID;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            return displayViewForID(item.getItemId());

        }
    };

    private boolean displayViewForID(int itemId) {
        List<GroceryItem> groceryItems = null;
        currentMenuItemID = itemId;
        switch (itemId) {
            case R.id.navigation_people:
                showPeople();
                return true;
            case R.id.navigation_meats:
                groceryItems = dbController.getAllGroceryItemsfromSession(GroceryItem.SESSION_MEATS);
                break;
            case R.id.navigation_drinks:
                groceryItems = dbController.getAllGroceryItemsfromSession(GroceryItem.SESSION_DRINKS);
                break;
            case R.id.navigation_others:
                groceryItems = dbController.getAllGroceryItemsfromSession(GroceryItem.SESSION_OTHERS);
                break;
        }
        showGroceryItemsList(groceryItems);
        return true;
    }

    private void showGroceryItemsList(List<GroceryItem> groceryItems) {
        linearLayout.removeAllViews();

        for (final GroceryItem groceryItem : groceryItems) {
            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setId(groceryItem.getId());
            cb.setText(groceryItem.getName());
            cb.setTag(groceryItem.getName());
            cb.setTextSize(36);
            cb.setChecked(groceryItem.isChecked());
//            cb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String msg = "You have " + (isChecked ? "checked" : "unchecked") + " : " + buttonView.getTag().toString()
                            + String.format(", id: %d", buttonView.getId());
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    //TODO fazer funcionar a persistencia do isChecked
                    dbController.updateGroceryItemIsChecked(groceryItem.getId(), isChecked);
                }
            });


            linearLayout.addView(cb);
            Log.i(TAG, "item recuperado do BD: " + groceryItem.getName());

        }
    }

    private void showPeople (){
        //getPeopleList
        List<People> peopleList = dbController.getPeopleList();

        //clar layout
        linearLayout.removeAllViews();

        //populate layout
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (final People person : peopleList) {
            Context context = getApplicationContext();

            //cria linear layout horizontal
            LinearLayout llhorizontal = new LinearLayout(getApplicationContext());
            llhorizontal.setOrientation(LinearLayout.HORIZONTAL);
            llhorizontal.setLayoutParams(lp);

            //textview com número atual
            final TextView tvValue = new TextView(context);
            tvValue.setText(String.format("%d", person.getQuantity()));
            tvValue.setLayoutParams(lp);
            tvValue.setTextSize(40);

            //textviwe com a identificação
            TextView tvName = new TextView(context);
            tvName.setLayoutParams(lp);
            tvName.setText("(" + person.getName()+ ")");

            ImageButton imageImageButton = new ImageButton(context);
            imageImageButton.setLayoutParams(lp);
//            imageImageButton.setBackgroundColor(Color.TRANSPARENT);
            switch (person.getId()) {
                case 1:
                    imageImageButton.setImageResource(R.drawable.icons8_farmer_48);
                    break;
                case 2:
                    imageImageButton.setImageResource(R.drawable.icons8_portrait_mode_female_48);
                    break;
                default:
                    imageImageButton.setImageResource(R.drawable.baseline_child_care_black_24dp);
            }

            //Botão descrementar
            ImageButton decrementBtn = new ImageButton(context);
            decrementBtn.setImageResource(R.drawable.baseline_arrow_back_ios_black_24dp);
            decrementBtn.setLayoutParams(lp);
//            decrementBtn.setBackgroundColor(Color.TRANSPARENT);
            decrementBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = "clicou em decrement";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    int value = Integer.parseInt(tvValue.getText().toString());
                    if(value > 0 ){
                        value --;
                    } else {
                        value = 0;
                    }
                    tvValue.setText(String.format("%d",value));
                    dbController.updatePeopleQuantity(person.getId(), value);

                }
            });

            //botão incrementar
            ImageButton incrementBtn = new ImageButton(context);
            incrementBtn.setImageResource(R.drawable.baseline_arrow_forward_ios_black_24dp);
//            incrementBtn.setBackgroundColor(Color.TRANSPARENT);
            incrementBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = "clicou em increment";
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    int value = Integer.parseInt(tvValue.getText().toString());
                    if(value < 5000 ){
                        value ++;
                    } else {
                        value = 4000;
                    }
                    tvValue.setText(String.format("%d",value));
                    dbController.updatePeopleQuantity(person.getId(), value);
                }
            });

            //montar ll horizontal e add no ll vertical
            llhorizontal.addView(imageImageButton);
            llhorizontal.addView(tvName);
            llhorizontal.addView(decrementBtn);
            llhorizontal.addView(tvValue);
            llhorizontal.addView(incrementBtn);

            linearLayout.addView(llhorizontal);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        linearLayout = findViewById(R.id.main_content);

        // Get singleton instance of database
        DBHelper databaseHelper = DBHelper.getInstance(this);
        dbController = new DBController(databaseHelper);

        //inicializa a primeira tela
        currentMenuItemID = R.id.navigation_people;
        displayViewForID(currentMenuItemID);

    }

    public void clearButton(View view) {
        dbController.resetData(getApplicationContext());
        displayViewForID(currentMenuItemID);

    }

    public void calculateButton(View view) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putString("umaString", "Olá");
        intent.putExtra("stringBundle", b);
        intent.putExtra("mensagem", "Mundo");
        intent.putExtra("umBoolean", true);
        intent.putExtra("umInteiro", 5);
        startActivity(intent);

    }
}
