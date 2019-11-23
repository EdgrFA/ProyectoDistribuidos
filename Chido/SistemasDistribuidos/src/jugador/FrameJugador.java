package jugador;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import reloj.RelojComponent;
import sources.Ports;

public class FrameJugador extends JFrame{
    private String Nombre, Archivo;
    private String ip[];
    private RelojComponent reloj;
    private JPanel panel;
    private JLabel archivo;
    private long tam;
    private boolean existeArchivo;
    
    public FrameJugador(String ip[]) {
        super("Jugador");
        this.ip = ip;
        existeArchivo = false;
                
        super.setSize(300, 230);
        super.setResizable(false);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);
        panel = (JPanel) super.getContentPane();
        panel.setLayout(null);
        initcomponents();
    }
    
    private void initcomponents(){
        JLabel servidor = new JLabel("Servidor: " + ip);
        Dimension size = servidor.getPreferredSize();
        servidor.setBounds(15, 15, size.width+10, size.height);
        panel.add(servidor);
        
        archivo = new JLabel("Archivo:  Sin archivo");
        archivo.setBounds(15, 45, 250, size.height);
        panel.add(archivo);
        
        //Botones
        JButton boton1=new JButton("Cargar"); 
        JButton boton2=new JButton("Enviar");
        Dimension sizeb1 = boton1.getPreferredSize();
        Dimension sizeb2 = boton2.getPreferredSize();     
        boton1.setBounds(55, 95, sizeb1.width+10, sizeb1.height);
        boton2.setBounds(155, 95, sizeb2.width+10, sizeb2.height);
        boton1.addActionListener(onClickCargarArchivo);
        boton2.addActionListener(onClickEnviarArchivo);
        panel.add(boton1);
        panel.add(boton2);
        
        reloj = new RelojComponent(1, 30, 30, 1, panel);
        reloj.start();
    }
    
    private ActionListener onClickCargarArchivo = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jf = new JFileChooser();
            //jf.setMultiSelectionEnabled(true);
            jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int r = jf.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                File file = jf.getSelectedFile();
                Archivo = file.getAbsolutePath();
                Nombre = file.getName();
                tam = file.length();
                archivo.setText("Archivo:  " + Nombre);
                existeArchivo = true;
            }else {
                existeArchivo = false;
            }
        }
    };
    
    private ActionListener onClickEnviarArchivo = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                SocketJugador sj;
                sj = new SocketJugador(Preguntar_Admin(), Ports.puertoServidor);
                sj.enviarArchivo(Archivo, Nombre, tam);                                
                messageFrame("Archivo enviado.");
            } catch (IOException ex) {
                messageFrame("No se pudo enviar archivo");
            }
        }
    };
    
    private void messageFrame(String mssg){
        Thread t = new Thread(new Runnable(){
            public void run(){
                JOptionPane.showMessageDialog(null, mssg);
            }
        });
        t.start();
    }
    
    public String Preguntar_Admin(){
        String Admin=""; 
        for(String ip : ip){            
            try {
                Socket cl = new Socket(ip, 7004);
                cl.setSoTimeout(1000);                
                DataInputStream dis = new DataInputStream(cl.getInputStream());                                                
                Admin=dis.readUTF();
                dis.close();
                cl.close();                                
            } catch (IOException ex) {
                System.out.println("Bully: " + ex.toString());
            }
        }
        return Admin;
    }
    
    public static void main(String[] args) {
        
        String ip []=new String[3];
        ip[0]="";
        ip[1]="";
        ip[2]="";
        FrameJugador stf = new FrameJugador(ip);
        stf.setVisible(true);
    }
    
}
