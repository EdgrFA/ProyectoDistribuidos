package reloj;

import javax.swing.JTextField;

public class Reloj extends Thread{
    private int horas, minutos, segundos;
    private int tiempo, retraso;
    private JTextField Hora_completa;
    private boolean parar;
    
    public Reloj(int Horas, int Minutos, int Segundos, int tiempo) {
        this.horas = Horas;
        this.minutos = Minutos;
        this.segundos = Segundos;
        this.tiempo = tiempo;
        this.parar = false;
        this.Hora_completa = new JTextField("00:00:00");
    }
    
    public Reloj(int Horas, int Minutos, int Segundos) {
        this.horas = Horas;
        this.minutos = Minutos;
        this.segundos = Segundos;
    }
    
    public void setHoras(int Horas) {
        this.horas = Horas;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public void setSegundos(int segundo) {
        this.segundos = segundo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
    
    public int getMinutos() {
        return minutos;
    }

    public int getSegundos() {
        return segundos;
    }
    
    public int getHoras() {
        return horas;
    }

    public JTextField getHora_completa() {
        return Hora_completa;
    }
    
    public void realentizarTiempo(int ajuste){
        retraso = Math.abs(ajuste) + 1;
        if(retraso == 3)
            tiempo = 3;
        else if(retraso <= 4)
            tiempo = 2;
        else
            tiempo = 5;
    }
    
    public void pararReloj(){
        parar = true;
    }
    
    public void renaudarReloj(){
        parar = false;
        actualizarFormatoHoraCompleta(); 
    }
    
    public void adelantarTiempo(int segundos){
        int addHoras = this.horas + segundos/3600;
        int addMinutos = this.minutos + (segundos%3600)/60;
        int addSegundos = this.segundos + ((segundos%3600)%60);

        if(addSegundos/60 > 0){
            addMinutos += addSegundos/60;
            addSegundos = addSegundos%60;
        }
        if(addMinutos/60 > 0){
            addHoras += addMinutos/60;
            addMinutos = addMinutos%60;
        }
        addHoras = addHoras%24;
        
        //Actualizar hora
        this.segundos = addSegundos;
        this.minutos = addMinutos;
        this.horas = addHoras;
        tiempo = 1;
    }
    
    @Override
    public void run() {
        while(true){
            
            try {
                Thread.sleep(1000*tiempo);
            } catch (InterruptedException ex) {
                System.out.println(ex.toString());
            }
            
            segundos++;
            if(segundos >= 60){
                minutos++;
                segundos = 0;
            }
            if(minutos >= 60){
                horas++;
                minutos = 0;
            }
            if(horas >= 24)
                horas = 0;
            
            if(!parar)
                actualizarFormatoHoraCompleta(); 
            
            //Ajuste en caso de realentizar el reloj
            if(retraso > 0){
                retraso -= tiempo;
                if(retraso <= 0){
                    tiempo = 1;
                }else if(retraso == 3){
                    tiempo = 3;
                }else if(retraso <= 4)
                    tiempo = 2;
            }
        }
    }
    
    public void actualizarFormatoHoraCompleta(){
        String strHora = Integer.toString(horas), 
                strMinutos = Integer.toString(minutos), 
                strSegundos = Integer.toString(segundos);
        
        if(horas < 10)
            strHora = "0" + horas;
        if(minutos < 10)
            strMinutos = "0" + minutos;
        if(segundos < 10)
            strSegundos = "0" + segundos;
            
        Hora_completa.setText(strHora + ":" + strMinutos + ":" + strSegundos);  
    }
    
}
