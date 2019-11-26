package servidortiempo;

import algoritmoberkeley.AlgoritmoBerkeley;
import algoritmobully.AlgoritmoBully;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import reloj.RelojComponent;
import sources.Ports;

public class FrameServidorTiempo extends JFrame{
    private String [] IPs = new String[2];
    private final JPanel panel;
    private RelojComponent reloj;
    private JLabel serv1, serv2;
    private final int idServidor;
    
    public FrameServidorTiempo(String [] IPs, int idServidor) {
        super("Servidor Tiempo");
        this.IPs = IPs;
        this.idServidor = idServidor;
        super.setSize(270,155);
        super.setResizable(false);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);
        panel = (JPanel) super.getContentPane();
        panel.setLayout(null);
        initcomponents();
    }
    
    private void initcomponents(){
        
        serv1 = new JLabel("Servidor 1: " + IPs[0]);
        serv2 = new JLabel("Servidor 2: " + IPs[1]);
        Dimension size = serv1.getPreferredSize();
        serv1.setBounds(15, 15, size.width+10, size.height);
        serv2.setBounds(15, 45, size.width+10, size.height);
        panel.add(serv1);
        panel.add(serv2);
        
        reloj = new RelojComponent(1, 30, 30, 1, panel);
        reloj.start();
    }
    
    public void iniciarServicios(){
        //Algoritmo de eleccion de administrador
        AlgoritmoBully bully = new AlgoritmoBully(IPs, Ports.puertoBully, idServidor);
        bully.enviarNivel();
        bully.asignarAdministracion();
        
        AlgoritmoBerkeley algoritmoB = new AlgoritmoBerkeley(IPs, bully, reloj);
        algoritmoB.start();
    }

    
    public static void main(String[] args) {
        String [] IPs = new String[2];
        for(int i = 0; i < 2; i++){
            String mssg = "Introduce el IP del servidor " + (i+1);
            IPs[i] = JOptionPane.showInputDialog(null, mssg);
        }
        IPs[1] = "127.0.0.1";
        IPs[0] = "192.168.0.25";
        FrameServidorTiempo stf = new FrameServidorTiempo(IPs, 10);
        stf.iniciarServicios();
        stf.setVisible(true);
    }
}
