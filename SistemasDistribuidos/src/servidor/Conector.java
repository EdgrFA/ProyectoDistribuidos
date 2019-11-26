package servidor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conector {
    private static final String URL = "jdbc:mysql://localhost:3306/Distribuidos";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public Conector() {
    }
    
    public static Connection getConnection(){
        Connection con=null;
        try{
        Class.forName("com.mysql.jdbc.Driver");
        con = (Connection) DriverManager.getConnection(URL, USER, PASSWORD);
        }catch(Exception e){
           System.out.println(e); 
        }
        return con;
    }
    public void Insertar(String ip,int suma[][],int hora,int minutos,int segundos) throws SQLException, ClassNotFoundException{
        Connection con=null;
        try{
            con=getConnection();
            PreparedStatement ps;
            ps=con.prepareStatement("insert into datos(c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c24,c25,c26,c27,"
                                                    + "r1,r2,r3,r4,r5,r6,r7,r8,r9,r10,r11,r12,r13,r14,r15,r16,r17,r18,r19,r20,r21,r22,r23,r24,r25,r26,r27"
                                                    + ",ip,hora,minutos,segundos) "
                                                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
                                                    + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"                                                                                                                                   
                                                    + ",?,?,?,?)");
            
            for (int i = 0; i < 27; i++) {
                ps.setInt(i+1, suma[0][i]);
                ps.setInt(i+27+1, suma[1][i]);
            }            
            ps.setString(55, ip);
            ps.setInt(56, hora);
            ps.setInt(57, minutos);
            ps.setInt(58, segundos);
            

            int res= ps.executeUpdate();
            if(res>0){
                System.out.println("Base: Guardado");
            }else{
                
            }
            con.close();
        }catch(Exception e){
            System.err.println(e);
        }
    }
    
}


