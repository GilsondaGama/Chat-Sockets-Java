package com.meta.app.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.JOptionPane;


/**
 *
 * @author Gilson da Gama
 */
public class ArquivoLogS {

    File arquivo;
    FileReader fileReader;
    BufferedReader bufferedReader;
    FileWriter fileWriter;
    BufferedWriter bufferedWriter;
    
    public ArquivoLogS(String logs) {
        escreverLog(logs);
        
    }
    
    private void escreverLog(String logs) {    
        try {  
            arquivo = new File("Servidor-"+getDate().replaceAll("/", "-")+".log");
            
            fileReader = new FileReader(arquivo);
            bufferedReader = new BufferedReader(fileReader);
            
            Vector texto = new Vector();
            while (bufferedReader.ready()){
                texto.add(bufferedReader.readLine());                
            }       
            fileWriter = new FileWriter(arquivo);
            bufferedWriter =  new BufferedWriter(fileWriter);
            for(int i=0; i<texto.size(); i++) {
                bufferedWriter.write(texto.get(i).toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.write(logs);
            bufferedReader.close();
            bufferedWriter.close();
        } catch (FileNotFoundException ex) {
            try {
                arquivo.createNewFile();
                escreverLog(logs);
                
            } catch (IOException ex1) {
                JOptionPane.showMessageDialog(null, "Erro ao gravar no arquivo de Logs.");
                System.exit(0);
            }        
        } catch (IOException er) {
            JOptionPane.showMessageDialog(null, "Erro ao gravar no arquivo de Logs.");
            System.exit(0);            
        }
                
    }
    
    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
    return dateFormat.format(date);
}    
    
}
