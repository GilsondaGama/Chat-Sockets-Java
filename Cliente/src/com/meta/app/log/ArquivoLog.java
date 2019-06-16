package com.meta.app.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Gilson da Gama
 */
public class ArquivoLog {

    File arquivo;
    FileReader fileReader;
    BufferedReader bufferedReader;
    FileWriter fileWriter;
    BufferedWriter bufferedWriter;
    
    public ArquivoLog(String logs, String ip, String nome) {
        escreverLog(logs, ip, nome);
        
    }
    
    private void escreverLog(String logs, String ip, String nome) {        
        try {
            arquivo = new File("Cliente-"+nome+" - IP-"+ip+".log");
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
                escreverLog(logs, ip, nome);
                
            } catch (IOException ex1) {
                JOptionPane.showMessageDialog(null, "Erro ao gravar no arquivo de Logs.");
                System.exit(0);
            }        
        } catch (IOException er) {
            JOptionPane.showMessageDialog(null, "Erro ao gravar no arquivo de Logs.");
            System.exit(0);            
        }
                
    }
    
}
