package com.example.compras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
          this, drawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);

        TextView nombreHeader = (TextView) headerView.findViewById(R.id.usuario_nombre_perfil);
        CircleImageView imagenHeader = (CircleImageView) headerView.findViewById(R.id.usuario_imagen_perfil);



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

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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
       return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       int id= item.getItemId();

        if(id ==R.id.nav_carrito){
            activityCarrito();
        }
        else if(id ==R.id.nav_categorias){
            activityCategorias();
        }
        else if(id ==R.id.nav_buscar){
            activityBuscar();
        }
        else if(id ==R.id.nav_perfil){
            activityPerfil();
        }
        else if(id ==R.id.nav_salir){
            auth.signOut();
            enviarLogin();
        }

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
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