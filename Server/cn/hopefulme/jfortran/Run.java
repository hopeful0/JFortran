package cn.hopefulme.jfortran;

import java.net.Socket;
import java.io.*;

public class Run {

	private static long lastHeart;

	/**
	 * 运行程序
	 * @param is socket输入流，用来读取客户端输入
	 * @param br socket输入流，用来按行读取客户端输入
	 * @param os socket输出流，向客户端返回信息
	 */
	public static void run(InputStream is, BufferedReader br, OutputStream os) {
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
			if(compile .length() > 0) {
				os.write(("编译失败！\n"+compile).getBytes());
				os.flush();
				return;
			}
			//执行程序
			Terminal terminal = new Terminal("./temp/exe");
			terminal.start();
			lastHeart = System.nanoTime();
	 		while(terminal.process.isAlive()) {
				if (System.nanoTime() - lastHeart > 1000000000) {
					System.out.println("Lose Connection!");
					break;
				}
				String input = "";
				//有输入时获取输入
				String data;
				while(is.available() > 0 && (data=br.readLine()) != null) {
					//忽略其中的心跳包
					if(data.equals("**##*#*#heart package**##*#*#")) {
						lastHeart = System.nanoTime();
						continue;
					}
					if(data.equals("**##*#*#input ended**##*#*#")) break;
					input += data + "\n";
		 		}
				if (input.length() > 0)
					terminal.input(input);
				String output = terminal.output();
				//有输出时输出信息
				if(output.length() > 0) {
					os.write(output.getBytes());
					os.flush();
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
	}

}
