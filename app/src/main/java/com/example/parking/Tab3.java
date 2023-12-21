package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Tab3 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Crea una lista de datos (puedes cambiar esto según tus necesidades)
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            dataList.add("Item " + i);
        }

        // Crea y establece el adaptador
        Tab3Adaptador adapter = new Tab3Adaptador(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

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
    // Función para redirigir a la actividad AnadirVehiculoActivity
    private void redirectToAnadirVehiculo() {
        Intent intent = new Intent(getActivity(), Añadir_Vehiculos.class);
        startActivity(intent);
    }

}

