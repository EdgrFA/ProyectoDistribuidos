package algoritmoberkeley;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import reloj.RelojComponent;

public class SocketServidorTiempo {
    private final RelojComponent reloj;
    private final int port;

    public SocketServidorTiempo(int port, RelojComponent reloj) {
        this.port = port;
        this.reloj = reloj;
    }
    
    public RelojBerkeley consultarTiempoServidor(String ip) throws IOException{
        Socket cl = new Socket(ip, port);
        cl.setSoTimeout(3*1000);
        DataInputStream dis= new DataInputStream(cl.getInputStream());   
        DataOutputStream dos= new DataOutputStream(cl.getOutputStream());
        
        //Tomar tiempos antes de envio
        int t0Horas = reloj.getHoras();
        int t0Minutos = reloj.getMinutos();
        int t0Segundos = reloj.getSegundos();

        //Mandar hora a los servidores
        dos.writeInt(1);
        dos.flush();
        dos.writeInt(t0Horas);
        dos.flush();
        dos.writeInt(t0Minutos);
        dos.flush();
        dos.writeInt(t0Segundos);
        dos.flush();

        //Leer respuesta
        int diferenciaTiempo = dis.readInt();

        //Tiempos al recibir datos
        int t1Horas = reloj.getHoras();
        int t1Minutos = reloj.getMinutos();
        int t1Segundos = reloj.getSegundos();

        //Calular latencia de respuesta
        int latencia = (t1Horas - t1Horas)*3600;
        latencia += (t1Minutos - t0Minutos)*60;
        latencia += t1Segundos - t0Segundos;

        diferenciaTiempo = diferenciaTiempo - (latencia/2);

        dis.close(); 
        dos.close();
        cl.close();

        return new RelojBerkeley(ip, diferenciaTiempo);
    }
    
    public void clienteBerkeley(int limiteTiempo) throws IOException{
        ServerSocket servidorReloj = new ServerSocket(port);
        servidorReloj.setSoTimeout(limiteTiempo*1000);
        try{
            for(;;){
                //Esperamos una conexión 
                Socket cl = servidorReloj.accept();                        
                DataOutputStream dos= new DataOutputStream(cl.getOutputStream());                        
                DataInputStream dis= new DataInputStream(cl.getInputStream());

                if(dis.readInt()==1){
                    //Enviar diferencia de tiempo al asministrador
                   int horaServidor = dis.readInt();
                   int minutosServidor = dis.readInt();
                   int segundosServidor = dis.readInt();

                   int difTiempos = (reloj.getHoras() - horaServidor)*3600;
                   difTiempos += (reloj.getMinutos() - minutosServidor)*60;
                   difTiempos += reloj.getSegundos() - segundosServidor;

                   dos.writeInt(difTiempos);
                   dos.flush();
                   System.out.println("Berkeley(Cliente): Se envio diferencia de tiempo");
                }else{
                    //Ajuste de reloj
                    int ajuste = dis.readInt();
                    System.out.println("Berkeley(Cliente): Ajuste de segundos de " + ajuste);
                    if(ajuste < 0){
                        reloj.realentizarTiempo(ajuste);
                        System.out.println("Berkeley(Cliente): Se realentizo el tiempo.");
                    } else{
                        reloj.adelantarTiempo(ajuste);
                        System.out.println("Berkeley(Cliente): Se recorrio tiempo.");
                    }
                }       
                dis.close();
                dos.close();                    
                cl.close();
            }
        }catch (SocketTimeoutException ste) {
            System.out.println("Berkeley(Cliente): " + ste.toString());
        }finally{
            servidorReloj.close();
        }
    }
    
    public boolean enviarAjuste(RelojBerkeley servidor) throws IOException{
        Socket cl = new Socket(servidor.getIP(), port);
        cl.setSoTimeout(3*1000);
        DataOutputStream dos= new DataOutputStream(cl.getOutputStream());

        dos.writeInt(2);
        dos.flush();
        dos.writeInt(servidor.getAjuste());
        dos.flush();

        dos.close();
        cl.close();
        return true;
    }
}
