/*@author Agnes Aronsson [agar3573]*/
package streamSockets;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {
	private JTextField userText; // input field
	private JTextArea chatWindow; // message feed
	private BufferedReader in; // getting text from server
	private PrintWriter out; // sending text to server
	private String message = ""; // overwritten when client user hits enter
	private String serverIP;
	private int serverPort;
	private Socket connection;
	
	// constructor setting up GUI
	public Client(String host, int port) {
		super("Client");
		serverIP = host;
		serverPort = port;
	
		userText = new JTextField();
		userText.setText("Enter message here...");
		userText.setEditable(false); // user can not type until connected to server
		userText.addFocusListener(
			new FocusListener() {
				public void focusGained(FocusEvent event) {
					userText.setText("");
				}
				public void focusLost(FocusEvent event) {
					userText.setText("Enter message here...");
				}
			}
		);
		userText.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(400,200);
		setVisible(true);
	}
	
	// program body
	public void start() {
		try {
			connectToServer();
			setupStreams();
			chat();
		} catch(SocketException se) {
			showMessage("\nServer terminated the connection!");
		} catch(IOException ioE) {
			ioE.printStackTrace();
		} finally {
			closeDown();
		}
	}
	
	// connect client to server
	private void connectToServer() {
		showMessage("Trying to connect...");
		try {
			connection = new Socket(InetAddress.getByName(serverIP), serverPort);
		} catch(IOException ioE) { // if connection can not be established..
			showMessage("\nUnable to connect to server host: " + serverIP + " Port: " + serverPort + "\nClosing down...");
			try {
				Thread.sleep(5000); // (allow user time to read messages)
			} catch(InterruptedException ie) {}
			System.exit(1); // close down program
		}
		setTitle("Connected to Host: " + connection.getInetAddress().getHostName() + " Port: " + connection.getPort());
		showMessage("\nConnection established, start chat!\n");
	}

	// set up streams to send and receive messages
	private void setupStreams() throws IOException {
		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "ISO-8859-1"), true);
		out.flush();
	}

	// while chatting with server
	private void chat() throws IOException {
		ableToType(true);
		while(true) {
			message = in.readLine();
			showMessage("\n" + message); // print incoming messages from server in chat feed
		}
	}

	// close the streams and sockets
	private void closeDown() {
		showMessage("\nClosing down...");
		ableToType(false);
		try {
			out.close();
			in.close();
			connection.close();
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
		try {
			Thread.sleep(5000);
		} catch(InterruptedException ie) {}
		System.exit(0);
	}

	// send messages to server
	private void sendMessage(String message) {
		out.println(message);
	}
	
	// update chat feed
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(message); // add messages to and from client to chat feed in a Thread
				}
			}
		);	
	}
	
	// enable/disable input field
	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					userText.setEditable(b);
				}
			}
		);
	}
	
	// main method
	public static void main(String[] args) {
		Client charlie; // the client is called Charlie
		if(args.length == 2) {
			charlie = new Client(args[0], Integer.parseInt(args[1]));
		} else if(args.length == 1) {
			charlie = new Client(args[0], 2000);
		} else {
			charlie = new Client("127.0.0.1", 2000);
		}
			
		charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		charlie.start();
	}
}
