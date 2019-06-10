package com.liliane.assigment.churrascator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    public static final String PREFS_NAME = "MyPrefsFile";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

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
            decrementBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            incrementBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

        //Cria a tabela, se for a primeira vez que abre o aplicativo
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean openAppforTheFirstTime = settings.getBoolean("openAppforTheFirstTime", true);
        if(openAppforTheFirstTime) {
            //guarda que já abriu o app
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("openAppforTheFirstTime", false);
            editor.commit();
            //cria a tabela vazia
            dbController.resetData(getApplicationContext());
        }


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
        intent.putExtra("resultString", calculateResult());
        startActivity(intent);
    }

    private String calculateResult() {
        People homem, mulher, crianca;
        List<People> people = dbController.getPeopleList();
        homem = people.get(0);
        mulher = people.get(1);
        crianca = people.get(2);
        if((homem.getQuantity() == 0) && (mulher.getQuantity() == 0) && (crianca.getQuantity() == 0)) {
            return getString(R.string.pleaseSelectAtLeastOnePerson);
        }

        //Calcula carne e guarnição, se houver arroz a quantidade de carne cai pela metade.
        double maminhaKg = 0, picanhaKg = 0, arrozKg = 0, mandiocaKg = 0;

        List<GroceryItem> carne = dbController.getAllGroceryItemsfromSession(GroceryItem.SESSION_MEATS);
        GroceryItem maminha = carne.get(0);
        GroceryItem picanha  = carne.get(1);

        List<GroceryItem> guarnicao = dbController.getAllGroceryItemsfromSession(GroceryItem.SESSION_OTHERS);
        GroceryItem mandioca = guarnicao.get(0);
        GroceryItem arroz = guarnicao.get(1);

        double totalKg = homem.getQuantity()*homem.getEats() +
                mulher.getQuantity()*mulher.getEats() +
                crianca.getQuantity() * crianca.getEats();

        //Calcula carne
        if (!picanha.isChecked() && !maminha.isChecked()){
                return getString(R.string.noMeatError);
        }
        if(maminha.isChecked()) {
            maminhaKg = totalKg;
        }
        if(picanha.isChecked()) {
            picanhaKg = totalKg;;
        }
        if(maminha.isChecked() && picanha.isChecked()){
            maminhaKg *= 0.7;
            picanhaKg *= 0.3;
        }

        if(mandioca.isChecked()) {
            mandiocaKg = totalKg * 0.4;
        }

        if (arroz.isChecked()) {
            arrozKg = totalKg * 0.5;

            if(maminhaKg != 0) maminhaKg /= 2;
            if(picanhaKg != 0) picanhaKg /= 2;
            if(mandioca.isChecked()) {
                arrozKg *= 0.6;
                mandiocaKg *= 0.5;
            }
        }

        //calcula bebidas
        double refrigeranteMl = 0, cervejaMl = 0 ;
        List<GroceryItem> bebida = dbController.getAllGroceryItemsfromSession(GroceryItem.SESSION_DRINKS);
        GroceryItem refrigerante = bebida.get(0);
        GroceryItem cerveja = bebida.get(1);

        if(refrigerante.isChecked() && !cerveja.isChecked()) {
            refrigeranteMl = homem.getQuantity() * homem.getDrinks() +
                    mulher.getQuantity() * mulher.getDrinks() +
                    crianca.getQuantity() * mulher.getDrinks();
        }

        if(!refrigerante.isChecked() && cerveja.isChecked()) {
            cervejaMl = homem.getQuantity() * homem.getDrinks() +
                    mulher.getQuantity() * mulher.getDrinks();
        }


        double totalPrice = 0 ;
        String kgOf = " Kg " + getString(R.string.of) + " ";
        String L_Of = " L " + getString(R.string.of) + " ";
        String resultString = "Churrascator - Calculadora de churrasco \n\n";
        resultString += "\n" + getString(R.string.peopleList) + "\n";
        if (homem.getQuantity() > 0) resultString += homem.getName() + ": " +  homem.getQuantity() + "\n";
        if (mulher.getQuantity() > 0) resultString += mulher.getName() + ": " +  mulher.getQuantity() + "\n";
        if (crianca.getQuantity() > 0) resultString += crianca.getName() + ": " +  crianca.getQuantity() + "\n";
        resultString += "\n" + getString(R.string.groceryList) + "\n";
        if(maminhaKg > 0 ) {
            double maminhaTotalPrice = maminhaKg * maminha.getPrice();
            totalPrice += maminhaTotalPrice;
            resultString += maminhaKg + kgOf + maminha.getName() + " (" + getString(R.string.price) + ": " + maminhaTotalPrice + ")\n";
        }
        if(picanhaKg > 0 ) {
            double picanhaTotalPrice = picanhaKg * picanha.getPrice();;
            totalPrice += picanhaTotalPrice;
            resultString += picanhaKg + kgOf + picanha.getName() + " (" + getString(R.string.price) + ": " + picanhaTotalPrice + ")\n";
        }
        if(arrozKg > 0 ) {
            double arrozTotalPrice = arrozKg * arroz.getPrice();;
            totalPrice += arrozTotalPrice;
            resultString += picanhaKg + kgOf + arroz.getName() + " (" + getString(R.string.price) + ": " + arrozTotalPrice + ")\n";
        }
        if(mandiocaKg > 0 ) {
            double mandiocaTotalPrice = mandiocaKg * mandioca.getPrice();
            totalPrice += mandiocaTotalPrice;
            resultString += mandiocaKg + kgOf + mandioca.getName() + " (" + getString(R.string.price) + ": " + mandiocaTotalPrice + ")\n";
        }

        if(refrigeranteMl > 0 ) {
            double refrigeranteTotalPrice = refrigeranteMl * refrigerante.getPrice();
            totalPrice += refrigeranteTotalPrice;
            resultString += refrigeranteMl/1000 + L_Of + refrigerante.getName() + " (" + getString(R.string.price) + ": " + refrigeranteTotalPrice + ")\n";
        }

        if(cervejaMl > 0 ) {
            double cervejaTotalPrice = cervejaMl * cerveja.getPrice();
            totalPrice += cervejaTotalPrice;
            resultString += cervejaMl/1000 + L_Of + mandioca.getName() + " (" + getString(R.string.price) + ": " + cervejaTotalPrice + ")\n";
        }

        resultString += "\n" + getString(R.string.totalPrice) + ": " + totalPrice;

        return resultString;
    }
}
