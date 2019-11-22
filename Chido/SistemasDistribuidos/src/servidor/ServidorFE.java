/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import Caracteres.Contador;
import algoritmoberkeley.AlgoritmoBerkeley;
import algoritmobully.AlgoritmoBully;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Josh Plata
 */
public class ServidorFE extends Thread{
    private int port;
    private AlgoritmoBerkeley bebe;
    public ServidorFE(AlgoritmoBerkeley bebe) {
        this.port = 7004;
        this.bebe=bebe;
    }               
    @Override            
    public void run() {
        ServerSocket s = null;
        try {
            s = new ServerSocket(7004);        
            for(;;){           
                
                    Socket cl = s.accept();           
                    DataOutputStream dos;
                    dos = new DataOutputStream(cl.getOutputStream());
                    dos.writeUTF(bebe.getIpAdmin());
                    dos.flush();               

            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorFE.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
