package com.example.parking;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    // Nombres de las pestañas
    private int[] imagenes = new int[]{R.drawable.mapa, R.drawable.menu, R.drawable.vehiculos, R.drawable.usuario};

    // Código de solicitud para los permisos
    private static final int REQUEST_LOCATION_PERMISSIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verificar y solicitar permisos en tiempo de ejecución
        if (checkPermissions()) {
            initializeApp();
        } else {
            requestPermissions();
        }
    }
    protected void initializeApp() {
        //Pestañas
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    // Dentro de onConfigureTab
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        View tabView = getLayoutInflater().inflate(R.layout.custom_tab, null);
                        ImageView imageView = tabView.findViewById(R.id.tab_image);
                        imageView.setImageResource(imagenes[position]); // Establecer la imagen aquí

                        // Ajusta el tamaño de la imagen aquí
                        imageView.getLayoutParams().height = 125;
                        imageView.getLayoutParams().width = 125;

                        tab.setCustomView(tabView);
                    }
                }).attach();
        // Deshabilitar el cambio de fragmentos deslizando horizontalmente
        viewPager.setUserInputEnabled(false);
    }
    private boolean checkPermissions() {
        // Verificar si los permisos ya están concedidos
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        // Solicitar permisos en tiempo de ejecución
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                REQUEST_LOCATION_PERMISSIONS
        );
    }
    // Método para manejar el resultado de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, inicializar la aplicación
                initializeApp();
            } else {
                // Permiso denegado, puedes mostrar un mensaje o realizar alguna acción adecuada
                Toast.makeText(this, "Los permisos son necesarios para el funcionamiento de la aplicación.", Toast.LENGTH_SHORT).show();
                finish(); // Puedes cerrar la actividad en este punto o tomar otra acción según tus necesidades
            }
        }
    }
    public class MiPagerAdapter extends FragmentStateAdapter {

        public MiPagerAdapter(FragmentActivity activity){
            super(activity);
        }

        @Override
        public int getItemCount() {
            return 4;
        }

        @Override @NonNull
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new Tab1();
                case 1: return new Tab2();
                case 2: return new Tab3();
                case 3: return new Tab4();
            }
            return null;
        }
    }
    public void boton(View view){
        Intent i = new Intent(this, ModificarUsuarios.class);
        startActivity(i);
    }
   /* public void alertas(View view){
        Intent i = new Intent(this, ModificarUsuarios.class);
        startActivity(i);
    }
    public void privacidad(View view){
        Intent i = new Intent(this, ModificarUsuarios.class);
        startActivity(i);
    }*/
}