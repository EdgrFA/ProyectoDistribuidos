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
        System.out.println("Esta agarrando los cambios");
        String mssg = "Introduce el IP del servidor para replicar";
        int i = 0; 
        String [] LocalIps = {"10.100.75.173","10.100.72.34"};
        //!!!Falta pedir nivel del servidor
        mssg = "Introduce el id del Servidor";
        int idServidor = Integer.parseInt(JOptionPane.showInputDialog(null, mssg));
        FrameServidor stf = new FrameServidor(LocalIps, idServidor);
        stf.setVisible(true);
        stf.iniciarServicios();
    }
}
