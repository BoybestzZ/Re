/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jun
 */
public class check {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("192.168.1.38", 9100);
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            pw.println("hello");
            pw.close();

        } catch (IOException ex) {
            Logger.getLogger(check.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
