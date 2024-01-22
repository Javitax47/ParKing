package com.example.parking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Tab4 extends Fragment {
    private TextView saldoTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab4, container, false);

        // Obtener referencias a las vistas
        TextView nombre = view.findViewById(R.id.nombre);
        TextView correo = view.findViewById(R.id.correo);
        TextView ia = view.findViewById(R.id.textIA);
        Button cerrarSesionButton = view.findViewById(R.id.btnLogOut);
        ImageView acercade = view.findViewById(R.id.acercaDe);
        TextView preferences = view.findViewById(R.id.preferences);


        // Obtener referencia al TextView 'saldo'
        saldoTextView = view.findViewById(R.id.saldo);

        // Obtener usuario actual
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) {
            // Mostrar los detalles del usuario
            nombre.setText(usuario.getDisplayName());
            correo.setText(usuario.getEmail());

            // Obtener el saldo del documento 'identificacion'
            obtenerSaldoDesdeFirestore(usuario.getEmail());
        }


        // Configurar el botón de cierre de sesión
        cerrarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar sesión de Firebase
                FirebaseAuth.getInstance().signOut();

                // Abrir la actividad activity.landing.page
                Intent intent = new Intent(getActivity(), LandingPageActivity.class);
                startActivity(intent);
            }
        });
        preferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Preferencias.class);
                startActivity(intent);
            }
        });
        // Intención de Asistente Virtual
        ia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la actividad
                Intent intent = new Intent(getActivity(), Asistente.class);
                startActivity(intent);
            }
        });

        // Configurar el click de la imagen "Acerca de..."
        acercade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un diálogo de alerta
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Acerca de ParKing")
                        .setMessage("Bienvenido a ParKing, tu solución inteligente para todas las necesidades de aparcamiento. Nuestra aplicación revolucionaria te permite visualizar de manera eficiente los aparcamientos disponibles, ocupados y reservados en un mapa intuitivo. Además, con funciones de recomendación, te dirigimos hacia el aparcamiento más cercano a tu ubicación actual o destino deseado. ¿Necesitas asegurarte un lugar? Con la opción de reserva de plazas, puedes hacerlo con facilidad y tranquilidad. Además, te ofrecemos la comodidad de encontrar el camino más corto a tu plaza reservada. ¿Preferirías elegir un espacio en particular? Con ParKing, puedes hacerlo con solo unos pocos toques. Y para garantizar la máxima seguridad, te ofrecemos la función de activar y desactivar un bloqueador de vehículos, tanto para proteger tu espacio de estacionamiento como para prevenir el robo de tu vehículo. Con ParKing, aparcar nunca ha sido tan sencillo y seguro.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Acción al presionar el botón "Aceptar" en el diálogo
                                dialog.dismiss(); // Cerrar el diálogo
                            }
                        });

                // Personalizar el botón "Aceptar"
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setTextColor(getResources().getColor(R.color.colorPrimaryDark)); // Reemplaza "your_color" con tu propio color
                    }
                });

                // Mostrar el diálogo
                dialog.show();
            }
        });

        return view;
    }

    private void obtenerSaldoDesdeFirestore(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("identificacion")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener el valor del campo 'saldo' y mostrarlo en el TextView
                        Long saldo = documentSnapshot.getLong("saldo");
                        if (saldo != null) {
                            saldoTextView.setText(String.valueOf(saldo)+ "€");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar errores
                });
    }
}