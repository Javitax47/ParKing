package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextInputEditText editTextEmail, editTextPassword, editTextName;
    Button buttonReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextName = findViewById(R.id.name);
        buttonReg = findViewById(R.id.crearCuenta);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, nombre;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                nombre = String.valueOf(editTextName.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Introducir email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Introducir contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Tu contraseña debe tener 6 o más dígitos", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nombre)) {
                    Toast.makeText(RegisterActivity.this, "Introduce tu nombre y apellidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar el formato del correo electrónico
                if (!isValidEmail(email)) {
                    Toast.makeText(RegisterActivity.this, "El formato de correo no es correcto", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Separar el nombre y apellidos
                String[] partesNombre = nombre.split(" ");

                if (partesNombre.length == 1) {
                    Toast.makeText(RegisterActivity.this, "Introduce tus dos apellidos", Toast.LENGTH_SHORT).show();
                    return;
                } else if (partesNombre.length == 2) {
                    Toast.makeText(RegisterActivity.this, "Introduce tu segundo apellido", Toast.LENGTH_SHORT).show();
                    return;
                }

                String primerNombre = partesNombre.length > 0 ? partesNombre[0] : "";
                String apellidos = partesNombre.length > 2 ? partesNombre[1] + " " + partesNombre[2] : "";

                // Crear un mapa con los datos para la colección "identificacion"
                Map<String, Object> usuario = new HashMap<>();
                usuario.put("correo", email);
                usuario.put("contraseña", password);
                usuario.put("nombre", primerNombre);
                usuario.put("apellidos", apellidos);

                // Obtener una referencia a la colección "identificacion" en Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Añadir un nuevo documento a la colección "identificacion"
                db.collection("identificacion")
                        .add(usuario)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(RegisterActivity.this, "Documento creado con éxito en la colección 'identificacion'", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(RegisterActivity.this, "Error al crear el documento en la colección 'identificacion'", Toast.LENGTH_SHORT).show();
                        });

                // Continuar con la creación del usuario en Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Aquí se crea el perfil del usuario con el nombre
                            mAuth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nombre)
                                    .build()
                            );
                            Toast.makeText(RegisterActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private boolean isValidEmail(String email) {
                // Esta función utiliza una expresión regular para verificar el formato del correo electrónico
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });
    }
}