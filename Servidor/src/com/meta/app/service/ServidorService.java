package com.meta.app.service;

import com.meta.app.bean.ChatMessage;
import com.meta.app.bean.ChatMessage.Action;
import com.meta.app.controle.ConexaoBD;
import com.meta.app.log.ArquivoLogS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;


/**
 * @author Gilson da Gama
 */
public class ServidorService {   
    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();    
       
    //BackUp SERVIDOR
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);        

public JTextArea txtAreaLOG = new JTextArea();
    
    public ServidorService() {               
        try {
            serverSocket = new ServerSocket(5555);
            System.out.println("Servidor on!");
    
            while (true) {
                socket = serverSocket.accept();
                new Thread(new ListenerSocket(socket)).start();
            }

        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    private class ListenerSocket implements Runnable {
        private ObjectOutputStream output;
        private ObjectInputStream input;

        
        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream (socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            //BackUp SERVIDOR     
            logScheduled();

            ConexaoBD conecta = new ConexaoBD();
            conecta.conexao();
                        
            ChatMessage message = null;            
            try {
                while ((message = (ChatMessage) input.readObject()) != null) {
                    Action action = message.getAction();
                                       
                    if (action.equals(Action.CONNECT)) {                                 
                        try {
                            conecta.executaSql("SELECT * FROM login WHERE email = '"+message.getEmail()+"'");
                            conecta.rs.first();                                  
                                    
                            if (conecta.rs.getString("senha").equals(message.getSenha())) {
                                message.setName(conecta.rs.getString("nome"));
                               
                                boolean isConnect = connect(message, output);
                                if (isConnect) {
                                    mapOnlines.put(message.getName(), output);
                                    sendOnlines();                                    
                                }                              
                            } else {
                                JOptionPane.showMessageDialog(null, "Usuário ou Senha não encontrados!");  
                            }                            
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Usuário ou Senha não encontrados!" +ex);                         
                        }       
 
                    } else if (action.equals(Action.REGISTER)) {

                        try {
                            conecta.executaSql("SELECT * FROM login WHERE email = '"+message.getEmail()+"'");
                            if (conecta.rs.first()) {                                
                                JOptionPane.showMessageDialog(null, "Usuário já cadastrado!");  
                            } else {
                                try {
                                    PreparedStatement pst = conecta.con.prepareStatement("INSERT INTO login(email, senha, nome) VALUES (?, ?, ?)");
                                    pst.setString(1, message.getEmail());
                                    pst.setString(2, message.getSenha());
                                    pst.setString(3, message.getName());
                                    pst.execute();

                                    JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso!");                         
                                    boolean isConnect = connect(message, output);
                                    if (isConnect) {
                                        mapOnlines.put(message.getName(), output);
                                        sendOnlines();                                    
                                    }                              
                                } catch (SQLException ex) {
                                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar!\n" +ex);                         
                                }                                                                 
                            }                              
                        } catch (SQLException ex) {        
                            JOptionPane.showMessageDialog(null, "Usuário já cadastrado!");        
                        }                        
                        
                    } else if (action.equals(Action.DISCONNECT)) {
                        disconnect(message, output);
                        sendOnlines();
                        return;
                    } else if (action.equals(Action.SEND_ONE)) {                                               
                        sendOne(message);                        
                        //--------- Adicionar Mensagem para LOG -------------//                        
                        txtAreaLOG.append("Nome: "+ message.getName() +"\n"+ "disse: " + message.getText() + "\n"+ "Em: "+ getAgora() +"\n\n");    
                        
                    } else if (action.equals(Action.SEND_ALL)) {
                        sendAll(message);                           
                        //--------- Adicionar Mensagem para LOG -------------//                        
                        txtAreaLOG.append("Nome: "+ message.getName() +"\n"+ "disse: " + message.getText() + "\n"+ "Em: "+ getAgora() +"\n\n");      
                        
                    }
                }
            } catch (IOException ex) {
                ChatMessage cm = new ChatMessage();
                cm.setName(message.getName());
                disconnect(cm, output);
                sendOnlines();
                System.out.println(message.getName() + " deixou o chat!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            conecta.desconecta();
        }        
    }    
    
    private boolean connect(ChatMessage message, ObjectOutputStream output) {
        if (mapOnlines.size() == 0) {
            message.setText("YES");
            send(message, output);
            return true;
        }

        if (mapOnlines.containsKey(message.getName())) {
            message.setText("NO");
            send(message, output);
            return false;
        } else {
            message.setText("YES");
            send(message, output);
            return true;
        }
    }

    private void disconnect(ChatMessage message, ObjectOutputStream output) {
        mapOnlines.remove(message.getName());
        message.setText(" até logo!");
        message.setAction(Action.SEND_ONE);
        sendAll(message);

        System.out.println("User " + message.getName() + " sai da sala");
    }

    private void send(ChatMessage message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
           
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendOne(ChatMessage message) {                       
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getNameReserved())) {
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }  
    }

    private void sendAll(ChatMessage message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (!kv.getKey().equals(message.getName())) {
                message.setAction(Action.SEND_ONE);
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendOnlines() {
        Set<String> setNames = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNames.add(kv.getKey());
        }

        ChatMessage message = new ChatMessage();
        message.setAction(Action.USERS_ONLINE);
        message.setSetOnlines(setNames);

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            message.setName(kv.getKey());
            try {
                kv.getValue().writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
    
    private String getAgora() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }      
   
    
    //BackUp SERVIDOR
    private void logScheduled() {
        final Runnable loger = new Runnable() {
            @Override
            public void run() {
            //--------- Salvar LOG SERVIDOR -------------//                               
                if (!txtAreaLOG.getText().equals("")) {
                    new ArquivoLogS(txtAreaLOG.getText());  
                    txtAreaLOG.setText(""); 
                }
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(loger, 1, 9, TimeUnit.SECONDS);
    }       
}
