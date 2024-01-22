package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Tab2 extends Fragment {

    private FirebaseFirestore db;
    private Button buscar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2, container, false);

        buscar = rootView.findViewById(R.id.buscar);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Agrega un divisor entre los elementos del RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        List<Reserva> reservas = new ArrayList<>();

        // Configura la instancia de FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Accede a la colección "reservas_actuales" y obtén los documentos
        db.collection("reservas_actuales")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Extrae los campos del documento
                            com.google.firebase.Timestamp firebaseTimestamp = document.getTimestamp("periodo_inicio");

                            // Verifica si el Timestamp de Firebase no es nulo antes de la conversión
                            if (firebaseTimestamp != null) {
                                // Convierte el Timestamp de Firebase a java.sql.Timestamp
                                java.sql.Timestamp periodoInicio = new java.sql.Timestamp(firebaseTimestamp.getSeconds() * 1000);

                                String plaza = document.getString("plaza");
                                String parking = document.getString("Parking");

                                // Agrega la reserva a la lista
                                reservas.add(new Reserva(periodoInicio, plaza, "5€", parking));
                            } else {
                                // Handle the case where "periodo_inicio" is null
                                // You may choose to skip this document or handle it differently based on your requirements
                            }
                        }

                        // Crea el adaptador con la lista de reservas obtenidas de Firestore
                        ReservaAdapter reservaAdapter = new ReservaAdapter(reservas);
                        recyclerView.setAdapter(reservaAdapter);
                    } else {
                        // Maneja el error al obtener los datos de Firestore
                    }
                });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("plaza")
                        .whereEqualTo("id", "A2")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // Obtiene el primer documento con id=A2
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                // Actualiza el campo "encontrar" a True
                                db.collection("plaza")
                                        .document(document.getId())
                                        .update("encontrar", true)
                                        .addOnSuccessListener(aVoid -> {
                                            // Maneja el éxito de la actualización
                                            Toast.makeText(requireContext(), "Busca a tu alrededor", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Maneja el error en la actualización
                                            Toast.makeText(requireContext(), "Error al buscar tu plaza", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });
            }
        });

        return rootView;
    }
}




