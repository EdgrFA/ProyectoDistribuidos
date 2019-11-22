/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Caracteres;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Josh Plata
 */
public class Contador {
    
    public static int arreglo [][]= new int[2][27];  

    public Contador() {        
            for (int j = 0; j < 27; j++) {
                arreglo[0][j]=-1;
                arreglo[1][j]=0;               
            }
        
    }
    
    public int[][] Cuenta_Caracter(String archivo) throws FileNotFoundException, IOException{        
        String cadena;                      
        int indicador=0;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        
            while((cadena = b.readLine())!=null){
                cadena=cadena.toLowerCase();
                for (int k = 0; k < cadena.length(); k++) {
                    indicador=buscar(cadena.charAt(k));
                    arreglo[1][indicador]++;
                }
            }
        
        f.close();
        b.close();
        return arreglo;
    }
    
    public int buscar(char chapo){
        
        for (int i = 0; i < 27; i++) {           
            if(arreglo[0][i]==-1){
                arreglo[0][i]=(int)chapo;
                return i;
            }else if(arreglo[0][i]==chapo){                                     
                    return i;
            }            
        }
        return 27;
    }
    
   /* public static void main(String [] args) throws IOException{
                
        Contador cont=new Contador();
        cont.Cuenta_Caracter("Suma2.txt");
        
        for (int i = 0; i < 27; i++) {
            char caracter= (char) arreglo[0][i];
            System.out.println(caracter+" "+ arreglo[1][i]);
        }
    }*/
    
}
