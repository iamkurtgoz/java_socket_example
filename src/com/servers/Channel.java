package com.servers;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Channel implements Runnable {

	private Socket socket;
	private Scanner reader;
	private PrintWriter writer;
	
	private boolean isRunning;
	
	private ISocketListener socketListener;
	
	public Channel(Socket socket, ISocketListener socketListener) {
		this.socket = socket;
		this.socketListener = socketListener;
	}
	
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws Exception {
		isRunning = false;
		
		writer.close();
		reader.close();
		socket.close();
	}

	@Override
	public void run() {
		try {
			reader = new Scanner(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream());
			
			if(socketListener != null) {
				socketListener.onConnected(this);
			}
			
			isRunning = true;
			while(isRunning) {
				try {
					String msg = reader.nextLine();
					
					if(socketListener != null) {
						socketListener.onReceive(this, msg);
					}
				} catch(Exception e) {
					System.out.println("Server has closed.");
					break;
				}
			}
			
			if(socketListener != null) {
				socketListener.onDisconnected(this);
			}
		} catch(Exception e) {}
	}
	
	public void send(String msg) {
		writer.println(msg);
		writer.flush();
	}
	
	public Socket getSocket() {
		return socket;
	}
}