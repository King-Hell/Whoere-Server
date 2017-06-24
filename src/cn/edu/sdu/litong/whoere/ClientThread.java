package cn.edu.sdu.litong.whoere;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import java.text.SimpleDateFormat;

import java.util.Date;

public class ClientThread implements Runnable {

	private Socket clientSocket;
	private BufferedReader in = null;
	private String message = "";
	private ObjectInputStream is = null;
	private ObjectOutputStream oo = null;
	String data = null;
	PrintWriter out = null;
	private boolean isRegister = false;
	private Account account = null;

	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;

	}

	public String info() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "：" + clientSocket.getInetAddress();
	}

	@Override
	public void run() {
		try {
			/*
			 * OutputStream outs = clientSocket.getOutputStream(); InputStream
			 * ins = clientSocket.getInputStream(); BufferedWriter out = new
			 * BufferedWriter(new OutputStreamWriter(outs)); BufferedReader in =
			 * new BufferedReader(new InputStreamReader(ins));
			 */

			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			out = new PrintWriter(clientSocket.getOutputStream());
			while ((data = in.readLine()) != null) {
				if (data.equals("LOGIN")) {
					System.out.println(info() + "尝试登录");
					out.println("LOGIN_YES");
					out.flush();
					// sendMessage("欢迎"+clientSocket.getInetAddress()+"进入聊天室");
					break;
				} else if (data.equals("REGISTER")) {
					System.out.println(info() + "尝试注册");
					out.println("REGISTER_YES");
					out.flush();
					isRegister = true;
					break;
				}
			}

			try {
				is = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
				Object object = is.readObject();
				// out = new PrintWriter(clientSocket.getOutputStream());
				account = (Account) object;
				account.setSocket(clientSocket.getInetAddress().toString());
				account.connect();
				if (isRegister) {
					if (account.existCheck()) {

						out.println("REGISTER_EXIST");
						System.out.println(info() + "账户已存在，创建失败");

					} else {
						synchronized (this) {
							account.insert();
						}
						
						account.match();
						if (account.isFind == true) {
							out.println("REGISTER_YES");
							System.out.println(info() + "账户创建成功");
						} else {
							out.println("REGISTER_NO");
							System.out.println(info() + "账户创建失败");
						}
					}
					close();
				} else {
					account.match();
					
					if (account.isFind&&account.isRight) {
						out.println("ACCOUNT_YES");
						out.flush();
						System.out.println(info() + "已找到账户");
						sendMessage("@SYSTEM@" + "欢迎" + account.getUsername());
					} else {
						out.println("ACCOUNT_NO");
						out.flush();
						System.out.println(info() + "密码错误或未找到该账户");
						close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			while (!clientSocket.isClosed()) {
				if ((data = in.readLine()) != null) {
					if (data.equals("BYE")) {
						close();
						break;
					} else if (data.charAt(0) == '$') {
						synchronized (this) {
							account.upadteLocation(data.substring(1));
						}
						
					} else if (data.equals("REQUEST_LOCATION")) {
						System.out.println(info() + "尝试更新位置信息");
						oo = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
						oo.writeObject(account.getLocation());
						oo.writeObject(null);
						oo.flush();
					} else {
						sendMessage("@" + account.getUsername() + "@" + data);
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void close() throws IOException {
		WhoereSever.mClientList.remove(clientSocket);
		message = account.getUsername() + "关闭连接\n目前在线人数：" + WhoereSever.mClientList.size();
		sendMessage("@SYSTEM@" + message);
		clientSocket.close();
		
		System.out.println(info() + "关闭连接");

	}

	public void sendMessage(String msg) {
		if (msg.length()<8||!msg.substring(1, 7).equals("SYSTEM")) {
			System.out.println(info() + "说：" + msg);
		}

		int count = WhoereSever.mClientList.size();
		for (int i = 0; i < count; i++) {
			Socket mSocket = WhoereSever.mClientList.get(i);
			if(mSocket==clientSocket){
				continue;
			}
			PrintWriter out = null;
			try {
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(),"UTF-8")));
				out.println(msg);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
