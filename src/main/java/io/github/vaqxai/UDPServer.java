package io.github.vaqxai;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;

public class UDPServer extends Thread {

	protected DatagramSocket socket;
	protected boolean running;
	protected byte[] buf = new byte[256];
	/**
	 * The data in UDP messages is unprocessed, it may have line terminators, etc.
	 */
	protected LinkedList<Message> received = new LinkedList<>();

	/**
	 * 
	 * @return internal socket for direct usage
	 */
	public DatagramSocket getSocket() {
		return socket;
	}

	/**
	 * Creates a UDP Server at a given port
	 * @param port the port to create the server at
	 */
	public UDPServer(int port) {
		try{
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			System.out.println(e);
		}
	}

	/**
		* Creates a UDP Server at any port
	 */
	public UDPServer() {
		try{
			socket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println(e);
		}
	}

	/**
	 * @return the oldest received packet
	 */
	public Message get(){
		if (received.size() > 0) {
			return received.removeFirst();
		} else {
			System.out.println("Tried to get data from empty queue, returning nothing.");
			return null;
		}
	}

	/**
	 * Sends string data to the supplied address. Does not modify the input string.
	 * @param data the string to be sent
	 * @param address the receiver's hostname
	 * @param port the receiver's port
	 */
	public void send(String data, String address, int port){
		InetAddress destination = null;

		try {
			destination = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			System.out.println(String.format("Unknown host %s:%s while sending packet, aborting.", address, port));
			return;
		}

		buf = data.getBytes();

		DatagramPacket packet = new DatagramPacket(buf, buf.length, destination, port);
		try {
		socket.send(packet);
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public void run() {
		running = true;

		System.out.println("UDP SERVER START");

		while(running) {
			try {

				buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				InetAddress incomingAdddress = packet.getAddress();

				String receivedStr = new String(packet.getData(), 0, packet.getLength());
				System.out.println(String.format("SERVER got packet from %s:%s, contents: %s", incomingAdddress.getHostAddress(), packet.getPort(), receivedStr));
				received.add(new Message(receivedStr, incomingAdddress.getHostAddress(), packet.getPort()));

			} catch (IOException e) {

				System.out.println(e);

			}
		}

		System.out.println("UDP SERVER STOPPED");

	}
	
}