package com.servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable {

	private boolean running;
	
	private ServerSocket serverSocket;
	private ArrayList<Channel> channels;
	
	private ISocketListener listener;
	
	public Server(ISocketListener listener) {
		this.listener = listener;
	}
	
	public void bind(int port) throws Exception {
		this.serverSocket = new ServerSocket(port);
	}
	
	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void stop() throws Exception {
		running = false;
		serverSocket.close();
	}
	
	@Override
	public void run() {
		
		channels = new ArrayList<>();
		running = true;
		
		while(running) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("Client has connected.");
				
				Channel channel = new Channel(socket, listener);
				channel.start();
				
				channels.add(channel);
			} catch (IOException e) {
				break;
			}
		}
		
		try {
			for(Channel channel : channels) {
				channel.stop();
			}
			channels.clear();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void broadcast(String msg) {
		if(!running) {
			return;
		}
		
		for(Channel channel : channels) {
			channel.send(msg);
		}
	}
	
	public void remove(Channel channel) {
		if(!running) {
			return;
		}
		
		channels.remove(channel);
	}
	
	public ArrayList<Channel> getChannels() {
		return channels;
	}
}