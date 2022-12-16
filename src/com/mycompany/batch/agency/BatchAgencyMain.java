package com.mycompany.batch.agency;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class BatchAgencyMain {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(50001);
		while(true) {
			Socket socket = serverSocket.accept();
			System.out.println("연결 됨");
			
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
			String path = dis.readUTF();
			System.out.println("path: " + path);
			
			dis.close();
			dos.close();
			socket.close();
			System.out.println("연결 끊김");
		}
	}
}
