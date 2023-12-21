package com.example.parking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Tab2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Agrega un divisor entre los elementos del RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        List<Reserva> reservas = new ArrayList<>();
        // Agrega algunas reservas de ejemplo
        reservas.add(new Reserva("10:00 AM", "12:00 PM", "B3", "$10", "Calle San Juan 3"));
        reservas.add(new Reserva("02:00 PM", "04:00 PM", "A1", "$15", "Avenida Principal 15"));

        ReservaAdapter reservaAdapter = new ReservaAdapter(reservas);
        recyclerView.setAdapter(reservaAdapter);

        return rootView;
    }
}


