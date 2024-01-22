package com.example.parking;

import java.sql.Timestamp;

public class Reserva {
    private Timestamp horaInicio;
    private String lugar;
    private String precio;
    private String direccion;

    public Reserva(Timestamp horaInicio, String lugar, String precio, String direccion) {
        this.horaInicio = horaInicio;
        this.lugar = lugar;
        this.precio = precio;
        this.direccion = direccion;
    }

    public Timestamp getHoraInicio() {
        return horaInicio;
    }


    public String getLugar() {
        return lugar;
    }

    public String getPrecio() {
        return precio;
    }

    public String getDireccion() {
        return direccion;
    }
}
