package br.ufsm.csi.so.webserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class CriaLog {

    private static int bufferTam = 1000;
    private static byte[] logBuffer = new byte[bufferTam];
    private Socket Socket;
    private InetAddress ipUsuario;

    private Semaphore vazio = new Semaphore(bufferTam);
    private Semaphore cheio = new Semaphore(0);
    private Semaphore mutex = new Semaphore(1);
    private static int val;

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

            ipUsuario = Socket.getInetAddress();
            String ipUsuarioString = ipUsuario.toString();
            String[] endereco = requisicao.split("Host");
            String logString = "\nIp:" + ipUsuarioString + " Endereco: " + endereco[0] + " Data e hora: "+ dataHora + " \n";

            byte[] logByte = logString.getBytes();
            try {
                vazio.acquire(logByte.length);
                mutex.acquire();
            } catch (InterruptedException e) {
                System.out.println("ERRO: "+e.getMessage());
            }
            System.out.println("gravou os dados para o log...");
            for (int i = 0; i < logByte.length; i++) {
                logBuffer[val] = logByte[i];
                val++;
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
                System.out.println("ERRO: "+e.getMessage());
            }

            String logString = new String(logBuffer, 0, val);
            int tamConsumido = val;
            val = 0;
            logBuffer = new byte[bufferTam];

            System.out.println("consumiu...");
            mutex.release();
            vazio.release(tamConsumido);

            try {
                FileWriter myWriter = new FileWriter("log.txt", true);
                myWriter.write(logString);
                myWriter.close();
            } catch (IOException e) {
                System.out.println("Não foi possível gravar o log...");
                e.printStackTrace();
            }

        }

    }
}
