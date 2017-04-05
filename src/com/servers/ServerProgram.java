package com.servers;

import java.util.Scanner;

public class ServerProgram implements ISocketListener {
	
	private Server server;
	
	public static void main(String[] args) throws Exception {
		ServerProgram serverProgram = new ServerProgram();
		serverProgram.start();
	}
	
	public void start() throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.print("PORT : ");
		int port = Integer.parseInt(scanner.nextLine());
		
		server = new Server(this);
		server.bind(port); // Open Server
		server.start(); // Start Accept Thread
		System.out.println("Server has started.");
		
		while(true) {
			String msg = scanner.nextLine();
			
			if(msg.isEmpty()) {
				break;
			}
			server.broadcast("Server >> " + msg);
		}
		
		scanner.close();
		server.stop();
		System.out.println("Server has closed.");
	}

	@Override
	public void onConnected(Channel channel) {
		String hostName = channel.getSocket().getInetAddress().getHostName();
		int port = channel.getSocket().getPort();
		
		String msg = "Client connected from " + hostName + ":" + port;
		System.out.println(msg);
		for(Channel c : server.getChannels()) {
			if(c != channel) {
				c.send(msg);
			}
		}
	}

	@Override
	public void onDisconnected(Channel channel) {
		server.remove(channel);
		String hostName = channel.getSocket().getInetAddress().getHostName();
		int port = channel.getSocket().getPort();
		
		String msg = "Client disconnected from " + hostName + ":" + port;
		System.out.println(msg);
		server.broadcast(msg);
	}

	@Override
	public void onReceive(Channel channel, String msg) {
		System.out.println(msg);
		server.broadcast(msg);
	}
}