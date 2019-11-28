package algoritmobully;

public class ServidorInfo {
    private String IP;
    private int idServidor;
    private boolean activo;

    public ServidorInfo(String IP) {
        this.IP = IP;
        idServidor = -1;
        activo = false;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(int idServidor) {
        this.idServidor = idServidor;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        System.out.println("Se cambio estado de servidor " + IP + ":" + activo);
    }
}
