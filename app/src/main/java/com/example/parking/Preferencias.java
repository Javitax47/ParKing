package com.example.parking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class Preferencias extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf);

        FirebaseApp.initializeApp(this);

        Button usernameButton = findViewById(R.id.usernameButton);
        Button emailButton = findViewById(R.id.emailButton);
        Button changePhotoButton = findViewById(R.id.changePhotoButton);

        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoDialog();
            }
        });

        usernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("Nombre de Usuario", "Usuario123");
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog("Correo Electrónico", "ejemplo@correo.com");
            }
        });
    }
    private void openPhotoDialog() {
        // Inflar el diseño personalizado del cuadro de diálogo.
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_conf_foto, null);

        // Obtener referencias a los elementos del cuadro de diálogo.
        CircleImageView photoImageView = dialogView.findViewById(R.id.photoImageView);
        Button editButton = dialogView.findViewById(R.id.editButton);

        // Configurar la imagen en el CircleImageView.
        // Puedes establecer la imagen predeterminada o la imagen actual del usuario aquí.
        photoImageView.setImageResource(R.drawable.profile);

        // Crear y mostrar el cuadro de diálogo.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Manejar clic en el botón de edición.
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Aquí puedes manejar la lógica cuando el usuario hace clic en "Aceptar".
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Manejar la lógica cuando el usuario hace clic en "Cancelar".
            }
        });

        builder.show();
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    private void showInputDialog(String title, String previousData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Inflar el diseño personalizado del diálogo.
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_conf, null);
        builder.setView(dialogView);

        // Obtener referencias a los elementos del diseño personalizado.
        EditText newDataEditText = dialogView.findViewById(R.id.newDataEditText);

        // Establecer el texto del dato anterior como el hint del EditText.
        newDataEditText.setHint(previousData);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener el nuevo dato introducido por el usuario.
                String newData = newDataEditText.getText().toString();

                // Aquí maneja la lógica para guardar los nuevos datos en Firebase.
                updateUserProfileData(title, newData);
            }
        });

        builder.show();
    }
    private void updateUserProfileData(String field, String newData) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

            // Actualizar el campo correspondiente en la base de datos.
            switch (field) {
                case "Nombre de Usuario":
                    userRef.child("username").setValue(newData);
                    break;
                case "Correo Electrónico":
                    userRef.child("email").setValue(newData);
                    break;
            }
        }
    }

}
