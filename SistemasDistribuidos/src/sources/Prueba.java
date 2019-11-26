package sources;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import servidor.FrameServidor;

public class Prueba {
    public static void main(String[] args) {
        try{ 
            String [] IPs = new String[3];
            //the file to be opened for reading  
            FileInputStream fis = new FileInputStream("c:/IPS.txt");       
            Scanner sc = new Scanner(fis);    //file to be scanned  
            //returns true if there is another line to read  
            int i = 0;
            while(sc.hasNextLine()){
                IPs[i] = sc.nextLine();
                i++;
            }
            sc.close();     //closes the scanner
            
            String mssg = "Introduce el id del Servidor";
            int idServidor = Integer.parseInt(JOptionPane.showInputDialog(null, mssg));
            FrameServidor stf = new FrameServidor(IPs, idServidor);
            stf.setVisible(true);
            stf.iniciarServicios();
        }  
        catch(IOException e){  
            e.printStackTrace();  
        }  
        
        /*
        String mssg = "Introduce el IP del servidor para replicar";
        int i = 0; 
        String [] LocalIps = {"192.168.0.","192.168.0.","192.168.0.","192.168.0."};
        while(i < 3){
            
        }
        IPs[0] = "10.100.69.49";
        IPs[1] = "10.100.70.86";

        //!!!Falta pedir nivel del servidor
        mssg = "Introduce el id del Servidor";
        int idServidor = Integer.parseInt(JOptionPane.showInputDialog(null, mssg));
        FrameServidor stf = new FrameServidor(IPs, idServidor);
        stf.setVisible(true);
        stf.iniciarServicios();*/
    }
}
