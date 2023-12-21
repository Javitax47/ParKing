package com.example.parking;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DeteccionService extends Service {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializa Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Inicia la lógica de comprobación
        startChecking();
    }

    private void startChecking() {
        // Puedes utilizar un handler para ejecutar la comprobación periódicamente
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkDeteccion();
                // Establece el intervalo de tiempo para la comprobación (por ejemplo, cada 5 segundos)
                handler.postDelayed(this, 5000);
            }
        }, 0);
    }

    private void checkDeteccion() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getEmail();

            // Consulta la colección de reservas_actuales para el usuario actual
            CollectionReference reservasCollection = db.collection("reservas_actuales");
            Query query = reservasCollection.whereEqualTo("usuario", userId);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Boolean deteccion = document.getBoolean("deteccion");
                        if (deteccion != null && deteccion) {
                            // El valor de "deteccion" ha cambiado a true, lanzar la actividad Deteccion
                            Intent intent = new Intent(DeteccionService.this, Deteccion.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            // Puedes agregar lógica adicional aquí si es necesario
                        }
                    }
                }
            });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
