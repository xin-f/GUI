package miscelleaneous;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class PairSocketClient {
	static InputStream in;
	static OutputStream out;
	static String host = "172.20.1.1";
	/**
	 * This code together with PairServer.java, shows how to re-use a socket and in/out stream instead of create a new one.
	 *  1) In PairServer.java, server listen on 50000 port and if there's connection request, establish a socket.
	 *  2) Client send telegram to Server. Server responds with different info accord to the telegram received from Client
	 *  3) Client then re-use the stream and socket to send message to Server.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Socket socket = new Socket(InetAddress.getByName(host),50000);
			out = socket.getOutputStream();
			out.write(0x1);		//write 1 or 2 here.
			out.flush();
			in = socket.getInputStream();
			int i = 0;
			i = in.read();
			if(i == 0x61){
				out.write("xxx".getBytes());
				out.flush();
				System.out.println("\"a\" received, send \"xxx\"");
			}
			if(i == 0x62){
				out.write("yyy".getBytes());
				out.flush();
				System.out.println("\"b\" received, send \"yyy\"");
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
