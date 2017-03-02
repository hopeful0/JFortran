package cn.hopefulme.jfortran;

import java.io.*;

public class FCompiler {

	/**
	 * 存储代码到指定路径
	 * @param code 待存储的代码
	 * @param file 存储路径
	 * @return 存储成功返回true，否则返回false
	 */
	public static boolean storeCode(String code, String path) {
		try {
			File file = new File(path);
			if(! file.createNewFile()) return false;
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(code.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			System.out.println("Compiler_storeCode_Exception:"+e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 存储代码到临时文件(temp/code.f90)
	 * @param code 待存储的代码
	 * @return true if store complete.
	 */
	public static boolean storeCode(String code) {
		File file = new File("./temp/");
		if(! file.exists())
			file.mkdir();
		file = new File("./temp/code.f90");
		if(file.exists())
			file.delete();
		return storeCode(code, "./temp/code.f90");
	}

	/**
	 * 编译文件
	 * @param src 源代码文件相对路径
	 * @param exe 生成的可执行文件的路径
	 * @return 编译器返回信息
	 */
	public static String compileFile(String src, String exe) {
		Terminal terminal = new Terminal("gfortran " + src + " -o " + exe);
	 	terminal.start();
	 	try {
			terminal.process.waitFor();
		} catch (InterruptedException e) {}
		return terminal.output();
	}
	
	/**
	 * 编译代码
	 * @param code 源代码
	 * @param exe 生成的可执行文件的路径
	 * @return 编译器返回信息
	 */
	public static String compileCode(String code, String exe) {
		if(storeCode(code)) {
			return compileFile("temp/code.f90", exe);
		} else {
			return "代码存储失败，请重试！\n";
		}
	}

	/**
	 * 编译测试
	 * @param code 源代码
	 * @return 编译器返回信息
	 */
	public static String compileTest(String code) {
		String res = compileCode(code, "./temp/exe");
		if(res.length() == 0) 
			res = "编译成功！\n";
		else
			res = "编译失败！\n" + res;
		new Terminal("rm ./temp/exe").start();
		return res;
	}

}
