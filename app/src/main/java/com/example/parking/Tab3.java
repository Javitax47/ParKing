package com.example.parking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Tab3 extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    public List<String> vehiculos = new ArrayList<>();
    private static final int REQUEST_CODE_AÑADIR_VEHICULO = 1;
    private RecyclerView recyclerView;
    private Tab3Adaptador adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        Button button3 = view.findViewById(R.id.button3);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar a la función para redirigir al nuevo layout
                redirectToAnadirVehiculo();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Actualizar datos cuando el Fragment vuelve a estar en primer plano
        refreshData();
    }

    private void refreshData() {
        // Obtén el email del usuario actual
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Obtén el documento correspondiente al usuario actual
        DocumentReference userDocRef = db.collection("identificacion").document(userEmail);
        CollectionReference tusVehiculosRef = FirebaseFirestore.getInstance().collection("tus_vehiculos");
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtiene la lista de vehículos del documento
                    vehiculos = (List<String>) documentSnapshot.get("vehiculos");

                    // Nueva lista para almacenar los detalles de los vehículos
                    List<String> vehicleDetailsList = new ArrayList<>();

                    // Realiza la consulta a la colección tus_vehiculos
                    for (String matricula : vehiculos) {
                        tusVehiculosRef.whereEqualTo("matricula", matricula)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot vehicleSnapshot : queryDocumentSnapshots) {
                                            // Obtiene los datos del vehículo
                                            String nombreVehiculo = vehicleSnapshot.getString("nombre_vehiculo");
                                            String matricula = vehicleSnapshot.getString("matricula");
                                            String modelo = vehicleSnapshot.getString("modelo");

                                            // Construye el detalle del vehículo y agrega a la lista
                                            String vehicleDetails = nombreVehiculo + "\n" + matricula + "\n" + modelo;
                                            vehicleDetailsList.add(vehicleDetails);
                                        }

                                        // Si el adaptador ya existe, actualízalo; de lo contrario, créalo
                                        if (adapter == null) {
                                            adapter = new Tab3Adaptador(requireContext(), vehicleDetailsList);
                                            recyclerView.setAdapter(adapter);
                                        } else {
                                            adapter.updateData(vehicleDetailsList);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    // Función para redirigir a la actividad AnadirVehiculoActivity
    private void redirectToAnadirVehiculo() {
        Intent intent = new Intent(getActivity(), Añadir_Vehiculos.class);
        startActivity(intent);
    }
}
