package com.example.parking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private List<Reserva> reservas;

    public ReservaAdapter(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tab2, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);

        holder.hora.setText(String.valueOf(reserva.getHoraInicio()));
        holder.textLugar.setText(reserva.getLugar());
        holder.textDireccion.setText(reserva.getDireccion());
        holder.textPrecio.setText(reserva.getPrecio());
    }


    @Override
    public int getItemCount() {
        return reservas.size();
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView hora;
        TextView textLugar;
        TextView textDireccion;
        TextView textPrecio;

        ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            hora = itemView.findViewById(R.id.hora);
            textLugar = itemView.findViewById(R.id.textLugar);
            textDireccion = itemView.findViewById(R.id.textDireccion);
            textPrecio = itemView.findViewById(R.id.textPrecio);
        }
    }
}