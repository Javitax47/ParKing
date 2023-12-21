package com.example.parking;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Añadir_Vehiculos extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anadir_vehiculo);

        // Obtén una referencia al Spinner en tu actividad
        Spinner spinner = findViewById(R.id.spinner3);

        // Crea un array con las opciones
        String[] tiposVehiculo = {"Turismo", "Furgoneta o Camión", "Motos o Ciclomotores"};

        // Crea un adaptador de ArrayAdapter y asigna las opciones al Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposVehiculo);
        spinner.setAdapter(adapter);
    }
}