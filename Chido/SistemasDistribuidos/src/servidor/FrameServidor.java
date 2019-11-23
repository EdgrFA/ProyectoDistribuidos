package servidor;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import reloj.RelojComponent;
import algoritmoberkeley.AlgoritmoBerkeley;
import algoritmobully.AlgoritmoBully;
import java.awt.Color;
import javax.swing.BorderFactory;
import sources.Ports;

public class FrameServidor extends JFrame{
    
    private JLabel[] Caracter= new JLabel [27];
    private JLabel[] Contador= new JLabel [27];
    private JLabel ConexionValue, DatosValue;
    private RelojComponent reloj;
    private final String [] IPs;
    private final JPanel panel;
    private final int nivelServidor;
    
    
    
    public FrameServidor (String [] IPs, int nivelServidor){
        super("Servidor");
        this.IPs = IPs;
        this.nivelServidor = nivelServidor;
        super.setSize(300, 400);
        super.setResizable(false);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setLocationRelativeTo(null);
        panel = (JPanel) super.getContentPane();
        panel.setLayout(null);
        initcomponents();
    }
    
    private void initcomponents(){
        JLabel servR = new JLabel("Servidor Replica: " + IPs[0]);
        Dimension size = servR.getPreferredSize();
        servR.setBounds(30, 300, size.width+10, size.height);
        panel.add(servR);
        
        JLabel Conexion = new JLabel("Caracter: ");
        ConexionValue = new JLabel("Conexión: ");
        JLabel Datos = new JLabel("Frecuencia: "); 
        DatosValue = new JLabel("");
        Dimension sizea = Conexion.getPreferredSize();
        Dimension sizeb = ConexionValue.getPreferredSize();
        Dimension sizec = Datos.getPreferredSize();
        Dimension sized = DatosValue.getPreferredSize();
        Conexion.setBounds(120, 5, sizea.width+30, sizea.height);
        ConexionValue.setBounds(15, 50, sizeb.width+30, sizeb.height);
        Datos.setBounds(180, 5, sizec.width+30, sizec.height);
        DatosValue.setBounds(15, 80, sized.width+30, sized.height); 
        
        for (int i = 0; i < 27; i++) {
            Caracter[i]=new JLabel("c");
            Dimension sizex=Caracter[i].getPreferredSize();
            Caracter[i].setBounds(160, (i*10)+15, sizex.width+30, sizex.height);            
            panel.add(Caracter[i]);
            
            Contador[i]=new JLabel("co");
            Dimension sizex2=Contador[i].getPreferredSize();
            Contador[i].setBounds(180, (i*10)+15, sizex2.width+30, sizex2.height);            
            panel.add(Contador[i]);
            
        }
        
        panel.add( Conexion );  
        panel.add(ConexionValue );  
        panel.add( Datos );  
        //panel.add(DatosValue );   
        
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
        ServidorFE sfe= new ServidorFE(algoritmoB);
        sfe.start();
        //Servidor para jugadores
        SocketServidor ss = new SocketServidor(IPs, Ports.puertoServidor, Ports.puertoReplicar, 
                    reloj, Caracter,Contador,DatosValue);
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