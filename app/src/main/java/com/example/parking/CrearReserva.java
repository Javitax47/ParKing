package com.example.parking;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CrearReserva extends AppCompatActivity {
    private static final int NUMERO_DE_VISTAS = 3; // Cambia esto al número real de vistas que necesitas
    private ViewPager viewPager;
    private List<View> pages;
    private CrearReservaAdapter adapter;
    private int currentPage = 0;
    private TextView nombre;
    private Button reservar;
    Button plaza1, plaza2;
    private FirebaseFirestore firestore;
    private String plazaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserva);
        viewPager = findViewById(R.id.viewPager); // Replace with the actual ID from your layout
        firestore = FirebaseFirestore.getInstance();
        pages = new ArrayList<>();

        // Obtiene el valor del título del Intent
        String title = getIntent().getStringExtra(ParkingInfoBottomSheetFragment.ARG_TITLE);
        nombre = findViewById(R.id.textView16);
        nombre.setText(title);

        reservar = findViewById(R.id.button5);
        LayoutInflater inflater = getLayoutInflater();

        plaza1 = findViewById(R.id.plaza1);
        plaza2 = findViewById(R.id.plaza2);

        final String[] estado = {null};
        try {
            estado[0] = new obtenerEstadoPlaza().execute("A1").get();
            if ("disponible".equals(estado[0])) {
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
                plaza1.setBackgroundColor(color);
                plaza1.setText("A1\nDISPONIBLE");
                plaza1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorError);
                plaza1.setBackgroundColor(color);
                plaza1.setText("A1\nOCUPADO");
                plaza1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            estado[0] = new obtenerEstadoPlaza().execute("A2").get();
            if ("disponible".equals(estado[0])) {
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
                plaza2.setBackgroundColor(color);
                plaza2.setText("A2\nDISPONIBLE");
                plaza2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorError);
                plaza2.setBackgroundColor(color);
                plaza2.setText("A2\nOCUPADO");
                plaza2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        plaza1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plazaSeleccionada = "A1";
                try {
                    estado[0] = new obtenerEstadoPlaza().execute(plazaSeleccionada).get();
                    if ("disponible".equals(estado[0])) {
                        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
                        reservar.setBackgroundColor(color);
                    } else {
                        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPlazaInhabilitada);
                        reservar.setBackgroundColor(color);
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        plaza2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plazaSeleccionada = "A2";
                try {
                    estado[0] = new obtenerEstadoPlaza().execute(plazaSeleccionada).get();
                    if ("disponible".equals(estado[0])) {
                        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
                        reservar.setBackgroundColor(color);
                    } else {
                        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPlazaInhabilitada);
                        reservar.setBackgroundColor(color);
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    estado[0] = new obtenerEstadoPlaza().execute(plazaSeleccionada).get();
                    if("disponible".equals(estado[0])){
                        crearReserva(plazaSeleccionada, title);
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                        firestore.collection("plaza")
                                .whereEqualTo("id", plazaSeleccionada)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // Actualizar el estado de la plaza a "ocupado"
                                            firestore.collection("plaza").document(document.getId())
                                                    .update("estado", "reservado");
                                        }

                                        // Cierra la actividad actual y la reinicia
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                });
                    }
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            estado[0] = new obtenerEstadoPlaza().execute("A1").get();
            if ("disponible".equals(estado[0])) {
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
                plaza1.setBackgroundColor(color);
                plaza1.setText("A1\nDISPONIBLE");
                plaza1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorError);
                plaza1.setBackgroundColor(color);
                plaza1.setText("A1\nOCUPADO");
                plaza1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            estado[0] = new obtenerEstadoPlaza().execute("A2").get();
            if ("disponible".equals(estado[0])) {
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
                plaza2.setBackgroundColor(color);
                plaza2.setText("A2\nDISPONIBLE");
                plaza2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorError);
                plaza2.setBackgroundColor(color);
                plaza2.setText("A2\nOCUPADO");
                plaza2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Añade tantas páginas como necesites
        for (int i = 0; i < NUMERO_DE_VISTAS; i++) {
            View page = inflater.inflate(R.layout.reserva_item, null);

            // Configura los elementos de la página según sea necesario
            TextView textView1 = page.findViewById(R.id.textView1);
            TextView textView2 = page.findViewById(R.id.textView2);
            ImageView imageView = page.findViewById(R.id.imageView);

            // Configura los valores de los elementos según la posición de la página
            textView1.setText("Texto " + (i + 1));
            textView2.setText("Texto " + (i + 2));

            // Carga automáticamente la imagen correspondiente
            String imageName = "vehiculo" + (i + 1);
            int imageResourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            imageView.setImageResource(imageResourceId);

            pages.add(page);
        }

        // Añade la última página
        View lastPage = inflater.inflate(R.layout.reserva_sin_vehiculo, null);
        pages.add(lastPage);

        adapter = new CrearReservaAdapter(pages);
        viewPager.setAdapter(adapter);

        // Configura un listener para detectar el cambio de página
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // No es necesario implementar nada aquí
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // No es necesario implementar nada aquí
            }
        });
    }
    private void crearReserva(String plaza, String title) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

        // Realiza una consulta para verificar si ya existe una reserva con los mismos valores
        firestore.collection("reservas_actuales")
                .whereEqualTo("Parking", title)
                .whereEqualTo("plaza", plaza)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Verifica si ya existe una reserva con los mismos valores
                            if (task.getResult() != null && !task.getResult().isEmpty()) {
                                // Ya existe una reserva, manejarlo según tus necesidades
                                Toast.makeText(CrearReserva.this, "Ya existe una reserva con los mismos valores", Toast.LENGTH_SHORT).show();
                            } else {
                                // No existe una reserva con los mismos valores, crea la nueva reserva
                                Map<String, Object> reserva = new HashMap<>();
                                reserva.put("Parking", title);
                                reserva.put("plaza", plaza);
                                reserva.put("periodo_inicio", FieldValue.serverTimestamp());
                                reserva.put("usuario", usuario.getEmail());
                                reserva.put("deteccion", false);

                                firestore.collection("reservas_actuales")
                                        .add(reserva)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                // La reserva se creó exitosamente
                                                Toast.makeText(CrearReserva.this, "Reserva creada exitosamente", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Manejo de errores, si es necesario
                                                Toast.makeText(CrearReserva.this, "Error al crear la reserva", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Manejo de errores, si es necesario
                            Toast.makeText(CrearReserva.this, "Error al verificar la existencia de la reserva", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void onButton1Click(View view) {
        // Lógica para cambiar a la siguiente vista al hacer clic en Button1
        if (currentPage < NUMERO_DE_VISTAS - 1) {
            currentPage++;
            viewPager.setCurrentItem(currentPage);
        }
    }

    public void onButton2Click(View view) {
        // Lógica para cambiar a la vista anterior al hacer clic en Button2
        if (currentPage > 0) {
            currentPage--;
            viewPager.setCurrentItem(currentPage);
        }
    }
    public class obtenerEstadoPlaza extends AsyncTask<String, Void, String> {
        private Button plazaButton; // Agrega esta variable
        private String idPlaza;
        @Override
        protected String doInBackground(String... params) {
            idPlaza = params[0];
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            Task<QuerySnapshot> task = firestore.collection("plaza")
                    .whereEqualTo("id", idPlaza)
                    .get();

            try {
                QuerySnapshot queryDocumentSnapshots = Tasks.await(task);

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    String estado = document.getString("estado");
                    return estado; // Solo necesitamos la primera coincidencia
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace(); // Manejar la excepción según tus necesidades
            }
            return null;
        }

    }
}