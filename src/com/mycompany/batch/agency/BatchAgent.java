package com.mycompany.batch.agency;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BatchAgent{	
	// 필드
	ExecutorService threadPool;
	ServerSocket serverSocket;
	
	public void start() throws IOException {
		serverSocket = new ServerSocket(50001);
		log.info("[Batch Agency 시작]");
		
		threadPool = Executors.newFixedThreadPool(10);
		
		readMessage();
	}
	
	
	
	public void stop() throws IOException {
		serverSocket.close();
		threadPool.shutdown();
	}
	
	//클라이언트가 보낸 잡 파일 처리
	public void readMessage() {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						Socket socket = serverSocket.accept();
						log.info("배치서버에서 접속함");
						runApp(socket);
					}	
				} catch (Exception e) {
				}
			}
		});
	}
	
	
	public void runApp(Socket socket) {
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					DataInputStream dis = new DataInputStream(socket.getInputStream());					
					DataOutputStream dos = new DataOutputStream(socket.getOutputStream());					
					
					String path = dis.readUTF();
					int lastDot = path.lastIndexOf(".");
					
					//확장자 분기처리 bat/jar/sh
					String extension = path.substring(lastDot +1);
					log.info(extension);
					if(extension.equals("jar")) {
						path = "java -jar " + path;
					}else if(extension.equals("sh")) {
						path = "sh " + path;
					}
					
					try {
						// 받아온 path를 통해서 명령어 실행
						Process process = Runtime.getRuntime().exec(path);						
						BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));						
						String line = null;
						StringBuffer sb = new StringBuffer();
						
						//실행한 결과 읽기
						while((line = br.readLine()) != null ) {
							sb.append(line);
						}
						log.info("실행결과 : " + sb.toString());
						
						//실행한 결과 다시 보내기
						dos.writeUTF(sb.toString());
					}catch(Exception e) {
						dos.writeUTF("{response: 해당 프로그램 없음}");
					}finally {						
						dos.flush();						
						dis.close();
						dos.close();
						socket.close();
						log.info("연결 끊김");
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	public static void main(String[] args) throws Exception {
		BatchAgent batchAgent = new BatchAgent();
		batchAgent.start();
		
		
	}
}
