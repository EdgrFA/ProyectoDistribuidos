package algoritmoberkeley;

import algoritmobully.AlgoritmoBully;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import reloj.RelojComponent;
import sources.Ports;

public class AlgoritmoBerkeley extends Thread{
    private int rangoError, zigma, limiteTiempo; //Valor en segundos 
    private String [] IPs = new String[2];
    private final RelojComponent reloj;
    private String ipAdmin;
    private AlgoritmoBully bully;

    public AlgoritmoBerkeley(String [] IPs, AlgoritmoBully bully, RelojComponent reloj) {
        this.IPs = IPs;
        this.reloj = reloj;
        //Contadores
        this.zigma = 8;
        this.rangoError = 1800;
        this.limiteTiempo = 20;
        this.bully = bully;
        this.ipAdmin = null;
    }
    
    private int promediarRelojes(ArrayList<RelojBerkeley> relojes){
        int nodos = relojes.size() + 1;
        //Obtener Promedio
        int sumaTiempos = 0;
        for(int i =0; i < relojes.size(); i++){
            if(Math.abs(relojes.get(i).getDiferenciaTiempo()) > rangoError){
                nodos--;
                relojes.get(i).setValido(false);
            }else
                sumaTiempos += relojes.get(i).getDiferenciaTiempo();
        }
        
        //Calcular Ajuste
        int promSegundos = sumaTiempos/nodos;
        for(int i =0; i < relojes.size(); i++){
            int diferenciaTiempo = relojes.get(i).getDiferenciaTiempo();
            if(!relojes.get(i).isValido())
                relojes.get(i).setAjuste(-(diferenciaTiempo));
            else
                relojes.get(i).setAjuste(promSegundos - diferenciaTiempo);
        }
        return promSegundos;
    }
    
    private void ajustarServidorTiempo(int ajuste){
        //Ajustar Reloj Servidor
        if(ajuste < 0){
            reloj.realentizarTiempo(ajuste);
            System.out.println("Berkeley: Reducir tiempo");
        } else{
            reloj.adelantarTiempo(ajuste);
            System.out.println("Berkeley: Se ajusto tiempo recorriendo");
        }
    }
    
    private void registrarAjuste(String ip,int ajuste) {
        //Insertar registro de tiempo
        ConectorServidorTiempo con = new ConectorServidorTiempo();
        try {
            con.Insertar(ip, ajuste, reloj.getHoras(), reloj.getMinutos(), reloj.getSegundos());
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Berkeley: " + ex.toString());
        }
    }
    
    private void adminBerkeley(SocketServidorTiempo sst){
        //Consultar Tiempos Servidores
        System.out.println("Berkeley(Administrador): Ejecutando algoritmo.");
        ArrayList<RelojBerkeley> relojes = new ArrayList<>();
        for (int i = 0; i < IPs.length; i++) {
            try {
                System.out.println("Berkeley(Administrador): Consultando tiempo de " + IPs[i]);
                RelojBerkeley relojB = sst.consultarTiempoServidor(IPs[i]);
                relojes.add(relojB);
            } catch (IOException ex) {
                System.out.println("Berkeley(Administrador): " + ex.toString());
            }
        }

        //Promediar Tiempos
        System.out.println("Berkeley(Administrador): Promediando tiempos y enviando ajustes");
        int promSegundos = 0;
        if(!relojes.isEmpty())
            promSegundos = promediarRelojes(relojes);
        else{
            System.out.println("Berkeley(Administrador): Sin tiempos.");
            return;
        }

        //Ajustar Reloj Principal
        if(promSegundos != 0){
            registrarAjuste("ServidorReloj", promSegundos);
            ajustarServidorTiempo(promSegundos);
        }

        //Ajustar Relojes Servidores
        for(int i =0; i < relojes.size(); i++){
            int ajuste = relojes.get(i).getAjuste();
            if(ajuste != 0){
                registrarAjuste(relojes.get(i).getIP(), ajuste);
                try {
                    System.out.println("Berkeley(Administrador): Enviando ajuste a: " + relojes.get(i).getIP());
                    sst.enviarAjuste(relojes.get(i));
                } catch (IOException ex) {
                    System.out.println("Berkeley(Administrador): " + ex.toString());
                }
            }
        }
    }
    
    @Override
    public void run() {
        int adminActual = -1;
        SocketServidorTiempo sst = new SocketServidorTiempo(Ports.puertoBerkeley, reloj);
        for(;;){
            
            if(bully.getIdAdmin() == -1){
                //No existe aun servidor
                System.out.println("Berkeley: Buscando servidor.");
                bully.peticionEleccion(5);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    System.out.println("Berkeley(Sleep): " + ex.toString());
                }
            }
            
            else if(bully.administrando()){
                //Administrador Berkeley
                System.out.println("Berkeley: Administrando algoritmo.");
                while(bully.administrando()){
                    adminBerkeley(sst);
                    //Dormir Zigma segundos
                    try {
                        Thread.sleep(zigma*1000);
                    } catch (InterruptedException ex) {
                        System.out.println("Berkeley(Servidor): " + ex.toString());
                    }
                }
            }
            
            else{
                //Clientes Berkeley
                System.out.println("Berkeley: Modo cliente.");
                adminActual = bully.getIdAdmin();
                try {
                    sst.clienteBerkeley(limiteTiempo);
                } catch (IOException ex) {
                    System.out.println("Berkeley(Cliente): " + ex.toString());
                }
                if(adminActual == bully.getIdAdmin())
                    bully.setIdAdmin(-1);
            }
        }
    }
}
