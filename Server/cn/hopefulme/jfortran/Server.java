package cn.hopefulme.jfortran;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Server {

	public static void run() {
		try {
			ServerSocket server = new ServerSocket(1552);
			while(true) {
				Socket client = server.accept();
				InputStream is = client.getInputStream();
				final OutputStream os = client.getOutputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String cmd = br.readLine();
				switch(cmd) {
					case "compile":
						String code = readCode(br);
						String output = FCompiler.compileTest(code);
						os.write(output.getBytes());
						os.flush();
						break;
					case "run":
						Run.run(client ,is, br, os);
						if (! client.isConnected()) break;
						try {
							os.write("程序运行结束。\n".getBytes());
							os.flush();
						} catch (IOException e) {
							System.out.println("Server_run_callback_IOException:" + e.getMessage());
						}
						break;
				}
				br.close();
				isr.close();
				is.close();
				os.close();
				client.close();
			}
		} catch (IOException e) {
			System.out.println("Server_run_IOException:" + e.getMessage());
		}
	}

	 public static String readCode(BufferedReader br) {
		String code = "";
		String data;
		try { 
			while((data = br.readLine()) != null) {
				if(data.equals("**##*#*#code ended**##*#*#")) break;
				code += data + "\n";
			}
		} catch (IOException e) {
			System.out.println("Server_readCode_IOException:" + e.getMessage());
		}
		return code;
	}

}
