package gielda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private ServerSocket socket;
	private List<ConnectionHandler> connections;
	private int nextId;
	
	public BusinessLogicController blc;
	
	public class ConnectionHandler extends Thread {
		private Socket socket;
		public  BufferedReader reader;
		public  PrintWriter writer;
		private int id;
		public int playerId;
		private Interpreter interpreter;
		
		private ConnectionHandler(Socket socket, int id) {
			this.socket = socket;
			this.id = id;
			try {
				this.reader = new BufferedReader(
								new InputStreamReader(
										this.socket.getInputStream()));
				this.writer = new PrintWriter(this.socket.getOutputStream(), true);
			} catch (IOException e) {
				error("cannot open socket reader");
			}
			this.interpreter = new Interpreter(this);
		}
		
		public void run() {
			String buffer;
			try {
				while ((buffer = this.reader.readLine()) != null) {
					System.out.println(this+": "+buffer);
					
					int executionResult = this.interpreter.execute(buffer);
					
					if(executionResult == 1) {
						this.socket.close();
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				synchronized(Server.this.connections) {
					Server.this.connections.remove(this);
				}
				System.out.println("Server: "+this+" disconnected");
			}
		}
		
		public void error(String msg) {
			System.err.println(this+": "+msg);
		}
		
		public String toString() {
			return "Client (" + this.id + ")";
		}

		public Server getServer() {
			return Server.this; 
		}

		public void send(String string) {
			this.writer.println(string);
		}
	}
	
	public Server(int port) {
		try {
			this.socket = new ServerSocket(port);
			System.out.println("Server: successfully opened server socket on port: "+port);
		} catch (IOException  e) {
			System.err.println("Error: unable to open server socket on port: "+port);
			System.exit(-1);
		}
		
		this.connections = new ArrayList<ConnectionHandler>();
		this.blc = new BusinessLogicController();
	}
	
	public void listen() {
		while(true) {
			ConnectionHandler c;
			try {
				c = new ConnectionHandler(this.socket.accept(), ++nextId);
				synchronized (this.connections) {
					this.connections.add(c);	
				}
				c.start();
				System.out.println("Server: new "+ c +" connected");
			} catch(IOException e) {
				System.out.println("Error: unable to establish connection with client");
			}
		}
	}
	
	/**
	 * public static void main
	 * 
	 */
	
	public static void main(String [] args) {		
		Server server = new Server(9876);
		server.listen();
	}
}
