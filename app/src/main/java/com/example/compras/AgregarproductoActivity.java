package com.example.compras;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private String Categoria, NombrePro, Desc, Precio, CurrentDate, CurrentTime;

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

        dialog = new ProgressDialog(this);
        
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
        NombrePro= nombrepro.getText().toString();
        Desc = descripcionpro.getText().toString();
        Precio = preciopro.getText().toString();
        if(imagenUri == null){
            Toast.makeText(this, "Primero agregar una imagen", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty((NombrePro))) {
            Toast.makeText(this, "Debes agregar el nombre del producto", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty((Desc))) {
            Toast.makeText(this, "Debes ingresar las descripcion del producto", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty((Precio))) {
            Toast.makeText(this, "Debes ingresar el precio del producto", Toast.LENGTH_SHORT).show();
        }else {
            GuardarInformacion();
        }
    }

    private void GuardarInformacion() {
        dialog.setTitle("Guardando producto");
        dialog.setMessage("por favor espere mientras guardamos el producto");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MM-dd-yy");
        CurrentDate = currentDateFormat.format(calendar.getTime());

        productoRandonKey = CurrentDate + CurrentTime;

        final StorageReference filePath = ProductoImagenRef.child(imagenUri.getLastPathSegment() + productoRandonKey + ".jpg");
        final UploadTask ulploadTask = filePath.putFile(imagenUri);

        ulploadTask.addOnFailureListener(new OnFailureListener() {


            @Override
            public void onFailure(@NonNull Exception e) {
                String mensaje = e.toString();

                Toast.makeText(AgregarproductoActivity.this, "Erro:" +mensaje , Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarproductoActivity.this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = ulploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        return null;
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            
                        }
                    }
                })
            }
        })
    }
}