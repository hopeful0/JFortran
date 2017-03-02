package cn.hopefulme.jfortran;

import java.io.*;
import java.nio.ByteBuffer;

public class Terminal {

	//进程创建器
	ProcessBuilder pb;
	//进程
	public Process process;
	//输出流
	public InputStream out;
	//输入流
	public OutputStream in;

	public Terminal(String cmd) {
		pb = new ProcessBuilder(cmd.split(" "));
		pb.directory(new File("./"));
		pb.redirectErrorStream(true);
	}

	public void start() {
		try {
			process = pb.start();
			out = process.getInputStream();
			in = process.getOutputStream();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void input(String input) {
		try {
			in.write(input.getBytes());
			in.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public String output() {
		try {
			ByteBuffer bytes = ByteBuffer.allocate(102400);
			byte[] buffer = new byte[1024];
			int len;
			while(bytes.position() < out.available() && (len = out.read(buffer)) != -1) {
				bytes.put(buffer, 0, len);
			}
			return new String(bytes.array(), 0, bytes.position());
		} catch (IOException e) {
			System.out.println(e.getMessage());
 		}
		return null;
 	}
 
}
