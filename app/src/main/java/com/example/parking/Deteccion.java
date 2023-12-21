package com.example.parking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Arrays;
import java.util.Map;

public class Deteccion extends AppCompatActivity {
    Button btnno;
    Button btnsi;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deteccion);

        // Inicializa Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        restaurar_deteccion();

        btnno = findViewById(R.id.button4);
        SpannableString mitextoU = new SpannableString("No soy yo");
        mitextoU.setSpan(new UnderlineSpan(), 0, mitextoU.length(), 0);
        btnno.setText(mitextoU);

        btnsi = findViewById(R.id.button3);
        btnsi.setOnClickListener(v -> {
            // Obtener información de la reserva actual
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String email = currentUser.getEmail();

                // Consulta el documento correspondiente en la colección "reservas_actuales"
                db.collection("reservas_actuales").whereEqualTo("usuario", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Obtener información de la reserva
                                    String plazaId = "A2"; // El ID de la plaza

                                    // Actualizar el estado de la plaza en la colección "plaza"
                                    actualizarEstadoPlaza(plazaId);
                                    finish();
                                }
                            }
                        });
            }
        });
        btnno.setOnClickListener(v -> {
            // Guarda el estado actual en SharedPreferences
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("LayoutAnteriorVisible", true);
            editor.apply();

            restaurar_deteccion();
            finish();
        });
    }
    public void restaurar_deteccion() {
        // Obtiene el UID del usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Consulta la reserva correspondiente en la colección "reservas_actuales"
            db.collection("reservas_actuales")
                    .whereEqualTo("usuario", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtiene el ID del documento de reserva
                                String reservaId = document.getId();

                                // Actualiza el valor de "deteccion" en la reserva a nuevoEstado
                                db.collection("reservas_actuales").document(reservaId)
                                        .update("deteccion", false);
                            }
                        }
                    });
        }
    }

    private void actualizarEstadoPlaza(String plazaId) {
        // Obtener el documento de la plaza correspondiente en la colección "plaza"
        db.collection("plaza")
                .whereEqualTo("id", plazaId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Actualizar el estado de la plaza a "ocupado"
                            db.collection("plaza").document(document.getId())
                                    .update("estado", "ocupado");
                        }
                    }
                });
    }
}
