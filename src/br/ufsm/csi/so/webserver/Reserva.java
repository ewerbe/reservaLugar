package br.ufsm.csi.so.webserver;

import java.util.ArrayList;

public class Reserva {

    private int numeroLugar;
    private String nome;
    private String dataHora;
    private boolean reservado;


    public Reserva(int numeroLugar, String nome, String dataHora, boolean reservado) {
        this.numeroLugar = numeroLugar;
        this.nome = nome;
        this.dataHora = dataHora;
        this.reservado = reservado;
    }

    public int getNumeroLugar() {
        return numeroLugar;
    }

    public void setNumeroLugar(int numeroLugar) {
        this.numeroLugar = numeroLugar;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public boolean getReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }

    public static boolean verificaLugaresBusReservados(int numeroLugar, ArrayList<Reserva> reservas){
        //retorna true se a reserva já existe para o número do lugar solicitado.
        for (Reserva reserva : reservas) {
            if (reserva.getNumeroLugar() == numeroLugar){
                return true;
            }
        }
        return false;
    }
}
