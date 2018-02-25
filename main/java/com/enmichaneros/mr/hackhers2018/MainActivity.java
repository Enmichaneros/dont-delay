package com.enmichaneros.mr.hackhers2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button shortButton;
    Button longButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shortButton = findViewById(R.id.buttonShort);
        longButton = findViewById(R.id.buttonLong);

        shortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), DataActivity.class);
                i.putExtra("range", 250);
                startActivity(i);
            }
        });

        longButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), DataActivity.class);
                i.putExtra("range", 500);
                startActivity(i);
            }
        });

    }
}
