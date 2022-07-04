package br.ufsm.csi.so.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Servidor {

    public static Semaphore mutex = new Semaphore(1);
    public static ArrayList<Reserva> reservas = new ArrayList<>();
    public static Integer port = 8080;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Rodando na porta: " + port);

        while (true) {
            try {
                new Thread(new Requisicao(serverSocket, mutex, reservas)).start();
            } catch (Exception e) {
                System.out.println("ERRO:");
                System.out.println(e.getMessage());
            }
        }

    }

}
