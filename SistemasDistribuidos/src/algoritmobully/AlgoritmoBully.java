package algoritmobully;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class AlgoritmoBully extends Thread{
    static int ok = 0;
    
    HashMap<String, Integer> servidores;
    private final int port, idServidor;
    private String [] IPs;
    private int idAdmin;
    private String ipAdminStr;

    //Falta agregar una bandera para avisas si cambio administrador
    public AlgoritmoBully(String [] IPs, int port, int idServidor) {
        this.IPs = IPs;
        this.port = port;
        this.idServidor = idServidor;
        servidores = new HashMap<>();
        idAdmin = -1;
        
        ipAdminStr = null;
    }

    public HashMap<String, Integer> getServidores() {
        return servidores;
    }

    public int getIdAdmin() {
        return idAdmin;
    }
    
    public String getStrAdmin(){
        return ipAdminStr;
    }
    
    public void setIdAdmin(int id){
        idAdmin = -1;
    }
    
    public boolean administrando(){
        if(idServidor == idAdmin)
            return true;
        return false;
    }
    
    //Volver administrador al servidor tiempo desde el inicio
    public void asignarAdministracion(){
        idAdmin = idServidor;
        notificarAdministracion();
    }
    
    public void enviarNivel(){
        for (String ip : IPs) {
            try {
                System.out.println("Bully: Enviando Nivel a: " + ip);
                Socket cl = new Socket(ip, port);
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                //Enviar id peticion
                dos.writeInt(0);
                dos.flush();
                //Enviar Nivel
                dos.writeInt(idServidor);
                dos.flush();
            } catch (IOException ex) {
                ex.toString();
                System.out.println("Bully(Enviar Nivel): No se pudo conectar con: " + ip);
            }
        }
    }
    
    public boolean peticionEleccion(int limiteTiempo){
        //Retornar falso si no es nuevo administrador

        if(servidores.size()<IPs.length)
            return false;
        
        System.out.println("Bully: Realizando peticion eleccion");
        for(String ip : IPs){
            int nivel = servidores.get(ip);
            if(idServidor > nivel)
                continue;
            try {
                Socket cl = new Socket(ip, port);
                cl.setSoTimeout(3*1000);
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                
                
                dos.writeInt(1);
                dos.flush();
                
                //reciviendo ok
                int r = dis.readInt();
                
                
                dos.close();
                cl.close();
                
                if(r == ok)
                    return false;
            } catch (IOException ex) {
                System.out.println("Bully: " + ex.toString());
            }
        }
        
        //Enviar nueva administracion
        idAdmin = idServidor;
        notificarAdministracion();
        return true;
    }

    private void notificarAdministracion(){
        System.out.println("Bully: Enviando notificacion administracion a sevidores");
        for (String ip : IPs) {
            try {
                Socket cl = new Socket(ip, port);
                cl.setSoTimeout(2*1000);
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                //Enviar id peticion
                dos.writeInt(2);
                dos.flush();
                //Enviar Nivel
                dos.writeInt(idServidor);
                dos.flush();
                System.out.println("Bully: Se envio notificacion administracion a " + ip);
                idAdmin = idServidor;
            } catch (IOException ex) {
                ex.toString();
                System.out.println("Bully: No se pudo notificar administracion a: " + ip);
            }
        }
    }
    
    @Override
    public void run() {
        //En este hilo se recibiran las peticiones Eleccion y la asignacion de nuevo administrador
        System.out.println("Bully: Iniciando servicio en puerto: " + port);
        try {
            ServerSocket s = new ServerSocket(port);
            for(;;){
                //Esperamos una conexión 
                Socket cl = s.accept();
                String dirCliente = String.valueOf(cl.getInetAddress()).split("/")[1];

                DataInputStream dis = new DataInputStream(cl.getInputStream());
                int peticion = dis.readInt();
                //String nombre = dis.readUTF();
                int nivel;
                switch (peticion) {
                    case 0:
                        //LLEGA IDENTIFICADOR SERVIDOR
                        nivel = dis.readInt();
                        servidores.put(dirCliente, nivel);
                        System.out.println("Bully: Registrando: " + dirCliente + "/N: " + nivel);
                        break;
                    case 1:
                        //NOTIFICACION DE ELECCION
                        System.out.println("Bully: Se recibio notificacion de eleccion de servidor: " + dirCliente);
                        //MANDAR OK SI ES MENOR
                        nivel = servidores.get(dirCliente);
                        if(idServidor < nivel){
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            dos.writeInt(ok);
                            dos.flush();
                            System.out.println("Bully: Se envio OK a servidor: " + dirCliente);
                            dos.close();
                        }else{
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            dos.writeInt(1);
                            dos.flush();
                            System.out.println("Bully: Se envio anti OK a servidor: " + dirCliente);
                            dos.close();
                        }
                        
                        break;
                    case 2:
                        //NOTIFICACION DE NUEVO ADMINISTRADOR
                        idAdmin = servidores.get(dirCliente);
                        ipAdminStr = dirCliente;
                        System.out.println("Bully: Se recibio notificacion de eleccion de servidor: " + dirCliente);
                        if(idServidor > idAdmin)
                            peticionEleccion(5);
                        break;
                }
                dis.close();
                cl.close();
            }
        } catch (IOException ex) {
            System.out.println("Bully: " + ex.toString());
        }
        
    }
}
