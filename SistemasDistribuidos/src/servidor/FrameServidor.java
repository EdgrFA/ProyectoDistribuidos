package servidor;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import reloj.RelojComponent;
import algoritmoberkeley.AlgoritmoBerkeley;
import algoritmobully.AlgoritmoBully;
import sources.Ports;

public class FrameServidor extends JFrame{
    private JLabel[] Caracter= new JLabel [27];
    private JLabel[] Contador = new JLabel [27];
    private JLabel conexionValue;
    private RelojComponent reloj;
    private final String [] IPs;
    private final JPanel panel;
    private final int nivelServidor;
    
    
    
    public FrameServidor (String [] IPs, int nivelServidor){
        super("Servidor");
        this.IPs = IPs;
        this.nivelServidor = nivelServidor;
        super.setSize(220, 500);
        super.setResizable(false);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);
        panel = (JPanel) super.getContentPane();
        panel.setLayout(null);
        initcomponents();
    }
    
    private void initcomponents(){
        JLabel cabecera = new JLabel("Caracter | Frecuencia ");
        Dimension sizea = cabecera.getPreferredSize();
        cabecera.setBounds(41, 10, sizea.width+30, sizea.height);
        panel.add(cabecera);
        
        //Vista Caracteres
        Caracter= new JLabel [27];
        String[] carac = {"a","b","c","d","e","f","g","h","i","j",
                          "k","l","m","n","ñ","o","p","q","r","s",
                          "t","u","v","w","x","y","z"};
        for (int i = 0; i < 27; i++) {
            Caracter[i] = new JLabel(carac[i]);
            Dimension sizex = Caracter[i].getPreferredSize();
            Caracter[i].setBounds(81, (i*13)+27, sizex.width+30, sizex.height);            
            panel.add(Caracter[i]);
            
            Contador[i] = new JLabel(":  ---");
            Dimension sizex2 = Contador[i].getPreferredSize();
            Contador[i].setBounds(96, (i*13)+27, sizex2.width+30, sizex2.height);            
            panel.add(Contador[i]);
        }
        
        JLabel ConexionValue = new JLabel("Conexión: ");
        Dimension sizeb = ConexionValue.getPreferredSize();
        ConexionValue.setBounds(30, 392, sizeb.width+30, sizeb.height);
        panel.add(ConexionValue);
        
        conexionValue = new JLabel("");
        Dimension sized = conexionValue.getPreferredSize();
        conexionValue.setBounds(95, 392, sized.width+30, sized.height);
        panel.add(conexionValue);   
        
        reloj = new RelojComponent(1, 30, 30, 1, panel);
        reloj.start();
    }
    
    public void iniciarServicios(){
        //Algoritmo de eleccion de administrador
        AlgoritmoBully bully = new AlgoritmoBully(IPs, Ports.puertoBully, nivelServidor);
        bully.start();
        bully.enviarNivel();
        //Iniciar algoritmo de sincronizacion Berkeley
        AlgoritmoBerkeley algoritmoB = new AlgoritmoBerkeley(IPs, bully, reloj);
        algoritmoB.start();
        //Servidor para FE
        ServidorFE sfe = new ServidorFE(bully);
        sfe.start();
        //Servidor para jugadores
        SocketServidor ss = new SocketServidor(bully, Ports.puertoServidor, Ports.puertoReplicar, 
                    reloj, Caracter, Contador, conexionValue);
        ss.start();
    }
    
    public static void main(String[] args) {        
        String [] IPs = new String[2];
        String mssg = "Introduce el IP del servidor para replicar";

        //IPs[0] = JOptionPane.showInputDialog(null, mssg);
        //IPs[1] = JOptionPane.showInputDialog(null, mssg);
        IPs[0] = "10.100.69.49";
        IPs[1] = "10.100.70.86";

        //!!!Falta pedir nivel del servidor
        mssg = "Introduce el id del Servidor";
        int idServidor = Integer.parseInt(JOptionPane.showInputDialog(null, mssg));
        FrameServidor stf = new FrameServidor(IPs, idServidor);
        stf.setVisible(true);
        stf.iniciarServicios();
    }
}


/*
try {
    String thisIp = InetAddress.getLocalHost().getHostAddress();
    System.out.println("IP:"+thisIp);
}
catch(Exception e) 
{
	e.printStackTrace();
}
*/