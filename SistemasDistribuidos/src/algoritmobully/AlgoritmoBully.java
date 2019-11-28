package algoritmobully;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class AlgoritmoBully extends Thread{
    static int ok = 0;
    static int administrando = 1;
    private ArrayList<ServidorInfo> servidores;
    private final int port;
    private ServidorInfo administrador, miServidor;
    
    //Falta agregar una bandera para avisar si cambio administrador
    public AlgoritmoBully(String [] IPs, int port, int idServidor) {
        servidores = new ArrayList<>();
        for (String IP : IPs)
            servidores.add(new ServidorInfo(IP));
        this.port = port;
        miServidor = new ServidorInfo("10.100.78.119");
        miServidor.setIdServidor(idServidor);
        administrador = null;
    }

    public ServidorInfo getAdministrador() {
        return administrador;
    }

    public void setAdministrador(ServidorInfo administrador) {
        this.administrador = administrador;
    }

    public ArrayList<ServidorInfo> getServidores() {
        return servidores;
    }
    
    public boolean administrando(){
        if(miServidor.equals(administrador))
            return true;
        return false;
    }
    
    //Volver administrador al servidor tiempo desde el inicio
    public void asignarAdministracion(){
        administrador = miServidor;
        notificarAdministracion();
    }
    
    public void enviarNivel(){
        for (ServidorInfo servInfo : servidores) {
            while(true){
                try {
                    System.out.println("Bully: Enviando Nivel a: " + servInfo.getIP());
                    Socket cl = new Socket(servInfo.getIP(), port);
                    DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                    //Enviar id peticion
                    dos.writeInt(0);
                    dos.flush();
                    //Enviar Nivel
                    dos.writeInt(miServidor.getIdServidor());
                    dos.flush();
                    dos.close();
                    cl.close();
                    break;
                } catch (IOException ex) {
                    ex.toString();
                    System.out.println("Bully(Enviar Nivel): No se pudo conectar con: " + servInfo.getIP());
                }
            }
        }
    }
    
    public boolean peticionEleccion(){
        //Retornar falso si no es nuevo administrador

        for (ServidorInfo servInfo : servidores)
            if(servInfo.getIdServidor() == -1)
                return false;
        
        System.out.println("Bully: Realizando peticion eleccion");
        for(ServidorInfo servInfo : servidores){
            int nivel = servInfo.getIdServidor();
            if(!servInfo.isActivo()){
                System.out.println("Eleccion: " + servInfo.getIP() + " esta inactivo");
                continue;
            }
            if(miServidor.getIdServidor() > nivel)
                continue;
            try {
                Socket cl = new Socket(servInfo.getIP(), port);
                cl.setSoTimeout(5*1000); //int limiteTiempo
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                
                dos.writeInt(1);
                dos.flush();
                
                //recibiendo ok
                int r = dis.readInt();
                dos.close();
                cl.close();
                
                if(r == ok){
                    System.out.println("Se recibio OK");
                    return false;
                }
                else if(r == administrando){
                    administrador = servInfo;
                    return false;
                }
            } catch (IOException ex) {
                System.out.println("Bully: " + ex.toString());
                servInfo.setActivo(false);
            }
        }
        
        //Enviar nueva administracion
        notificarAdministracion();
        return true;
    }

    private void notificarAdministracion(){
        System.out.println("Bully: Enviando notificacion administracion a sevidores");
        for (ServidorInfo servInfo : servidores) {
            if(!servInfo.isActivo())
                continue;
            try {
                Socket cl = new Socket(servInfo.getIP(), port);
                cl.setSoTimeout(5*1000); // int limite tiempo
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                //Enviar id peticion
                dos.writeInt(2);
                dos.flush();
                System.out.println("Bully: Se envio notificacion administracion a " + servInfo.getIP());
            } catch (IOException ex) {
                System.out.println("Bully: No se pudo notificar administracion a: " + servInfo.getIP());
                servInfo.setActivo(false);
            }
        }
        administrador = miServidor;
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
                        for (ServidorInfo servInfo : servidores) {
                            if(servInfo.getIP().equals(dirCliente)){
                                System.out.println("Llego: " + dirCliente);
                                servInfo.setIdServidor(nivel);
                                servInfo.setActivo(true);
                                break;
                            }
                        }
                        System.out.println("Bully: Registrando: " + dirCliente + "/N: " + nivel);
                        break;
                    case 1:
                        //NOTIFICACION DE ELECCION
                        System.out.println("Bully: Se recibio notificacion de eleccion de servidor: " + dirCliente);
                        //MANDAR OK SI ES MENOR
                        for (ServidorInfo servInfo : servidores) {
                            if(servInfo.getIP().equals(dirCliente)){
                                if(miServidor.getIdServidor() > servInfo.getIdServidor()){
                                    DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                                    dos.writeInt(ok);
                                    dos.flush();
                                    
                                    System.out.println("Bully: Se envio OK a servidor: " + dirCliente);
                                    dos.close();
                                }
                                if(!servInfo.isActivo())
                                    servInfo.setActivo(true);
                                break;
                            }
                        }
                        break;
                    case 2:
                        //NOTIFICACION DE AMINISTRACION
                        for (ServidorInfo servInfo : servidores) {
                            if(servInfo.getIP().equals(dirCliente)){
                                
                                if(!servInfo.isActivo())
                                    servInfo.setActivo(true);
                                
                                //El administrador es menor
                                if(miServidor.getIdServidor() > servInfo.getIdServidor()){
                                    peticionEleccion();
                                    break;
                                }
                                
                                administrador = servInfo;
                                System.out.println("Bully: Nuevo administrador " + dirCliente);
                                break;
                            }
                        }
                }
                dis.close();
                cl.close();
            }
        } catch (IOException ex) {
            System.out.println("Bully: " + ex.toString());
        }
        
    }
}
