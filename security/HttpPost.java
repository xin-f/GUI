package security;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HttpPost {
	static BufferedReader in = null;
//	static File file = new File("d:\\IEC61850_81.pck");
	static File file = null;
//	static String fileName = "IEC61850_81.pck";
	static String fileName = null;
	static String password = null;
	static int suc;
	private static int timeout;
	public static Timer t = null; //Make the timer be public so that the uploading process can be terminated by 'Reset' in Frame.java by t.cancel()
	
//	public  static void main(String[] s) {
	public  static void upldFW() {	
		String str = Frame.fw;
		file = new File(str);
		int i;
		while(( i = str.indexOf("\\")) > -1){
			str= str.substring(i+1, str.length());
		}
		fileName = str;
		password = Frame.fwpw;
		
		t = new Timer();
		timeout = Frame.period*1000;
		t.schedule(new TimerTask(){
			public void run(){
				try {
					URL url = new URL(Frame.ip);
					URLConnection connection =  url.openConnection();
					String boundary = "---------------------wowothisisunique-sxf";
					String newLine = "\r\n";
					//头部的\r\n都是系统自己加的.
					connection.setRequestProperty("User-Agent", "sxf");
					connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
					connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
					connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary); 
					//Content-Type 里指明了数据是以 multipart/form-data 来编码，本次请求的 boundary 是什么内容。消息主体里按照字段个数又分为多个结构类似的部分，每部分都是以 --boundary 开始，
					//紧接着是内容描述信息，然后是回车，最后是字段具体内容（文本或二进制）。有它就有分界符,没它就不需要分界符.
					//如果传输的是文件，还要包含文件名和文件类型信息。消息主体最后以 --boundary-- 标示结束。
//					connection.setRequestMethod("POST");
//					connection.setRequestProperty("userfile2", fileName);
//					connection.setConnectTimeout(2000); //timeout
//					connection.setReadTimeout(2000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
//					connection.setConnectTimeout(2000);
//					connection.connect();

					BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
					StringBuffer sb_pw = new StringBuffer(); //写边界符,约定上传属性,格式application/octet-stream
					sb_pw.append(boundary)
					.append(newLine)
					.append("Content-Disposition: form-data; name=\"curr_password\"")
					.append(newLine)
					.append(newLine)
					.append(password)
					.append(newLine)
					.append(boundary)
					.append(newLine);
					out.write(sb_pw.toString().getBytes());
					out.flush(); //flush()很关键.
					StringBuffer sb_fw = new StringBuffer();;
					sb_fw.append("Content-Disposition: form-data; name=\"userfile2\";filename="+"\""+fileName+"\"") //注意格式,边看wireshark边凑.
						.append(newLine)
						.append("Content-Type: application/octet-stream")
						.append(newLine)
						.append(newLine);
					
					FileInputStream fis = new FileInputStream(file);
					byte[] bytes = new byte[1024];
					int numReadByte = 0;
					out.write(sb_fw.toString().getBytes());
					out.flush(); //flush()很关键.
					while((numReadByte = fis.read(bytes, 0, 1024))>0){	//写数据内容,即.pck
						out.write(bytes, 0, numReadByte);
						//不带flush(),都是624请求FIN. 然后都会报超时,
						out.flush();
//						System.out.println(new String(bytes));
					}
					StringBuffer sb_end = new StringBuffer();
					sb_end.append(newLine)
					.append(boundary)
					.append(newLine);
					out.write(sb_end.toString().getBytes());
					out.flush();

				/*用浏览器上传时的格式:
				MIME Multipart Media Encapsulation, Type: multipart/form-data, Boundary: "---------------------------177882047830227"
				    [Type: multipart/form-data]
				    First boundary: -----------------------------177882047830227\r\n
				    Encapsulated multipart part: 
				        Content-Disposition: form-data; name="curr_password"\r\n\r\n
				        Data (3 bytes)
				            Data: 787878
				            [Length: 3]
				    Boundary: \r\n-----------------------------177882047830227\r\n
				    Encapsulated multipart part:  (application/octet-stream)
				        Content-Disposition: form-data; name="userfile2"; filename="IEC61850_81.pck"\r\n
				        Content-Type: application/octet-stream\r\n\r\n
				        Data (1532784 bytes)
				    Last boundary: \r\n-----------------------------177882047830227--\r\n
				 */

					fis.close();
					out.close();
					String str;	
					in = new BufferedReader(new InputStreamReader(connection.getInputStream()));					
					while((str = in.readLine()) != null){
						if(str.indexOf("Update in progress") > -1){
							suc += 1;
							System.out.println("FW upload successful "+suc+" times.");
							Frame.updateTextAreacnt(suc);
//							String str1 = "FW upload successful "+suc+" time.\n";
//							String str2 = "FW upload successful "+suc+" times.\n";
//							str = (suc == 1)?"":"";
							Frame.updateTextArea((suc==1)?"FW upload successful "+suc+" time.\n":"FW upload successful "+suc+" times.\n");
						}
						if(str.indexOf("The password is incorrect!") > -1) {
							System.out.println("FW upload failed. Pw is not correct.");
							Frame.updateTextArea("FW upload failed. Pw is not correct.\n");
						}
					}
//					System.out.println(connection.getHeaderField(0));  //HTTP/1.0 200 OK
					in.close();
					
				
				} catch (MalformedURLException e) {
					//new URL()
					e.printStackTrace();
				} catch (IOException e) {
					Frame.updateTextArea("Connection not established.\nIf the IP is correct, try again or check it via browser.\n ");
					e.printStackTrace();
				}
			}
		}, 0, timeout);				 
   }  
}
/*
成功时的log:
+++ 00001 00298355 Mo 26.06.2017 15:18:18:306 start firmware upload from client 172.020.001.001:52067
+++ 00002 00298357 Mo 26.06.2017 15:18:18:308 HTTP: Start Upload max. 3194880 Bytes
+++ 00003 00298653 Mo 26.06.2017 15:18:18:605 HTTP: file "IEC61850_81.pck"
+++ 00004 00301373 Mo 26.06.2017 15:18:21:325 Received "APPL" EN100 firmware update (1529328 bytes) from 172.020.001.001:52067 ( ).
+++ 00005 00302091 Mo 26.06.2017 15:18:22:042 HTTP: Code flashed len = 1529332
+++ 00006 00327833 Mo 26.06.2017 15:18:47:786 HTTP: Code flash OK.

超时时的log: no flush().
+++ 00007 00429859 Mo 26.06.2017 15:20:29:814 start firmware upload from client 172.020.001.001:52070
+++ 00008 00429859 Mo 26.06.2017 15:20:29:814 HTTP: Start Upload max. 3194880 Bytes
+++ 00009 00430149 Mo 26.06.2017 15:20:30:104 HTTP: file "IEC61850_81.pck"
+++ 00010 00437056 Mo 26.06.2017 15:20:37:011 upload busy for client 172.020.001.001:52070
+++ 00011 00443089 Mo 26.06.2017 15:20:43:044 upload time exceeded
end of system log
*/
