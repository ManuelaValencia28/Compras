package com.example.compras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        bottomNavigationView = findViewById(R.id.boton_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

           if(item.getItemId() == R.id.fragmentoUno){
               Fragments(new FragmentUno());
           }
            if(item.getItemId() == R.id.fragmentoDos){
                Fragments(new FragmentDos());
            }
            if(item.getItemId() == R.id.fragmentoTres   ){
                Fragments(new FragmentTres());
            }
            if(item.getItemId() == R.id.fragmentoCuatro){
                Fragments(new FragmentCuatro());
            }
            return true;
        }
    };

    private void Fragments(Fragment fragment){
       getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,fragment)
               .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
               .commit();
    }


}