package com.meta.app.service;

import com.meta.app.bean.ChatMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Gilson da Gama
 */
public class ClienteService {
    
    private Socket socket;
    private ObjectOutputStream output;
    
    public Socket connect(ChatMessage message) {
        try {
//          this.socket = new Socket("localhost", 5555);
            this.socket = new Socket(message.getIp(), Integer.parseInt(message.getPorta()));           
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(null, "Servidor não encontrado.");                     
//            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro na conexão com o servidor."); 
//            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);           
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Servidor não encontrado.");
        }
        
        return socket;
    }
    
    public void send(ChatMessage message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
