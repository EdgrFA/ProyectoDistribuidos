package jugador;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketJugador {
    private final int port;
    private final String host;

    public SocketJugador(String host, int port) {
        this.host = host;
        this.port = port;        
    }   
    
    public void enviarArchivo(String archivo, String nombre, long tam) throws IOException{
        int porcentaje = 0, n = 0;
        byte[]b = new byte[1024];
        long enviados = 0;
        Socket cl = new Socket(host,port);
        DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
        DataInputStream dis = new DataInputStream(new FileInputStream(archivo));
        dos.writeUTF(nombre);
        dos.flush();
        dos.writeLong(tam);
        dos.flush();
        //Sección para el envío del archivo
        while(enviados<tam){
            n = dis.read(b);
            dos.write(b,0,n);
            dos.flush();
            enviados = enviados+n;
            porcentaje = (int)(enviados*100/tam);
            System.out.print("Enviados:" + porcentaje + "%\r");
        }//while
        System.out.println("\n\nArchivo enviado ");
        dis.close();                
        dos.close();
        cl.close();
    }
}
