/*@author Agnes Aronsson [agar3573]*/
import java.io.*;
import java.net.*;

public class ServerThread implements Runnable {
	private Socket socket;
	private String name;
	private BufferedReader serverIn;
	private BufferedReader userIn;
	private PrintWriter out;
	
	public ServerThread(Socket socket, String name) {
		this.socket = socket;
		this.name = name;
	}
	
	@Override
	public void run() {
		try  {
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"), true);
			serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			userIn = new BufferedReader(new InputStreamReader(System.in));
			
			while(!socket.isClosed()) {
				if(serverIn.ready()) {
					String input = serverIn.readLine();
					if(input != null) {
						System.out.println(input);
					}
				}
				if(userIn.ready()) {
					out.println(name + " > " + userIn.readLine());
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
		}
	}
}
