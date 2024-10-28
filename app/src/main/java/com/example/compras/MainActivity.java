package com.example.compras;

import androidx.appcompat.app.AppCompatActivity;

import android.app.assist.AssistContent;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Esto sirve para poder validar que cuando se ingrese, muestre el activity y nos direccione a otro activity

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(MainActivity.this, Home.class );
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}