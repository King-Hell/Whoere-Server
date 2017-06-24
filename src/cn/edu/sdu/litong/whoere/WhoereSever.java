package cn.edu.sdu.litong.whoere;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WhoereSever {
	private static final int PORT=49400;
	protected static ArrayList<Socket> mClientList=new ArrayList<Socket>();
	private static ExecutorService mExecutor=null;
	private static ServerSocket server=null;
	
	public static void main(String[] args) {
		try{
			server = new ServerSocket(PORT);
		
		mExecutor=Executors.newCachedThreadPool();
		System.out.println("服务器已启动，等待客户端连接...");
		Socket clientSocket=null;
		while (true) {
			
			clientSocket = server.accept();
			
			mClientList.add(clientSocket);
			mExecutor.execute(new ClientThread(clientSocket));
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
