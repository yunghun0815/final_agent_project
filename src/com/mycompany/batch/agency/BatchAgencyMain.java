package com.mycompany.batch.agency;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;

import org.json.JSONObject;

public class BatchAgencyMain {
	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(50001);
		while(true) {
			Socket socket = serverSocket.accept();
			System.out.println("연결 됨");
			
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
			
			String path = dis.readUTF();
			int lastDot = path.lastIndexOf(".");
			
			//확장자
			String extension = path.substring(lastDot +1);
			System.out.println(extension);
			if(extension.equals("jar")) {
				path = "java -jar " + path;
			}
			Process process = Runtime.getRuntime().exec(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = null;
			StringBuffer sb = new StringBuffer();
			while((line = br.readLine()) != null ) {
				sb.append(line);
			}
			System.out.println("실행결과 : " + sb.toString());
			dos.writeUTF(sb.toString());
			dos.flush();
			//System.out.println("path: " + path);
			
			dis.close();
			dos.close();
			socket.close();
			System.out.println("연결 끊김");
		}
	}
}
