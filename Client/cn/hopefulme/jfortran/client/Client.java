package cn.hopefulme.jfortran.client;

import java.io.*;
import java.net.Socket;

public class Client {
	
	public static Socket socket;

	public static InputStream is;

	public static BufferedReader br;

	public static OutputStream os;

	public static Thread thread;

	public static Callback callback;

	/**
	 * 连接服务器端，并取得输入输出流
	 */
	private static void connect() {
		try {
			socket = new Socket("hopefulme.cn", 1552);
			if(socket != null) {
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
				os = socket.getOutputStream();
			 }
		} catch (IOException e ){
			System.out.println("Client_connect_IOException:" + e.getMessage());
		}
	}

	/**
	 * 关闭输入输出流，断开连接
	 */
	private static void disconnect() {
		try {
			if(os != null) os.close();
			if(br != null) br.close();
			if(is != null) is.close();
			if(socket != null) socket.close();
			os = null;
			br = null;
			is = null;
			socket = null;
		} catch (IOException e ){
			System.out.println("Client_disconnect_IOException:" + e.getMessage());
		}
	}

	/**
	 * 发送一个命令
	 * @param cmd 待发送的命令
	 */
	public static void sendCommand(String cmd) {
		if(null == os) return;
		try {
			os.write((cmd + "\n").getBytes());
			os.flush();
		} catch (IOException e ){
			System.out.println("Client_sendCommand_IOException:" + e.getMessage());
		}
	}

	/**
	 * 发送一段代码
	 * @param code 待发送的代码
	 */
	public static void sendCode(String code) {
		if(null == os) return;
		try {
			os.write((code + "\n**##*#*#code ended**##*#*#\n").getBytes());
			os.flush();
		} catch (IOException e ){
			System.out.println("Client_sendLine_IOException:" + e.getMessage());
		}
	}

	/**
	 * 发送一段输入
	 * @param input 待发送的输入
	 */
	public static void sendInput(String input) {
		if(null == os) return;
		try {
			os.write((input + "\n**##*#*#input ended**##*#*#\n").getBytes());
			os.flush();
		} catch (IOException e ){
			System.out.println("Client_sendLine_IOException:" + e.getMessage());
		}
	}

	/**
	 * 在新线程中开始一个连接
	 * @param callback 回调
	 */
 	public static void start(Callback callback) {
		Client.callback = callback;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				connect();
				if (null == socket || null == is || null == os) {
					callback.connectFailed();
					return;
				}
				callback.connected();
				try {
					String output;
					while ((output = br.readLine()) != null) {
						callback.output(output);
					}
				} catch (IOException e) {
					System.out.println("Client_start_IOException:" + e.getMessage());
				}
				disconnect();
				callback.disconnected();
			}
		});
		thread.start();
	}

	/**
	 * 停止当前连接
	 */
	public static void stop() {
		if (null == thread) return;
		if (thread.isAlive()) thread.interrupt();
		disconnect();
		if (null != callback) callback.disconnected();
		callback = null;
		thread = null;
	}

	public static abstract class Callback {
		public void output(String msg){};
		public void connected(){};
		public void connectFailed(){};
		public void disconnected(){};
	}

}
