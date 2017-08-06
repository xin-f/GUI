package miscelleaneous;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LaunchCMD {
	public static void main(String[] args) {
		/*cmd.exe /c 后跟文件,相当于双击,然后用默认关联的程序打开; 后跟程序,相当于双击打开程序,
		 * 但,后面只能跟一个参数,所以遇到带空格的路径时,要把路径用双引号包起来.
		 * 用变量表示时: ("\""+t_path+"\"")
		 * 
		 * cmd 加了/c参数后它将运行/c后面的命令, 不加只运行cmd. 可以在命令行试下CMD /C DIR C:和CMD DIR C:
		 * cmd /c dir 是执行完dir命令后关闭命令窗口。
		 * cmd /k dir 是执行完dir命令后不关闭命令窗口。
		 * cmd /c start dir 会打开一个新窗口后执行dir指令，原窗口会关闭。
		 * cmd /k start dir 会打开一个新窗口后执行dir指令，原窗口不会关闭。
		 */
		String file = "cmd.exe /c  \"D:\\Program Files\\readme.txt\"";//只能带一个参数,所以多加了一层引号,引号又经过转义.
		String program = "cmd.exe /c \"D:\\Program Files\\FirmwareUpdate.exe\"";
		String program1 = "D:\\Program Files\\FirmwareUpdate.exe";
		String file_prog = "D:\\tools\\Notepad++.7.3.bin.x64\\notepad++.exe D:\\Program Files\\readme.txt";//程序加要打开的文件.
		String ping = "ping 172.20.1.24 -t";
		String str;
		try {
//			Process p = Runtime.getRuntime().exec(file);
//			Process p1 = Runtime.getRuntime().exec(program);
//			Process p11 = Runtime.getRuntime().exec(program);
//			Process p2 = Runtime.getRuntime().exec(file_prog);
			Process p3 = Runtime.getRuntime().exec(ping);
			BufferedReader in = new BufferedReader(new InputStreamReader(p3.getInputStream()));
			while((str = in.readLine())!=null) 
				System.out.println(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

}
