package br.ufsm.csi.so.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Requisicao implements Runnable {

    @Override
    public void run() {
    }

    public Requisicao(ServerSocket serverSocket, Semaphore mutex, ArrayList<Reserva> reservas)
            throws IOException, InterruptedException {

        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        byte[] buffer = new byte[2048];
        int fromBuffer = inputStream.read(buffer);
        Boolean ok = false;
        String requisicao = new String(buffer, 0, fromBuffer);

        String[] lines = requisicao.split("\n");
        String[] linha0 = lines[0].split(" ");

        OutputStream outputStream = socket.getOutputStream();

        //arquivo que retornará como resposta da requisicao
        File file = new File("resources" + File.separator + linha0[1]);

            //requisicao para index.
        if (linha0[1].equals("/")) {
            file = new File("resources" + File.separator + "index.html");

            //requisicao para reservar um lugar no bus.
        } else if (linha0[1].startsWith("/reservar")) {
            file = new File("resources" + File.separator + "reserva.html");

            //req para confirmacao de reserva.
        } else if (linha0[1].startsWith("/confirmar")) {
            mutex.acquire();
            String nome = Service.getNomeReserva(linha0[1]);
            Integer numeroLugar = Service.getNumeroLugar(linha0[1]);
            String data = Service.getDateReserva();

            if (!reservas.isEmpty()) {
                if (Reserva.verificaLugaresBusReservados(numeroLugar, reservas)) {
                    System.out.println("lugar indisponível...");
                } else {
                    System.out.println("nova reserva...");
                    Reserva novaReserva = new Reserva(numeroLugar, nome, data, true);
                    reservas.add(novaReserva);
                    ok = true;
                }
            } else {
                System.out.println("nova reserva...");
                Reserva novaReserva = new Reserva(numeroLugar, nome, data, true);
                reservas.add(novaReserva);
                ok = true;
            }
            mutex.release();
            file = new File("resources" + File.separator + "index.html");

        } else if (linha0[1].equals("/js/index.js")) {
            String js = Service.getJS(reservas);
            outputStream.write(js.getBytes());

        } else if (linha0[1].equals("/js/solicitar.js")) {
            String js = Service.getJSReserva(reservas);
            outputStream.write(js.getBytes());

        }
        if (file.exists() && !linha0[1].equals("/js/index.js") && !linha0[1].equals("/js/reservar.js")) {
            FileInputStream fileInputStream = new FileInputStream(file);
            String mimeType = Files.probeContentType(file.toPath());

            outputStream.write(("HTTP/1.1 200 OK\n" +            //monta o cabecalho http
                    "Content-Type: " + mimeType + ";charset=UTF-8\n\n").getBytes());

            if (linha0[1].startsWith("/confirmar")) { //verifica se houve sucesso ou falha na solicitacao do pedido
                if(ok) {
                    outputStream.write("<script type='text/javascript'>alert('Reserva realizada com sucesso')</script>".getBytes(StandardCharsets.UTF_8));


                }else {
                    outputStream.write("<script type='text/javascript'>alert('Não foi possível realizar a reserva deste lugar')</script>".getBytes(StandardCharsets.UTF_8));
                }
            }
            fromBuffer = fileInputStream.read(buffer);
            while (fromBuffer > 0) {
                outputStream.write(buffer, 0, fromBuffer);
                fromBuffer = fileInputStream.read(buffer);
            }
        }
        outputStream.flush();
        socket.close();
    }
}
