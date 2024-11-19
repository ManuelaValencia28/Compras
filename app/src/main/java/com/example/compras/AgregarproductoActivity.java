package com.example.compras;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AgregarproductoActivity extends AppCompatActivity {

    private ImageView imagenpro;
    private EditText nombrepro, descripcionpro, preciopro;
    private Button agregarpro;
    private static final int Gallery_Pick = 1;
    private Uri imagenUri;
    private String productoRandonKey, downloadUri;
    private StorageReference ProductoImagenRef;
    private DatabaseReference ProductoRef;
    private ProgressDialog dialog;
    private String Categoria, NombrePro, Desc, Precio ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregarproducto);

        Categoria = getIntent().getExtras().get("categoria").toString();
        ProductoImagenRef = FirebaseStorage.getInstance().getReference().child("Imagenes Productos");
        ProductoRef = FirebaseDatabase.getInstance().getReference().child("Productos");

        Toast.makeText(this, Categoria,Toast.LENGTH_SHORT).show();


        imagenpro = (ImageView) findViewById(R.id.imagenpro);
        nombrepro = (EditText) findViewById(R.id.nombrepro);
        descripcionpro = (EditText) findViewById(R.id.descripcionpro);
        preciopro = (EditText) findViewById(R.id.preciopro);
        agregarpro = (Button) findViewById(R.id.agregarpro);
        
        imagenpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirGaleria();
            }
        });
        agregarpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidarProducto();
            }
        });

    }

    private void AbrirGaleria() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/");
        startActivityForResult(intent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            imagenUri = data.getData();
            imagenpro.setImageURI(imagenUri);


        }
    }

    private void ValidarProducto() {
    }
}