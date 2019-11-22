package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import reloj.RelojComponent;

public class SocketServidorReplica extends Thread{
    private final int port;
    private final RelojComponent reloj;

    public SocketServidorReplica(int port, RelojComponent reloj) {
        this.port = port;
        this.reloj = reloj;
    }

    public void replicarRegistro(String ip, int suma[][]) throws IOException{
        Socket cl = new Socket(ip, port);
        DataOutputStream dos= new DataOutputStream(cl.getOutputStream());
        dos.writeUTF(ip);
        dos.flush();
        for (int i = 0; i < 27; i++) {            
            dos.writeInt(suma[0][i]);
            dos.flush();
            dos.writeInt(suma[1][i]);
            dos.flush();
        }                
        dos.close();
        cl.close();
        System.out.println("Replica: Se replico registro.");
    }
    
    private void recibirReplica() throws IOException{
        ServerSocket s = new ServerSocket(port);
        for(;;){
            //Esperamos una conexión 
            Socket cl = s.accept(); 

            DataInputStream dis = new DataInputStream(cl.getInputStream());
            String direccion = dis.readUTF();                                                                            
            int suma [][] = new int [2][27];
            
                for (int i = 0; i < 27; i++) {
                    suma[0][i]=dis.readInt();
                    suma[1][i]=dis.readInt();
                }                    
            
            dis.close();                    
            cl.close();                                
            //Registrar en BD
            registrarOperacion(direccion, suma);
            System.out.println("Replica: Se recibio replica.");
        }
    }
    
    private void registrarOperacion(String ip, int suma[][]){
        //Insertar registro en la BD
        try {
            Conector con = new Conector();
            con.Insertar(ip, suma, reloj.getHoras(), reloj.getMinutos(), reloj.getSegundos());
            System.out.println("Replica: Se registro replica correctamente en la BD.");
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Replica: " + ex.toString());
        }
    }    
    
    @Override
    public void run() {
        for(;;){
            try {
                recibirReplica();
            } catch (IOException ex) {
                System.out.println("Replica: " + ex.toString());
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println(ex.toString());
            }
        }
    }
    
    
}
