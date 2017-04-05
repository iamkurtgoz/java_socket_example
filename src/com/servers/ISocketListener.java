package com.servers;

public interface ISocketListener {
	void onConnected(Channel channel);
	void onDisconnected(Channel channel);
	void onReceive(Channel channel, String msg);
}