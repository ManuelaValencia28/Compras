package com.example.compras;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private EditText numero, codigo;
    private Button enviarnumero, enviarcodigo;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String VerificacionID;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        numero = (EditText) findViewById(R.id.numero);
        codigo = (EditText) findViewById(R.id.codigo);
        enviarnumero = (Button) findViewById(R.id.enviarnumero);
        enviarcodigo = (Button) findViewById(R.id.enviarcodigo);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        enviarnumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = numero.getText().toString();
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(Login.this,"INGRESE UN NÚMERO DE CELULAR.", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setTitle("VALIDANDO NÚMERO");
                    dialog.setMessage("Por favor espere mientras validamos su número.");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(Login.this)
                            .setCallbacks(callbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(options); //Este metodo envia el numero...
                }
            }
        });
        enviarcodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numero.setVisibility(View.GONE);
                enviarnumero.setVisibility(View.GONE);
                String VerificacionCode = codigo.getText().toString();
                if(TextUtils.isEmpty(VerificacionCode)){
                    Toast.makeText(Login.this,"INGRESE EL CODIGO RECIBIDO", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setTitle("VERIFICANDO...");
                    dialog.setMessage("Espere por favor...");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificacionID, VerificacionCode);
                    IngresadoConExito(credential);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                IngresadoConExito(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                Toast.makeText(Login.this,"FALLO EL INICIO, Causas: \n1. Número invalido. \n2. Sin conexión a Internet.", Toast.LENGTH_LONG).show();
                numero.setVisibility(View.VISIBLE);
                enviarnumero.setVisibility(View.VISIBLE);
                codigo.setVisibility(View.GONE);
                enviarcodigo.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken Token) {
                VerificacionID = s;
                resendingToken = Token;
                dialog.dismiss();
                Toast.makeText(Login.this, "CODIGO ENVIADO SATISFACTORIAMENTE, Reciba tu bandeja de entrada.", Toast.LENGTH_LONG).show();
                numero.setVisibility(View.GONE);
                enviarnumero.setVisibility(View.GONE);
                codigo.setVisibility(View.VISIBLE);
                enviarcodigo.setVisibility(View.VISIBLE);
            }
        };
    }

    private void IngresadoConExito(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(Login.this, "INGRESO CON EXITO.", Toast.LENGTH_LONG).show();
                    EnviaralaPrincipal();
                } else {
                    String err = task.getException().toString();
                    Toast.makeText(Login.this, "ERROR: " + err, Toast.LENGTH_SHORT).show();
                    ;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null){
            EnviaralaPrincipal();
        }
    }

    private void EnviaralaPrincipal() {
        Intent intent = new Intent(Login.this, PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("Phone", phoneNumber);
        startActivity(intent);
        finish();
    }

}