/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientSever;

/**
 *
 * @author Roshen
 */

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleChatClient
{
    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;
    
    public void go() {
        JFrame frame = new JFrame("Simple Chat Client");
        JPanel mainPanel = new JPanel();
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        
        setUpNetworking();
        
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
        
        frame.setSize(650, 500);
        frame.setVisible(true);
        
    }
    
    private void setUpNetworking() {
        try {
            // attempt to open a socket connection to a known Server(IP Address) 
            // and Port number
            // (the server must be running! or an exception is thrown)
            sock = new Socket("10.102.11.25", 5000);  // localhost:5000 
            
            // get an input stream from the socket
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            
            // get an output stream from the socket
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established by client");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                writer.println(outgoing.getText());
                writer.flush();                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }
    
    public static void main(String[] args) {
        new SimpleChatClient().go();
    }
    
    /**
     * Runnable that executes in a Thread and continuously reads data from 
     * a socket until the socket is closed.  As this is executed in a thread,
     * it doesn't block the main thread from doing its work (e.g. allowing
     * user input, and reacting to clicks on the Send button)
     */
    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                // This loop continues to read from the BufferedReader (stream)
                // until the Socket is closed.  The readline() methods returns:
                // 1. when it encounters a end-of-line character, and
                // 2. when the socket is closed.
                // Note that if no data is currently being sent to this socket, 
                // the readLine() method does not return and is said to be in a 
                // blocked state, waiting for the next input.
                // The while loop is entered only when a line of text 
                // is read from the stream.
                while ((message = reader.readLine()) != null) {
                    System.out.println("client read " + message);
                    incoming.append(message + "\n");
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
