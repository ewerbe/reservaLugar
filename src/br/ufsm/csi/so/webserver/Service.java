package br.ufsm.csi.so.webserver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Service {

    public static String getJS(ArrayList<Reserva> reservas){
        String header = getHeader();
        String body = "";

        for (Reserva reserva : reservas) {
            if(reserva.getReservado()) {
                body = body +" document.getElementById('" + reserva.getNumeroLugar() + "').classList.add('ocupado')\n";
                body = body +" $('#teste').append('<tr> <th>"+reserva.getNumeroLugar()+"</th><td>"+reserva.getNome()+"</td><td>"+reserva.getDataHora()+"</td></tr>')\n";
            }
        }

        return header.concat(body);
    }


    public static String getJSReserva(ArrayList<Reserva> reservas){
        String header = getHeader();
        String body = "";

        for (Reserva reserva : reservas) {
            if(reserva.getReservado()) {
                body = body+" $('#"+reserva.getNumeroLugar()+"').remove(); ";
                body = body+" $('.icon0"+reserva.getNumeroLugar()+"').addClass('ocupado'); ";
            }
        }
        return header.concat(body);
    }


    public static String getHeader(){
        return "HTTP/1.1 200 OK\n Content-Type: text/javascript;charset=UTF-8\n\n";
    }


    public static String getNomeReserva(String nome){
        String[] fimUrl = nome.split("=");
        String[] nomeRetorno = fimUrl[1].split("&");
        return nomeRetorno[0].toString();
    }


    public static Integer getNumeroLugar(String num){
        String[] fimUrl = num.split("=");
        return Integer.parseInt(fimUrl[2]);
    }



    public static  String getDateReserva() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
