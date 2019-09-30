/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author avinash
 */
public class ClientServer {

    /**
     * @param args the command line arguments
     */
    Vector<String> users = new Vector<String>();
    Vector<HandleClient> clients = new Vector<HandleClient>();

    public void process() throws Exception {
        ServerSocket server = new ServerSocket(18524);
        out.println("Server Started...");
        while (true) {
            Socket client = server.accept();
            
            //add incoming client to connected clients vector.
            HandleClient c = new HandleClient(client);
            clients.add(c);
            
        }  // end of while
    }

    public static void main(String... args) throws Exception {
        new ClientServer().process();
    } // end of main

    public void broadcast(String user, String message) {
        // send message to all connected users
        for (HandleClient c : clients) {
            c.sendMessage(user, message);
        }
    }

    /*
     * Inner class, responsible of handling incoming clients.
     * Each connected client will set as it's own thread.
     */
    class HandleClient extends Thread {

        String name = "";//client name/username
        BufferedReader input;//get input from client
        PrintWriter output;//send output to client

        public HandleClient(Socket client) throws Exception {
            // get input and output streams
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
            // read name
            name = input.readLine();
            users.add(name); // add to users vector
            broadcast(name, " Has connected!");
            start();
        }

        public void sendMessage(String uname, String msg) {
            output.println(uname + ": " + msg);
        }

        public void getOnlineUsers() {
            for (HandleClient c : clients) {
                for (int i = 0; i < users.size(); i++) {
                    broadcast("", users.get(i));
                }
            }
        }

        public String getUserName() {
            return name;
        }

        public void run() {
            String line;
            try {
                while (true) {
                    line = input.readLine();
                    if (line.equals("!end")) {
                        //notify all for user disconnection
                        broadcast(name, " Has disconnected!");
                        clients.remove(this);
                        users.remove(name);
                        break;
                    } else if(line.equals("!getusers")){
                        getOnlineUsers();
                        break;
                    }
                    broadcast(name, line); // method  of outer class - send messages to all
                }    // end of while
            } // try
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            
        } // end of run()
    } // end of inner class
    
}
