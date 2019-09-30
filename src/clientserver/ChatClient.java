import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient extends JFrame implements ActionListener {
String uname;
PrintWriter pw;
BufferedReader br;
JTextArea taMessages, taUserList;
JTextField tfInput;
JButton btnSend, btnExit;
Socket client;

public ChatClient(String uname, String servername) throws Exception {
    super("Connected as: " + uname);  // set title for frame
    this.uname = uname;
    client = new Socket(servername, 18524);
    br = new BufferedReader(new InputStreamReader(client.getInputStream()));
    pw = new PrintWriter(client.getOutputStream(), true);
    pw.println(uname);  // send name to server
    //bring up the chat interface
    buildInterface();
    new MessagesThread().start();  // create thread that listens for messages
}

public void buildInterface() {
    btnSend = new JButton("Send");
    btnExit = new JButton("Exit");
    //chat area
    taMessages = new JTextArea();
    taMessages.setRows(10);
    taMessages.setColumns(50);
    taMessages.setEditable(false);
    //online users list
    taUserList = new JTextArea();
    taUserList.setRows(10);
    taUserList.setColumns(10);
    taUserList.setEditable(false);
    //top panel (chat area and online users list
    JScrollPane chatPanel = new JScrollPane(taMessages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JScrollPane onlineUsersPanel = new JScrollPane(taUserList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    JPanel tp = new JPanel(new FlowLayout());
    tp.add(chatPanel);
    tp.add(onlineUsersPanel);
    add(tp, "Center");
    //user input field
    tfInput = new JTextField(50);
    //buttom panel (input field, send and exit)
    JPanel bp = new JPanel(new FlowLayout());
    bp.add(tfInput);
    bp.add(btnSend);
    bp.add(btnExit);
    add(bp, "South");
    btnSend.addActionListener(this);
    tfInput.addActionListener(this);//allow user to press Enter key in order to send message
    btnExit.addActionListener(this);
    setSize(500, 300);
    setVisible(true);
    pack();
}

@Override
public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btnExit) {
        pw.println("!end");  // send end to server so that server know about the termination
        System.exit(0);
    } else if(tfInput.getText().contains("!getusers")){
            pw.println("!getusers");
    }else{
        // send message to server
        pw.println(tfInput.getText());
    }
}

public static void main(String args[]) {

    // take username from user
    String name = JOptionPane.showInputDialog(null, "Enter your name: ", "Username",
            JOptionPane.PLAIN_MESSAGE);
    String servername = "localhost";
    try {
        new ChatClient(name, servername);
    } catch (Exception ex) {
        out.println("Unable to connect to server.\nError: " + ex.getMessage());
    }
    
} // end of main

// inner class for Messages Thread
class MessagesThread extends Thread {

    @Override
    public void run() {
        String line;
        try {
            while (true) {
                line = br.readLine();
                taMessages.append(line + "\n");
                taMessages.setCaretPosition(taMessages.getDocument().getLength());//auto scroll to last message
            } // end of while
        } catch (Exception ex) {
        }
    }
    }
} //  end of client
