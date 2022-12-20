package com.mycompany.batch.agency;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class BatchAgencyMain {
	public static void main(String[] args) throws Exception {
		
		//내 아이피 + 50001포트 socket 서버 open
		ServerSocket serverSocket = new ServerSocket(50001);
		while(true) {
			Socket socket = serverSocket.accept();
			System.out.println("연결 됨");
			
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
			
			String path = dis.readUTF();
			int lastDot = path.lastIndexOf(".");
			
			//확장자 분기처리
			String extension = path.substring(lastDot +1);
			System.out.println(extension);
			if(extension.equals("jar")) {
				path = "java -jar " + path;
			}
			
			// 받아온 path를 통해서 명령어 실행
			Process process = Runtime.getRuntime().exec(path);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String line = null;
			StringBuffer sb = new StringBuffer();
			
			//실행한 결과 읽기
			while((line = br.readLine()) != null ) {
				sb.append(line);
			}
			System.out.println("실행결과 : " + sb.toString());
			
			//실행한 결과 다시 보내기
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
