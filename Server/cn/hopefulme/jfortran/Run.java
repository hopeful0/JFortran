package cn.hopefulme.jfortran;

import java.io.*;

public class Run {

	/**
	 * 运行程序
	 * @param is socket输入流，用来读取客户端输入
	 * @param br socket输入流，用来按行读取客户端输入
	 * @param os socket输出流，向客户端返回信息
	 * @param callback 回调，程序执行结束后调用
	 */
	public static void run(InputStream is, BufferedReader br, OutputStream os, Runnable callback) {
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
				return;
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
		} finally {
			//执行回调
			callback.run();
		}
	}

}
