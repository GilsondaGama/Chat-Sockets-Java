package com.meta.app.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Gilson da Gama
 */
public class ScheduledThreadPoolServer {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static void logScheduled() {
        final Runnable loger = new Runnable() {
            @Override
            public void run() {
                System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " beep");
                
                //--------- Salvar LOG SERVIDOR -------------//                
                //new ArquivoLogS(txtAreaLOG.getText());             
                //this.txtAreaLOG.setText("");   
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(loger, 1, 9, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        logScheduled();
    }      
}
