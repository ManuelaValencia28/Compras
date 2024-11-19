package com.example.compras;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private EditText nombre, ciudad, direccion, telefono;
    private Button guardar;
    private CircleImageView imagen;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserId;
    private static int Galery_Pick = 1;
    private StorageReference UserImagenPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        nombre = findViewById(R.id.perfil_nombre);
        ciudad = findViewById(R.id.perfil_ciudad);
        direccion = findViewById(R.id.perfil_direccion);
        telefono = findViewById(R.id.perfil_telefono);
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        dialog = new ProgressDialog(this);
        UserImagenPerfil = FirebaseStorage.getInstance().getReference().child("Perfil");
        guardar = findViewById(R.id.perfil_boton);
        imagen = findViewById(R.id.perfil_imagen);

        UserRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        // Obtener los valores
                        String nombres = snapshot.child("nombre").getValue(String.class);
                        String direccions = snapshot.child("direccion").getValue(String.class);
                        String ciudads = snapshot.child("ciudad").getValue(String.class);
                        String telefonos = snapshot.child("telefono").getValue(String.class);
                        String imagens = snapshot.child("imagen").getValue(String.class);

                        // Logs para depuración
                        Log.d("Firebase", "Nombre: " + nombres);
                        Log.d("Firebase", "Dirección: " + direccions);
                        Log.d("Firebase", "Ciudad: " + ciudads);
                        Log.d("Firebase", "Teléfono: " + telefonos);
                        Log.d("Firebase", "Imagen URL: " + imagens);

                        // Si la URL de la imagen no es nula ni vacía
                        if (imagens != null && !imagens.isEmpty()) {
                            Picasso.get()
                                    .load(imagens)
                                    .placeholder(R.drawable.logo)  // Cambia 'logo' por 'image' si es necesario
                                    .into(imagen);
                        } else {
                            imagen.setImageResource(R.drawable.logo); // Imagen predeterminada
                        }

                        // Establecer los valores de texto
                        nombre.setText(nombres != null ? nombres : "No disponible");
                        direccion.setText(direccions != null ? direccions : "No disponible");
                        ciudad.setText(ciudads != null ? ciudads : "No disponible");
                        telefono.setText(telefonos != null ? telefonos : "No disponible");

                    } else {
                        // Si el snapshot no existe, puedes manejar esto aquí.
                        imagen.setImageResource(R.drawable.logo); // Imagen predeterminada
                        nombre.setText("No disponible");
                        direccion.setText("No disponible");
                        ciudad.setText("No disponible");
                        telefono.setText("No disponible");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Si hay algún error, establece una imagen por defecto
                    imagen.setImageResource(R.drawable.logo); // Cambia 'logo' por 'image' si es necesario
                    nombre.setText("Error");
                    direccion.setText("Error");
                    ciudad.setText("Error");
                    telefono.setText("Error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejo del error de cancelación

            }
        });




        guardar.setOnClickListener(view -> GuardarInformacion());

        imagen.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); // Cambié la línea a "image/*"
            startActivityForResult(intent, Galery_Pick);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica que el código de la solicitud sea el esperado y que la imagen esté seleccionada
        if (requestCode == Galery_Pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData(); // Obtén la URI de la imagen seleccionada

            // Muestra el progreso mientras se sube la imagen
            dialog.setTitle("Subiendo imagen de perfil");
            dialog.setMessage("Por favor espere. Estamos procesando su foto");
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

            // Subir la imagen a Firebase Storage
            StorageReference filePath = UserImagenPerfil.child(CurrentUserId + ".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUri = uri.toString(); // Obtén la URL de descarga de la imagen

                        // Guarda la URL de la imagen en la base de datos de Firebase
                        UserRef.child(CurrentUserId).child("imagen").setValue(downloadUri).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Picasso.get().load(downloadUri).into(imagen); // Carga la imagen en la vista
                                dialog.dismiss();
                            } else {
                                String mensaje = task1.getException().getMessage();
                                Toast.makeText(PerfilActivity.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    });
                } else {
                    String mensaje = task.getException().getMessage();
                    Toast.makeText(PerfilActivity.this, "Error al subir la imagen: " + mensaje, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
    }

    private void GuardarInformacion() {
        String nombres = nombre.getText().toString().toUpperCase();
        String direccions = direccion.getText().toString();
        String ciudads = ciudad.getText().toString();
        String phones = telefono.getText().toString();

        if(TextUtils.isEmpty(nombres)){
            Toast.makeText(this, "Ingrese el nombre.", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(direccions)){
            Toast.makeText(this, "Ingrese la direccion.", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(ciudads)){
            Toast.makeText(this, "Ingrese la ciudad.", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(phones)){
            Toast.makeText(this, "Ingrese su número.", Toast.LENGTH_SHORT).show();
        }else{
            dialog.setTitle("Guardando");
            dialog.setMessage("Por favor espere...");
            dialog.show();;
            dialog.setCanceledOnTouchOutside(true);

            HashMap map = new HashMap();
            map.put("Nombre", nombres);
            map.put("Direccion", direccions);
            map.put("Ciudad", ciudads);
            map.put("Telefono", phones);
            map.put("uID", CurrentUserId);

            UserRef.child(CurrentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        EnviarAlInicio();
                        dialog.dismiss();
                    }else{
                        String mensaje = task.getException().toString();
                        Toast.makeText(PerfilActivity.this, "Error: "+mensaje, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void EnviarAlInicio() {
        Intent intent = new Intent(PerfilActivity.this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}