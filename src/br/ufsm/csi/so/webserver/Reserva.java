package br.ufsm.csi.so.webserver;

public class Reserva {

    private int numeroLugar;
    private String nome;
    private String dataHora;
    private Boolean reservado;


    public Reserva(int numeroLugar, String nome, String dataHora, Boolean reservado) {
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

    public Boolean getReservado() {
        return reservado;
    }

    public void setReservado(Boolean reservado) {
        this.reservado = reservado;
    }
}
