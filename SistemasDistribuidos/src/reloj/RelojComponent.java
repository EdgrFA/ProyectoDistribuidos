package reloj;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RelojComponent extends Reloj{
    private JPanel jPanel;
    private JButton jButtonEditarHora;
    private JTextField txtReloj;
    private Container contentPane;
    private boolean editando = false;

    public RelojComponent(int horas, int minutos, int segundos, int tiempo, Container contentPane) {
        super(horas, minutos, segundos, tiempo);
        this.contentPane = contentPane;
        initComponents();
    }
    
    private void initComponents(){
        jPanel = new javax.swing.JPanel();
        JPanel jPanelReloj = new javax.swing.JPanel();
        txtReloj = getHora_completa();
        txtReloj.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtReloj.setToolTipText("");
        txtReloj.setBorder(BorderFactory.createEmptyBorder());
        txtReloj.setEditable(false);
        txtReloj.setFocusable(false);
        
        jButtonEditarHora = new javax.swing.JButton();
        jButtonEditarHora.setText("Editar Hora");
        jButtonEditarHora.addActionListener(onClickEditarHora);
        
        jPanelReloj.setBackground(new java.awt.Color(255, 255, 255));
        jPanelReloj.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        javax.swing.GroupLayout jPanelRelojLayout = new javax.swing.GroupLayout(jPanelReloj);
        jPanelReloj.setLayout(jPanelRelojLayout);
        jPanelRelojLayout.setHorizontalGroup(
            jPanelRelojLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRelojLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(txtReloj))
        );
        jPanelRelojLayout.setVerticalGroup(
            jPanelRelojLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRelojLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(txtReloj))
        );

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jButtonEditarHora)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelReloj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelReloj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonEditarHora, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(577, Short.MAX_VALUE)
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(501, Short.MAX_VALUE)
                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

    }
    
    private ActionListener onClickEditarHora = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(editando){
                Pattern p = Pattern.compile("([01]?[0-9]|(2[0-3])):([0-5]?[0-9]):([0-5]?[0-9])");
                String nuevaHora = txtReloj.getText();
                Matcher m = p.matcher(nuevaHora);
                if (m.matches()){
                    String[] hora = nuevaHora.split(":");
                    try {
                        setHoras(Integer.parseInt(hora[0]));
                        setMinutos(Integer.parseInt(hora[1]));
                        setSegundos(Integer.parseInt(hora[2]));
                        actualizarFormatoHoraCompleta();
                        messageFrame("Hora actualizada");
                    }catch(NumberFormatException nf){
                        System.out.println("Error con el formato");
                    }
                }else{
                    messageFrame("Hora incorrecta");
                }
                editando = false;
                jButtonEditarHora.setText("Editar Hora");
                txtReloj.setEditable(false);
                txtReloj.setFocusable(false);
                renaudarReloj();
                
            }else{
                editando = true;
                jButtonEditarHora.setText("Guardar");
                txtReloj.setEditable(true);
                txtReloj.setFocusable(true);
                pararReloj();
            }
        }
    };
    
    public static void messageFrame(String mssg){
        Thread t = new Thread(new Runnable(){
            public void run(){
                JOptionPane.showMessageDialog(null, mssg);
            }
        });
        t.start();
    }
    
    public static void main(String[] args) {
        JFrame f = new JFrame("Reloj");
        f.setSize(270,80);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        RelojComponent reloj = new RelojComponent(1, 2, 2, 1, f.getContentPane());
        reloj.start();
        f.setResizable(false);
        f.setVisible(true);
    }
}
