package com.example.compras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUserId;
    private DatabaseReference  userRef;
    private String Telefono = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Bundle bundle= getIntent().getExtras();
        if (bundle!=null){
            Telefono = bundle.getString("phone");
        }

        auth = FirebaseAuth.getInstance();
        currentUserId  =auth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Usuarios");
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

    private void enviarLogin() {
        Intent intent = new Intent(PrincipalActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //verificar si el usuario esta en la base de datos
    private void verificarUsuarioExistente() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(currentUserId)){
                    enviarAlSetup();
                }
            }
            //si esta en la base de datos nos envia a el Setup
            private void enviarAlSetup() {
                Intent intent = new Intent(PrincipalActivity.this, SetupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("phone", Telefono);
                startActivity(intent);
                finish();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}