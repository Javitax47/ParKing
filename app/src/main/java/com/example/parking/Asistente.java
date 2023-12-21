package com.example.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Asistente extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    ImageView serverStatusImage;
    TextView serverStatusText;
    public String url = "http://asistenteartificial.ddns.net:1234/v1/chat/completions";
    private static final long CHECK_CONNECTION_INTERVAL = 5000; // 5 segundos
    private Handler handler = new Handler();
    private Runnable checkConnectionRunnable;
    private boolean systemMessageAdded = false;
    private JSONArray messagesArray = new JSONArray();
    JSONObject jsonBody = new JSONObject();
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(600, TimeUnit.SECONDS) // Adjust the timeout as needed
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ia);
        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);
        serverStatusImage = findViewById(R.id.online_status);
        serverStatusText = findViewById(R.id.online_status_text);

        // Llamada al método para verificar la conexión antes de realizar la llamada a la API
        checkServerConnection();

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            addToChat(question,Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
        });

        // Programar la verificación de conexión periódicamente
        checkConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                checkServerConnection();
                handler.postDelayed(this, CHECK_CONNECTION_INTERVAL);
            }
        };

        handler.postDelayed(checkConnectionRunnable, CHECK_CONNECTION_INTERVAL);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Detener la verificación de conexión al destruir la actividad
        handler.removeCallbacks(checkConnectionRunnable);
    }
    private void updateServerStatusImage(boolean isConnected) {
        runOnUiThread(() -> {
            if (isConnected) {
                serverStatusImage.setImageResource(R.drawable.online); // Cambia a la imagen "online"
                serverStatusText.setText("Online");
            } else {
                serverStatusImage.setImageResource(R.drawable.offline); // Cambia a la imagen "offline"
                serverStatusText.setText("Offline");
            }
        });
    }

    private void checkServerConnection() {
        Request request = new Request.Builder()
                .url(url) // URL de tu servidor
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                updateServerStatusImage(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    updateServerStatusImage(true);
                } else {
                    updateServerStatusImage(false);
                }
            }
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response) {
        runOnUiThread(() -> {
            if (!messageList.isEmpty()) {
                messageList.remove(messageList.size() - 1);
                addToChat(response, Message.SENT_BY_BOT);
            }
        });
    }


    void callAPI(String question) {
        // okhttp
        addToChat("Escribiendo... ", Message.SENT_BY_BOT);

        try {
            // Crear un nuevo objeto para el mensaje del usuario
            JSONObject userMessageObject = new JSONObject();
            userMessageObject.put("role", "user");
            userMessageObject.put("content", question);

            // Agregar el mensaje predefinido solo si no se ha agregado antes
            if (!systemMessageAdded) {
                JSONObject systemMessageObject = new JSONObject();
                systemMessageObject.put("role", "system");
                systemMessageObject.put("content", "Eres un asistente artificial que forma parte de una app llamada ParKing cuyo objetivo es ofrecer respuestas cortas y concisas sobre el funcionamiento de la app.\n\nEsta es la información necesaria que necesitas saber para asistir correctamente al usuario:\n1. Para cambiar la foto de perfil debes ir a la pestaña de cuenta e ir a configuración, entonces verás el botón de 'Cambiar foto de perfil', sigue las instrucciones y no tendrás problemas.\n\nEsta es la conversacion que tienes con el usuario:\n\n");
                messagesArray.put(systemMessageObject);
                systemMessageAdded = true;
            }

            // Agregar el mensaje del usuario al array de mensajes
            messagesArray.put(userMessageObject);

            // Agregar el array al cuerpo de la solicitud
            jsonBody.put("model", "local-model");
            jsonBody.put("messages", messagesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> addResponse("Failed to load response due to " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray choicesArray = jsonObject.getJSONArray("choices");

                        // Obtener la respuesta de la IA
                        String result = choicesArray.getJSONObject(0).getJSONObject("message").getString("content");

                        // Crear un nuevo objeto para el mensaje de la IA
                        JSONObject botMessageObject = new JSONObject();
                        botMessageObject.put("role", "assistant");
                        botMessageObject.put("content", result);

                        // Agregar el mensaje de la IA al array de mensajes
                        messagesArray.put(botMessageObject);

                        // Mostrar la respuesta en la interfaz de usuario
                        runOnUiThread(() -> addResponse(result.trim()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> addResponse("Failed to load content due to " + response.body().toString()));
                }
            }
        });
    }
}
