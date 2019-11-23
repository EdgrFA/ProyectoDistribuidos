package algoritmoberkeley;

public class RelojBerkeley {
    private String IP;
    private int diferenciaTiempo;
    private int ajuste;
    private boolean valido;

    public RelojBerkeley(String IP, int diferenciaTiempo) {
        this.IP = IP;
        this.diferenciaTiempo = diferenciaTiempo;
        valido = true;
    }

    public int getDiferenciaTiempo() {
        return diferenciaTiempo;
    }

    public void setAjuste(int ajuste) {
        this.ajuste = ajuste;
    }

    public int getAjuste() {
        return ajuste;
    }
    
    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getIP() {
        return IP;
    }
}
