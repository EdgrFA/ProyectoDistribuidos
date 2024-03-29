package servidor;

import Caracteres.Contador;
import algoritmobully.AlgoritmoBully;
import algoritmobully.ServidorInfo;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import reloj.RelojComponent;

public class SocketServidor extends Thread{
    private SocketServidorReplica ssr;
    private final int port, portServidorR;
    private final JLabel Caracter[], Contador[], ip;
    private RelojComponent reloj;
    private AlgoritmoBully bully;
    
    public SocketServidor(AlgoritmoBully bully,int port, int portServidorR, 
                RelojComponent reloj, JLabel[] Caracter, JLabel Contador[], JLabel ip) {
        this.port = port;
        this.portServidorR = portServidorR;
        this.reloj = reloj;
        this.Caracter = Caracter;
        this.Contador = Contador;
        this.bully = bully;
        this.ssr = null;
        this.ip=ip;
    }
    
    private void iniciarServidorReplica(){
        ssr = new SocketServidorReplica(portServidorR, reloj);
        ssr.start();
    }

    private void conexionJugador() throws IOException{
        ServerSocket s = new ServerSocket(port);
        for(;;){
            //Esperamos una conexión 
            Socket cl = s.accept();
            String dirCliente = String.valueOf(cl.getInetAddress()).split("/")[1];
            System.out.println("Jugador: Conexion con " + dirCliente);
            ip.setText(dirCliente);
            
            DataInputStream dis = new DataInputStream(cl.getInputStream());
            String nombre = dis.readUTF();
            long tam = dis.readLong();

            System.out.println("Jugador: Recibiendo el archivo " + nombre);
            FileOutputStream fos = new FileOutputStream(nombre);
            DataOutputStream dos = new DataOutputStream(fos);

            //Sección para recibir el archivo
            long recibidos = 0;
            int n;
            while(recibidos < tam){
                byte []b = new byte[1024];
                n = dis.read(b);                        
                dos.write(b,0,n);
                dos.flush();
                recibidos = recibidos + n;
            }//while

            System.out.println("Jugador: Archivo recibido.");
            fos.close();
            dos.close();
            dis.close();
            cl.close();
            
            //Colocar caracteres con frecuencia en interfaz
            
            Contador cars = new Contador();
            int arreglo[][] = cars.Cuenta_Caracter(nombre);
            registrarOperacion(dirCliente, arreglo);
            for (int i = 0; i < 27; i++) {
                Caracter[i].setText(String.valueOf((char)arreglo[0][i]));
                Contador[i].setText(String.valueOf(arreglo[1][i]));
            }
            
            messageFrame("Se recibio arreglo de Jugador: " + dirCliente);
        }
    }
    
   /* private int sumarArchivo(String archivo) throws FileNotFoundException, IOException{
        String cadena;
        int cuenta = 0;
        
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null)
            cuenta+=Integer.parseInt(cadena);
        
        f.close();
        b.close();
        return cuenta;
    }
    */
    private void registrarOperacion(String ip, int suma[][]){
        //Insertar registro en la BD
        try {
            Conector con = new Conector();
            con.Insertar(ip, suma, reloj.getHoras(), reloj.getMinutos(), reloj.getSegundos());
            System.out.println("BD: Se registro correctamente.");
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex.toString());
        }
        
        //Replicar resultado
        System.out.println("Enviando replica!!!!!");
        try {
            for (ServidorInfo si : bully.getServidores()) {
                if(!si.isActivo())
                    continue;
                ssr.replicarRegistro(si.getIP(), suma);
            }
        } catch (IOException ex) {
            System.out.println("Replica: " + ex.toString());
        }
    }
    
    private void messageFrame(String mssg){
        Thread t = new Thread(new Runnable(){
            public void run(){
                JOptionPane.showMessageDialog(null, mssg);
            }
        });
        t.start();
    }
    
    @Override
    public void run() {
        
        iniciarServidorReplica();
        
        for(;;){
            try {
                conexionJugador();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println(ex.toString());
            }
        }
    }
    
    
    
}
