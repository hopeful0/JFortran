package cn.hopefulme.jfortran;

import java.net.Socket;
import java.io.*;

public class Run {

	/**
	 * 运行程序
	 * @param client socket客户端
	 * @param is socket输入流，用来读取客户端输入
	 * @param br socket输入流，用来按行读取客户端输入
	 * @param os socket输出流，向客户端返回信息
	 * @return 如果客户端关闭（心跳包发送失败），返回false，其他情况返回true
	 */
	public static boolean run(Socket client, InputStream is, BufferedReader br, OutputStream os) {
		try {
			//读取代码
			String code = "";
			/*
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String info;
			while((info=br.readLine()) != null) {
				if(info.equals("**##*#*#code ended**##*#*#")) break;
				code += info + "\n";
			}
			*/
			code = Server.readCode(br);
			//编译代码
			String compile = FCompiler.compileCode(code, "./temp/exe");
			if(compile.length() > 0) {
				os.write(("编译失败！\n"+compile).getBytes());
				os.flush();
				return true;
			}
			//执行程序
			Terminal terminal = new Terminal("./temp/exe");
			terminal.start();
	 		while(terminal.process.isAlive()) {
				String input = "";
				//有输入时获取输入
		 		if(is.available() > 0) {
					String data;
					while((data=br.readLine()) != null) {
						if(data.equals("**##*#*#input ended**##*#*#")) break;
						input += data + "\n";
		 		 	}
					terminal.input(input);
				} 
				String output = terminal.output();
				//有输出时输出信息
				if(output.length() > 0) {
					os.write(output.getBytes());
					os.flush();
	 			}
				//发送心跳包
				try {
					client.sendUrgentData(0XFF);
				} catch (IOException e) {
					return false;
				}
				Thread.sleep(30);
		 	}
			String output = terminal.output();
			//有输出时输出信息
		 	if(output.length() > 0) {
				os.write(output.getBytes());
				os.flush();
	 		}
		} catch (IOException e) {
			System.out.println("Run_run_IOException" + e.getMessage());
	 	} catch (InterruptedException e) {
			System.out.println("Run_run_InterruptedException" + e.getMessage());
		}
		return true;
	}

}
