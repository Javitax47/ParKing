package com.example.parking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModificarUsuarios extends AppCompatActivity {

    // Declaraciones de vistas
    private EditText editTextName, editTextEmail, editTextPassword;
    private Button buttonUpdate;

    // Instancias de Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ImageView imageViewProfile;
    private Button buttonChangePhoto;
    private static final int PICK_IMAGE_REQUEST = 1;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf);

        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inicialización de vistas
        editTextName = findViewById(R.id.editTextName);
        //editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        Button changePhotoButton = findViewById(R.id.changePhotoButton);
        Button alertas = findViewById(R.id.alertas);
        Button privacidad = findViewById(R.id.privacidad);

        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoDialog();
            }
        });

        // Manejar clic en el botón de actualización
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();

            }
        });
        alertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModificarUsuarios.this, alertas.class);
                startActivity(intent);
            }
        });
        privacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModificarUsuarios.this, privacidad.class);
                startActivity(intent);
            }
        });
        //imageViewProfile = findViewById(R.id.imageViewProfile);
       // buttonChangePhoto = findViewById(R.id.buttonChangePhoto);

        /*buttonChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an image picker intent
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });*/
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseUser users = mAuth.getCurrentUser();
            StorageReference profilePictureRef = storageRef.child("users/" + users.getUid() + ".jpg");
            //profilePictureRef.putFile(uri);

            // Upload the image to Firebase Storage
            UploadTask uploadTask = profilePictureRef.putFile(uri);
            uploadTask.addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Image uploaded successfully, get the download URL
                    profilePictureRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Guardar la URL de descarga en Firestore o en otro lugar
                        saveDownloadUrlToFirestore(downloadUri.toString());
                    });
                } else {
                    // Handle the error
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            });
            // Update the ImageView with the selected image
        }
    }
    private void saveDownloadUrlToFirestore(String downloadUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //String userId = user.getUid();
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("fotos_perfil").document(userId);

            // Actualizar el campo de la URL de la imagen en Firestore
            userRef.update("profilePictureUrl", downloadUrl)
                    .addOnSuccessListener(aVoid -> {
                        // Éxito al guardar la URL en Firestore
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error
                    });
        }
    }

    // Método para actualizar el perfil del usuario
    private void updateUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView correo = this.findViewById(R.id.correo);
        TextView nombre = this.findViewById(R.id.nombre);

        if (user != null) {
            // Obtiene los valores de los campos de edición
            String newName = editTextName.getText().toString().trim();
            //String newEmail = editTextEmail.getText().toString().trim();
            String newPassword = editTextPassword.getText().toString().trim();

            // Actualiza el perfil del usuario en Firebase Authentication
            if (!newName.isEmpty()) {
                user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(newName).build());
                //nombre.setText(usuario.getDisplayName());
            }
            /*if (!newEmail.isEmpty()) {
                user.updateEmail(newEmail);
                //correo.setText(usuario.getEmail());
            }*/
            if (!newPassword.isEmpty()) {
                user.updatePassword(newPassword);
            }
            /*String profilePicturePath = "users/" + user.getUid() + "/profile_picture.jpg";
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profilePicturePath)) // Set the photo URI
                    .build());*/

            // Actualiza los datos en la base de datos en tiempo real (Firebase Realtime Database)
            String userId = user.getUid();
            User updatedUser = new User(newName, newPassword/*, profilePicturePath*/); // Asumiendo que tienes una clase User
            mDatabase.child("users").child(userId).setValue(updatedUser);
            mAuth.signOut();

            // Abre la actividad LandingPage
            Intent intent = new Intent(ModificarUsuarios.this, LandingPageActivity.class);
            startActivity(intent);
            finish();
        }
    }

}