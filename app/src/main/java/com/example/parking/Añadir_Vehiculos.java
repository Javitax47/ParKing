package com.example.parking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Añadir_Vehiculos extends AppCompatActivity {

    private EditText Nombre;
    private EditText MarcaModelo;
    private EditText Matricula;
    private String tipo_vehiculo;
    private String tipo_uso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anadir_vehiculo);

        Spinner spinnerTipoVehiculo = findViewById(R.id.spinner3);
        String[] tiposVehiculo = {"Turismo", "Furgoneta o Camión", "Motos o Ciclomotores"};
        ArrayAdapter<String> adapterTipoVehiculo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposVehiculo);
        spinnerTipoVehiculo.setAdapter(adapterTipoVehiculo);
        spinnerTipoVehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Almacena el valor seleccionado del Spinner de tipo de vehículo
                tipo_vehiculo = tiposVehiculo[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        // Spinner para propósito del vehículo
        Spinner spinnerPropositoVehiculo = findViewById(R.id.spinner4);
        String[] propositoVehiculo = {"Personal", "Familiar/Amigo", "Alquiler"};
        ArrayAdapter<String> adapterPropositoVehiculo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, propositoVehiculo);
        spinnerPropositoVehiculo.setAdapter(adapterPropositoVehiculo);
        spinnerPropositoVehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Almacena el valor seleccionado del Spinner de propósito de vehículo
                tipo_uso = propositoVehiculo[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        // Inicializar las variables para los campos modificables
        Nombre = findViewById(R.id.editTextText3);
        MarcaModelo = findViewById(R.id.editTextText2);
        Matricula = findViewById(R.id.editTextText);

        Button buttonGuardar = findViewById(R.id.button4);
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarvehiculo();
            }

        });
    }


    private void guardarvehiculo() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario != null) {
            // Verificar si ya existe un vehículo con la misma matrícula
            firestore.collection("tus_vehiculos")
                    .whereEqualTo("matricula", Matricula.getText().toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                    // Ya existe un vehículo con la misma matrícula, manejar según sea necesario
                                    Toast.makeText(Añadir_Vehiculos.this, "Ya existe un vehículo con esa matrícula", Toast.LENGTH_SHORT).show();
                                } else {
                                    // No existe un vehículo con la misma matrícula, crea la nueva entrada
                                    Map<String, Object> reserva = new HashMap<>();
                                    reserva.put("matricula", Matricula.getText().toString());
                                    reserva.put("modelo", MarcaModelo.getText().toString());
                                    reserva.put("nombre_vehiculo", Nombre.getText().toString());
                                    reserva.put("tipo_uso", tipo_uso);
                                    reserva.put("tipo_vehiculo", tipo_vehiculo);

                                    firestore.collection("tus_vehiculos")
                                            .add(reserva)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    actualizarCampoVehiculos(usuario.getEmail(), Matricula.getText().toString());
                                                    Toast.makeText(Añadir_Vehiculos.this, "Vehículo guardado exitosamente", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Dentro de guardarvehiculo() después de procesar los datos y antes de mostrar los toasts
                                                    Toast.makeText(Añadir_Vehiculos.this, "Error al guardar el vehículo", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // Manejo de errores, si es necesario
                                Toast.makeText(Añadir_Vehiculos.this, "Error al verificar la existencia del vehículo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void actualizarCampoVehiculos(String correoUsuario, String nuevaMatricula) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Actualizar el campo "vehiculos" en el documento de identificación
        firestore.collection("identificacion")
                .document(correoUsuario)
                .update("vehiculos", FieldValue.arrayUnion(nuevaMatricula))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Manejar el éxito, si es necesario
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejo de errores, si es necesario
                    }
                });
    }
}