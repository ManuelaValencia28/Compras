package com.example.compras;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class FragmentTres extends Fragment {

    private View fragmento;
    private EditText nombre, ciudad, direccion, telefono;
    private Button guardar;
    private CircleImageView imagen;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserId;
    private static int Galery_Pick = 1;
    private StorageReference UserImagenPerfil;

    public FragmentTres() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmento = inflater.inflate(R.layout.fragment_tres, container, false);

        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Admin");
        dialog = new ProgressDialog(getContext());

        nombre = (EditText) fragmento.findViewById(R.id.perfilF_nombre);
        ciudad = (EditText) fragmento.findViewById(R.id.perfilF_ciudad);
        direccion = (EditText) fragmento.findViewById(R.id.perfilF_direccion);
        telefono = (EditText) fragmento.findViewById(R.id.perfilF_telefono);
        guardar = (Button) fragmento.findViewById(R.id.perfilF_boton);
        imagen = (CircleImageView) fragmento.findViewById(R.id.perfilF_imagen);

        UserImagenPerfil = FirebaseStorage.getInstance().getReference().child("Perfil");

        UserRef.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    // Obtén los valores del snapshot con comprobación nula
                    String nombres = snapshot.child("nombre").getValue(String.class);
                    String direccions = snapshot.child("direccion").getValue(String.class);
                    String ciudads = snapshot.child("ciudad").getValue(String.class);
                    String telefonos = snapshot.child("telefono").getValue(String.class);
                    String imagens = snapshot.child("imagen").getValue(String.class);

                    // Si el valor de "imagen" es nulo o vacío, carga una imagen por defecto
                    if (imagens != null && !imagens.isEmpty()) {
                        Picasso.get().load(imagens).placeholder(R.drawable.logo).into(imagen);
                    } else {
                        Picasso.get().load(R.drawable.logo).into(imagen);
                    }

                    // Asigna los valores a los EditText si no son nulos
                    nombre.setText(nombres != null ? nombres : "");
                    direccion.setText(direccions != null ? direccions : "");
                    ciudad.setText(ciudads != null ? ciudads : "");
                    telefono.setText(telefonos != null ? telefonos : "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        guardar.setOnClickListener(view -> GuardarInformacion());

        imagen.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); // Cambié la línea a "image/*"
            startActivityForResult(intent, Galery_Pick);
        });

        return fragmento;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                                Toast.makeText(getContext(), "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    });
                } else {
                    String mensaje = task.getException().getMessage();
                    Toast.makeText(getContext(), "Error al subir la imagen: " + mensaje, Toast.LENGTH_SHORT).show();
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

        if (TextUtils.isEmpty(nombres)) {
            Toast.makeText(getContext(), "Ingrese el nombre.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(direccions)) {
            Toast.makeText(getContext(), "Ingrese la direccion.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(ciudads)) {
            Toast.makeText(getContext(), "Ingrese la ciudad.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phones)) {
            Toast.makeText(getContext(), "Ingrese su número.", Toast.LENGTH_SHORT).show();
        } else {
            dialog.setTitle("Guardando");
            dialog.setMessage("Por favor espere...");
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

            HashMap<String, Object> map = new HashMap<>();
            map.put("Nombre", nombres);
            map.put("Direccion", direccions);
            map.put("Ciudad", ciudads);
            map.put("Telefono", phones);
            map.put("uID", CurrentUserId);

            UserRef.child(CurrentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        EnviarAlInicio();
                        dialog.dismiss();
                    } else {
                        String mensaje = task.getException().toString();
                        Toast.makeText(getContext(), "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private void EnviarAlInicio() {
        Intent intent = new Intent(getContext(), AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
