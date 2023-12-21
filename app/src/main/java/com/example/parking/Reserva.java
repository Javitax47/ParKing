package com.example.parking;

public class Reserva {
    private String horaInicio;
    private String horaFinal;
    private String lugar;
    private String precio;
    private String direccion;

    public Reserva(String horaInicio, String horaFinal, String lugar, String precio, String direccion) {
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.lugar = lugar;
        this.precio = precio;
        this.direccion = direccion;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
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
