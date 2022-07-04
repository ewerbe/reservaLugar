package br.ufsm.csi.so.webserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class CriaLog {

    private static int tamBuf = 1000;
    private static byte[] logBuffer = new byte[tamBuf];
    private Socket Socket;
    private InetAddress ipReservado;
    Semaphore vazio = new Semaphore(tamBuf);
    Semaphore cheio = new Semaphore(0);
    Semaphore mutex = new Semaphore(1);
    private static int iStatic;
    private static File file = new File("log.txt");
    private String requisicao;
    private String dataHora;

    public CriaLog(Socket socket, String requisicao, String dataHora) {
        this.dataHora = dataHora;
        this.requisicao = requisicao;
        Socket = socket;

        try {
            if (file.createNewFile()) {
                System.out.println("Arquivo criado: " + file.getName());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        new Thread(new CriaLog.ProduzLog()).start();
        new Thread(new CriaLog.GravaLog()).start();

    }

    private class ProduzLog implements Runnable {

        @Override
        public void run() {

            ipReservado = Socket.getInetAddress();
            String ipString = ipReservado.toString();
            String[] endereco = requisicao.split("Host");
            String logString = "\nIp:"  + ipString +  " Endereco: " + endereco[0] + " Data e hora: "+  dataHora +  " \n";

            byte[] logBype = logString.getBytes();
            try {
                vazio.acquire(logBype.length);
                mutex.acquire();
            } catch (InterruptedException e) {
            }
            System.out.println("gravou");
            for (int i = 0; i < logBype.length; i++) {
                logBuffer[iStatic] = logBype[i];
                iStatic++;
            }
            mutex.release();
            cheio.release();

        }

    }

    private class GravaLog implements Runnable {

        @Override
        public void run() {

            try {
                cheio.acquire();
                mutex.acquire();
            } catch (InterruptedException e) {
                // TODO: handle exception
            }

            String logString = new String(logBuffer, 0, iStatic);
            int tamanhoUsado = iStatic;
            iStatic = 0;
            logBuffer = new byte[tamBuf];

            System.out.println("consumiu");
            mutex.release();
            vazio.release(tamanhoUsado);

            try {
                FileWriter myWriter = new FileWriter("log.txt", true);
                myWriter.write(logString);
                myWriter.close();
            } catch (IOException e) {
                System.out.println("Erro ao gravar os dados");
                e.printStackTrace();
            }


        }

    }
}
