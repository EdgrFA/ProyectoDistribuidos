package algoritmoberkeley;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConectorServidorTiempo {
    private static final String URL="jdbc:mysql://localhost:3306/relojServidor";
    private static final String USER="root";
    private static final String PASSWORD="root";

    public ConectorServidorTiempo() {
    }
    
    public static Connection getConnection(){
        Connection con=null;
        try{
        Class.forName("com.mysql.jdbc.Driver");
        con=(Connection) DriverManager.getConnection(URL, USER, PASSWORD);
        }catch(Exception e){
           System.out.println(e); 
        }
        return con;
    }
    
    public void Insertar(String ip,int ajuste, int hora,int minutos,int segundos) throws SQLException, ClassNotFoundException{
        Connection con=null;
        try{
            con=getConnection();
            PreparedStatement ps;
            ps=con.prepareStatement("insert into ajustes(ip,ajuste,hora,minutos,segundos) values (?,?,?,?,?)");
            ps.setString(1, ip);
            ps.setInt(2, ajuste);
            ps.setInt(3, hora);
            ps.setInt(4, minutos);
            ps.setInt(5, segundos);
            

            int res= ps.executeUpdate();
            if(res>0){
                System.out.println("Ajuste guardado");
            }else{
                System.out.println("Error al Guardadar");
            }
            con.close();
        }catch(Exception e){
            System.err.println(e);
        }
    }
}
