package com.example.parking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ParkingInfoBottomSheetFragment extends BottomSheetDialogFragment {

    public static final String ARG_TITLE = "arg_title";
    private static final String ARG_SNIPPET = "arg_snippet";

    public ParkingInfoBottomSheetFragment() {
        // Constructor vacío necesario
    }

    public static ParkingInfoBottomSheetFragment newInstance(String title, String snippet) {
        ParkingInfoBottomSheetFragment fragment = new ParkingInfoBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_SNIPPET, snippet);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtén los argumentos
        String title = getArguments().getString(ARG_TITLE, "");
        String snippet = getArguments().getString(ARG_SNIPPET, "");

        // Muestra la información en el desplegable
        TextView titleTextView = view.findViewById(R.id.titulo);
        TextView snippetTextView = view.findViewById(R.id.snippet);
        Button boton = view.findViewById(R.id.button2);

        titleTextView.setText(title);
        snippetTextView.setText(snippet);

        // Agrega un OnClickListener al botón
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para la actividad CrearReserva
                Intent intent = new Intent(getActivity(), CrearReserva.class);

                // Agrega el título como extra al Intent
                intent.putExtra(ARG_TITLE, title);

                // Inicia la actividad con el Intent
                startActivity(intent);
            }
        });
    }
}
