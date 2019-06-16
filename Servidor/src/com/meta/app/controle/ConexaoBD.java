package com.meta.app.controle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Gilson da Gama
 */

public class ConexaoBD {
    public Statement stm;
    public ResultSet rs;
    private final String database = "chat";
    private final String driver = "jdbc:mysql:";
    private final String caminho = "//localhost/";
    private final String usuario = "root";
    private final String senha = "";
    public Connection con;
            
    public void conexao() {
        System.setProperty("jbdc.Drives", driver);
        try {
            con=DriverManager.getConnection(driver+caminho+database, usuario, senha);
        } catch (SQLException ex) {
           JOptionPane.showMessageDialog(null, "Erro na CONEXAO: \n"+ex);          
        }
    }
    
    public void executaSql(String sql) {
        try {
            stm =  con.createStatement(rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY);
            rs = stm.executeQuery(sql);
        } catch (SQLException ex) {
           JOptionPane.showMessageDialog(null, "Erro na CONSULTA: \n"+ex.getMessage());                     
        }
        
    }

    public void desconecta() {
        try {
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro na DESCONEXAO: \n"+ex.getMessage());          
        }
    }

} 
