package com.example.compras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    private String CurrentUserId;
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
        CurrentUserId  =auth.getCurrentUser().getUid();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_principal_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       super.onOptionsItemSelected(item);
       if(item.getItemId()==R.id.nav_carrito){
            activityCarrito();
       }
       if(item.getItemId()==R.id.nav_categorias){
           activityCategorias();
       }
       if(item.getItemId()==R.id.nav_buscar){
           activityBuscar(); 
       }
       if(item.getItemId()==R.id.nav_perfil){
           activityPerfil();
       }
       if(item.getItemId()==R.id.nav_salir){
            auth.signOut();
            enviarLogin();
       }

       return true;
    }

    private void activityCarrito() {
        Toast.makeText(this, "Carrito", Toast.LENGTH_SHORT).show();

    }

    private void activityCategorias() {
        Toast.makeText(this, "Categorias", Toast.LENGTH_SHORT).show();
    }

    private void activityBuscar() {
        Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
    }

    private void activityPerfil() {
        Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PrincipalActivity.this,PerfilActivity.class);
        startActivity(intent);
    }

    //si esta en la base de datos nos envia a el Setup
    private void enviarAlSetup() {
        Intent intent = new Intent(PrincipalActivity.this, SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone", Telefono);
        startActivity(intent);
        finish();
    }

    private void enviarLogin() {
        Intent intent = new Intent(PrincipalActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}