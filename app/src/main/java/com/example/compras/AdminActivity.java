package com.example.compras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private String CurrentUserId;
    private DatabaseReference userRef;
    private String Telefono = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        bottomNavigationView = findViewById(R.id.boton_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        Bundle bundle= getIntent().getExtras();
        if (bundle!=null){
            Telefono = bundle.getString("phone");
        }

        auth = FirebaseAuth.getInstance();
        CurrentUserId  =auth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Admin");
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser == null){
            enviarLogin();
        }else{
            verificarUsuarioExistente();
        }
    }

    //verificar si el usuario esta en la base de datos
    private void verificarUsuarioExistente() {
        final String CurrentUserId = auth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(CurrentUserId)){
                    enviarAlSetup();
                }
            }  @Override public void onCancelled(@NonNull DatabaseError error) { }});
    }
    //si esta en la base de datos nos envia a el Setup
    private void enviarAlSetup() {
        Intent intent = new Intent(AdminActivity.this, SetupadminActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone", Telefono);
        startActivity(intent);
        finish();
    }

    private void enviarLogin() {
        Intent intent = new Intent(AdminActivity.this, LoginAdmin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}