package com.liliane.assigment.churrascator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {
    TextView textViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String resultString = getIntent().getStringExtra("resultString");
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setText(resultString);
    }

    public void shareResultButton(View view) {
        String texto = getString(R.string.functionWasNotImplementedYet);
        Toast toast = Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void backButton(View view) {
        onBackPressed();
    }
}
