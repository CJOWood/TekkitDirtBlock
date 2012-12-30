package org.chriswood.plugin.TekkitDirtBlock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.*;

public class TekkitDirtBlockTalk extends Thread {
	private static final Logger LOG = Logger.getLogger("TekkitDirtBlock");
	private boolean running;
	private final String host;
	private final int port;
	private ServerSocket server;
	private TekkitDirtBlockHolder stats;
	
	public TekkitDirtBlockTalk(TekkitDirtBlockHolder stats, String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		this.stats = stats;
		this.running = true;
		initialize();
	}
	
	private void initialize() throws Exception {
		try{
			server = new ServerSocket();
			server.bind(new InetSocketAddress(host, port));
		}catch(Exception e) {
			LOG.log(Level.SEVERE, "[TekkitDirtBlock] Error initializing CraftStats+. Make sure your host and port are set correctly.", e);
		}
	}
	
	protected void shutdown(){
		running = false;
		if(server == null)
			return;
		try{
			server.close();
		}catch(Exception e) {
			LOG.log(Level.WARNING, "[TekkitDirtBlock] Could not shutdown cleanly!");
		}
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				Socket socket = server.accept();
				socket.setSoTimeout(5000);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				String line = in.readLine();
				if(line.equals("GoForIt")) {
					out.write(stats.toJson());
					out.newLine();
					out.flush();
					stats.clearData();
				}else if(line.contains("ping")){
						out.write("Pong!");
						out.newLine();
						out.flush();
				}else {
					out.write("Wut? Sending me the wrong stuffs yo!");
					out.newLine();
					out.flush();
					LOG.log(Level.WARNING, "[TekkitDirtBlock] Bad Socket Response.");
					//throw new Exception("[TekkitDirtBlock] Error with CraftStats response code. Contact Us.");
				}
				
				//System.out.println("Data Upload..." + stats.toJson());
				//Clear data after upload
				in.close();
				out.close();
				socket.close();
				
			} catch(SocketException e) {
				//LOG.log(Level.WARNING, "[TekkitDirtBlock] Server Reloaded? Protocol error. ");
			} catch(IOException e) {
				LOG.log(Level.WARNING, "[TekkitDirtBlock] Error reading/writing. ", e.getStackTrace());
			}catch(Exception e) {
				LOG.log(Level.WARNING, "Error, some exception caught.", e);
			} 
		}
	}
}
