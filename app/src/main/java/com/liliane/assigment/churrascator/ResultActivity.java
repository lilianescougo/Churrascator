package com.liliane.assigment.churrascator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("stringBundle");
        String umaString = bundle.getString("umaString");
        String mensagem = intent.getStringExtra("mensagem");
    }
}
