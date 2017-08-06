package miscelleaneous;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class PairSocketServer {
	static InputStream in;
	static OutputStream out;
	static ServerSocket server;
	static Socket client;

	public static void main(String[] args) {
		
		try {
			server = new ServerSocket(50000,2,InetAddress.getByName("172.20.1.1"));			
			client = server.accept();
			in = client.getInputStream();
			out = client.getOutputStream();
			int i = 0;
			i = in.read();
			while(true){
				if(i == 0x1){
					out.write("a".getBytes());				
					out.flush();
					System.out.println("0x1 received, send \"a\"");
					break;
				}					
				if(i == 0x2){
					out.write("b".getBytes());
					out.flush();
					System.out.println("0x2 received, send \"b\"");
					break;
				}					
			}			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				server.close();
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
