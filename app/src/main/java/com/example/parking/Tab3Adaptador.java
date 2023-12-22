package com.example.parking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// En la clase Tab3Adaptador, cambia el tipo de dataList a List<String>
public class Tab3Adaptador extends RecyclerView.Adapter<Tab3Adaptador.ViewHolder> {
    private List<String> dataList; // Puedes cambiar el tipo de dato según tus necesidades
    private Context context;

    public Tab3Adaptador(Context context, List<String> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vehiculo_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Obtén los detalles del vehículo desde el dataList
        String vehiculoDetails = dataList.get(position);

        // Dividir los detalles del vehículo por saltos de línea
        String[] detailsArray = vehiculoDetails.split("\n");

        // Asignar los detalles del vehículo a las vistas
        holder.textView1.setText(detailsArray[0]);
        holder.textView2.setText(detailsArray[1]);
        holder.textView3.setText(detailsArray[2]);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Método para actualizar los datos del adaptador
    public void updateData(List<String> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView1, textView2, textView3;

        public ViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
        }
    }
}
