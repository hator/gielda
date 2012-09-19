package gielda;

import gielda.Tasks.Error;
import gielda.Tasks.Disconnect;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
	/** SINGLETON **/
	private static Server s;
	public static synchronized Server getServer() {
		if(s==null)
			s = new Server();
		return s;
	}
	
	/***********/
	private ServerSocket socket;
	private Map<Integer, ConnectionHandler> connections;
	private int nextId;
	
	public BusinessLogicController blc;
	
	/** ConnectionHandler **/
	public class ConnectionHandler extends Thread {
		private Socket			socket;
		private Interpreter 	interpreter;
		public  BufferedReader 	reader;
		public  PrintWriter 	writer;
		public  int 			id;
		
		
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
					
					try {
						this.interpreter.execute(buffer);
					} catch (Tasks.Error e) {
						this.error(e.getMsg());
					} catch(Tasks.Disconnect e) {
						this.socket.close();
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				synchronized(Server.this.connections) {
					Server.this.connections.remove(this.id);
				}
				System.out.println("Server: "+this+" disconnected");
			}
		}
		
		public void error(String msg) {
			System.err.println(this+": "+msg);
			this.send("error "+msg);
		}
		
		public String toString() {
			return "Client(" + this.id + ")";
		}

		public Server getServer() {
			return Server.this; 
		}

		public void send(String string) {
			this.writer.println(string);
		}

		public void setPlayerId(int playerId) {
			this.getServer().changeId(this.id, playerId);
			this.id = playerId;
		}
	} /** end of ConnectionHandler **/
	
	public void send(int playerId, String msg) {
		this.connections.get(playerId).send(msg);
	}

	public void changeId(int oldId, int newId) {
		synchronized (this.connections) {
			this.connections.put(newId, connections.remove(oldId));	
		}
	}
	
	public Server() {
		int port = 9876;
		try {
			this.socket = new ServerSocket(port);
			System.out.println("Server: successfully opened server socket on port: "+port);
		} catch (IOException  e) {
			System.err.println("Error: unable to open server socket on port: "+port);
			System.exit(-1);
		}
		
		this.connections = new HashMap<Integer, ConnectionHandler>();
		this.blc = new BusinessLogicController();
	}
	
	public void listen() {
		while(true) {
			ConnectionHandler c;
			try {
				c = new ConnectionHandler(this.socket.accept(), --nextId);
				synchronized (this.connections) {
					this.connections.put(nextId, c);	
				}
				c.start();
				System.out.println("Server: new "+ c +" connected");
			} catch(IOException e) {
				System.out.println("Error: unable to establish connection with client");
			}
		}
	}
	
	/** Main **/
	public static void main(String [] args) {		
		Server.getServer().listen();
	}
}
