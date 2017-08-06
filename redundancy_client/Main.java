package redundancy_client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Main { 

			
	public  void multiClient(String clientIP, String clientPORT) throws UnknownHostException{
		WebAccess w = new WebAccess(InetAddress.getByName(MultCli.ServerIP),2404, InetAddress.getByName(clientIP),Integer.parseInt(clientPORT));
//		WebAccess w2 = new WebAccess(InetAddress.getByName("172.20.1.60"),2404, InetAddress.getByName("172.20.1.3"),10001);
//		t1 = new Thread(w);
//		t2 = new Thread(w2);
//		t1.start();
//		t2.start();
	}		
}
class WebAccess implements Runnable{
	int dstport;
	int localport;
	InetAddress dst = null;
	InetAddress local = null;
	WebAccess(InetAddress host, int hostport,InetAddress client, int clientport){ //构造函数
		dst = host;	
		local = client;
		dstport = hostport;
		localport = clientport;
	}	
	Socket socket = null;
	//680443000000---680483000000 TESTFR
	//680407000000---68040B000000 STARTDT
	String STARTDT = "680407000000";
	String TESTFR = "680443000000";
	String TESTFRCON = "680483000000";
	String GI = "680E0200000064010600010000000014"; //假设总招的发送序列号为 1
	String supervisory = null;
	String str = null;
	static int nr;	//客户端接收号,收到I帧时加1,TCP重连时清零. 这里要加volatile吗? 
	int ns = 0; //客户端发送号
	int total = 0; 
	boolean startDTCON = false;
	volatile boolean stop = false;	//用来结束计时器
	boolean getSpon = false; //收到突发信息,用来回 S 帧.
	boolean recvTestFR = false; 
	public static Timer timer = null;
//	public static String name = null;
	DataInputStream in = null;
	DataOutputStream out = null;

	
	public void run(){
		try {
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		SendTestFR sendTestFR = new SendTestFR();
//		Thread t = new Thread(sendTestFR);
//		t.start();	//不用启,已经做一个TimerTask,由timer.schedule启.
		timer = new Timer();
		if(startDTCON == true)
		timer.schedule(sendTestFR, 0, /*MultCli.T3 *1000*/5000);
	}
	
	class Listen extends Thread{		
		Listen(String str){
			Thread.currentThread().setName(str);
		}
		Listen(){}
		public void run(){
			int start;
			boolean hasI = false;
				try {
					if((start = in.read()) == 0x68){
						System.out.println("start: "+start);
						int len = in.read();
						byte[] tmp = new byte [len];
						in.readFully(tmp);
						if(len == 4) {
							recvTestFR = true; //收到测试帧. 
							MultCli.updateTextArea(MultCli.threadName+": Recv TESTFR ACT\n");
							System.out.println(MultCli.threadName+": Recv TESTFR ACT");
							sendTestFRCON();
						}
						else{
							nr += 2;								
							hasI = true; //收到I 帧,while()被中断后确认.
						}
					}
				} catch (IOException e) {
					System.out.println("Exception is not printed here, but there's interupption.");
					//e.printStackTrace();
				}
//			}
			if(hasI == true){
				try {
					sendS(socket);
					hasI = false;
				} catch (IOException e) {
					e.printStackTrace();
				} //不是每一个I帧都马上确认,有可能几个在一个packet里.
			}
		}
	}
	
	class SendTestFR extends TimerTask{
		String str1;
		SendTestFR(String abc){
			System.out.println("New SendTestFR Object created ");
			str1 = abc;
		}
		SendTestFR(){} //constractor
		public void run(){	
			Listen listen = new Listen();
			listen.start();
			Timer t1_server = new Timer(true); //初始化后,即S帧确认完总召响应后,启个定时任务,如果这段时间没收到东西,就发Test FR.		
			t1_server.schedule(new TimerTask(){ //没用,这样不能结束DataInputStream.read()的阻塞.加了setSoTimeout()
				public void run(){
//					listen.interrupt();
//					System.out.println("listen interrupt");
				}			
			}, /*MultCli.T1 *1000*/1000); //.		
		}
	}
	/**
	 * Create normal connection by send STARTDT and check response.
	 * @throws IOException 
	 * 假设先发对时,再发总招.
	 * 不检查装置发的S帧里的序号是否正确.
	 */
	private boolean initialize() throws IOException {
		socket = new Socket(dst,dstport,local,localport);
		socket.setSoLinger(true, 1);
		socket.setSoTimeout(2000);
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream()); 
		byte[] boStartDT = HexStrToByte.hexStringToBytes(STARTDT);
		out.write(boStartDT);
        out.flush();
        nr = 0;
        byte[] biStartDT = new byte[6];
        in.read(biStartDT);
        str = ByteToStr.byteToStr(biStartDT);
        if(!(str.substring(5, 6).equals("b") || str.substring(5, 6).equals("B"))){        	
        	startDTCON = false;
        }else{
        	startDTCON = true;
        	byte[] boTS = StrToByte.strToByte(TimeFraming.framing());
        	out.write(boTS);
        	out.flush();
        	ns += 2;
        	try {
				Thread.sleep(800);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
        	//对时响应报文只读报文数量用来计算 nr .        	
        	sendS(nr, socket, in);
        	//这里,确认完对时响应之后到发送GI之前,这段时间会不会有装置的报文上来?有的话一直buffer在输入流内吗?
        	byte[] boGI = StrToByte.strToByte(GI);
        	out.write(boGI);
        	out.flush();
        	System.out.println("GI send");
        	ns += 2;
        	/*try {
				Thread.sleep(600);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}*/
        	//总招响应报文不处理.
//        	sendS(nr, socket, in); //四代,线程睡1000ms 也不保证总召信息传送结束!! 我擦,四代发响应总召,最后不发cot = 0x0a00, 就等t1后报超时关闭socket了.晕
        	while(in.read()==0x68){
        		int len = in.readUnsignedByte();
        		if(len > 4) nr += 2;
        		byte[] tmp = new byte[len];	
        		in.readFully(tmp);
        		String str = ByteToStr.byteToStr(tmp);
        		if (str.substring(12, 16).equals("0a00") || str.substring(12, 16).equals("0A00")) 
        			break;  //一直查到总召结束,这之前如果有自发,也把nr 加2,一块用S 确认了. 因为判据是长度大于4.
        	}
        	/*String NR = Integer.toHexString(nr);
    		if (NR.length() == 1)
    			NR = "0" + NR;
    		String supervisory = "68040100" + NR + "00"; //假设接收接收序列号不大于127,即一个字节少一个bit也能表示.
    		byte[] boSendS = StrToByte.strToByte(supervisory);
    		out.write(boSendS);
            out.flush();*/ 
        	sendS(socket);
        }
		return startDTCON;
	}
	
	
	private void sendS(Socket s) throws IOException{
		supervisory = "68040100";
//		nr = rcvnum;
    	if (nr >= 254){
    		System.out.println("Only one byte for N(s), 254 reached. Pls restart.");
    	}
    	String NR = Integer.toHexString(nr);
		if (NR.length() == 1)
			NR = "0" + NR;
		supervisory = supervisory + NR + "00"; //假设接收接收序列号不大于127,即一个字节少一个bit也能表示.
		out = new DataOutputStream(s.getOutputStream()); 
		byte[] boSendS = StrToByte.strToByte(supervisory);
		out.write(boSendS);
        out.flush();  
	}
	/*
	 * 接收序列号太大的没考虑
	 */
	//68 len nsl nsh nrl nrh ti sq cotl coth addl addh 00 00 00 14
	private void sendS(int rcvnum, Socket s, DataInputStream in) throws IOException{
		supervisory = "68040100";
//		String tele = null;
		total = in.available();
//    	System.out.println("available: "+in.available());
    	int start;
    	int len;
    	while ((start = in.read()) == 0x68){
    		len = in.readUnsignedByte();
//    		System.out.println("in sendS, len is (readUnsignedByte) "+len);
    		byte[] tmp = new byte[len];	//临时数组接收报文,但不处理.
    		in.readFully(tmp);
    		total = total - len - 2;
    		if(len > 4){	//长度大于 1+1+4=6, I帧
    			rcvnum += 2;
    		}
    		/*把自发信息拎出来,每次都发S确认.但好多自发信息在一个packet时怎么办?*/
    		/*答:没关系,本来就是通过0x68而不是通过一个frame来截断的*/
    		/*tele = "68" + Integer.toHexString(len) + ByteToStr.byteToStr(tmp);
    		if (tele.substring(16, 20).equals("0300")){ //cot = 03,自发信息
    			getSpon = true;
    		}*/
//    		System.out.println("available in loop: "+ in.available());
    		if (total == 0) break; //这里应该也可以一进initialize()就起t_ListenSpon, 然后检查cot = 0x0a来结束总召.
    	}
    	nr = rcvnum;
    	if (nr >= 254){
    		System.out.println("Only one byte for N(s), 254 reached. Pls restart.");
    	}
    	String NR = Integer.toHexString(rcvnum);
		if (NR.length() == 1)
			NR = "0" + NR;
		supervisory = supervisory + NR + "00"; //假设接收接收序列号不大于127,即一个字节少一个bit也能表示.
		out = new DataOutputStream(s.getOutputStream()); 
		byte[] boSendS = StrToByte.strToByte(supervisory);
		out.write(boSendS);
        out.flush();          
	}//sendS 发送S帧结束.
	
	private void sendTestFRACT() throws IOException{
		byte[] boTestFR = HexStrToByte.hexStringToBytes(TESTFR);
	    out.write(boTestFR);
        out.flush();        
        MultCli.updateTextArea(MultCli.threadName+": Send TESTFR ACT\n");
        System.out.println(MultCli.threadName+": Send TESTFR ACT");
	}
	private void sendTestFRCON() throws IOException{
		byte[] boTestFRCON = HexStrToByte.hexStringToBytes(TESTFRCON);
	    out.write(boTestFRCON);
        out.flush();
        MultCli.updateTextArea(MultCli.threadName+": Send TESTFR CON\n");
        System.out.println(MultCli.threadName+": Send TESTFR CON");
	}
	
	/*class ListenSpon implements Runnable{ //想用来监听突发信息,做不到.
		DataInputStream in = null;
		int start;
    	int len;
    	String str = null;
		ListenSpon(DataInputStream dis){
			in = dis;
			System.out.println("new thread runs");
		}
		public void run(){
			try {
				//68 len nsl nsh nrl nrh ti sq cotl coth addl addh 00 00 00 14
				while ((start = in.read()) == 0x68){
					len = in.read();
					byte[] tmp = new byte[len];
					System.out.println("in listenspon, len is "+len);
					in.readFully(tmp);
					str = ByteToStr.byteToStr(tmp);
					if(str.length() > 16){
						if(str.subSequence(12, 16).equals("0300")){
							getSpon = true;
							nr += 2;
//							break; //只要读到第一个突发事件,就跳出.
						}
					}									
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
}
/*5.16.2017 之前想初始化后单起一个线程来监听突发事件, 但好像实现不了. 起的线程如果用别的socket,相当于一发两收,乱了. 如果再建一个socket, 要实现一整套发,收操作.
 * 			新想法, 在SendTestFR 类的初始化中加形参,把DataInputStream in 传进来,判断输入流里有没有突发事件(不传进来也能直接用实例变量).
 *5.17.2017 线程名一直为Thread 17..之类递增的数字,想让线程名显示为Client I,II,III,在创建实例时各种用Thread.currentThread().setName(arg0)传进去,.getName()获得,都不行
 *			最后在MultCli中启线程时把t1.getName()读到一个字符串,再取出来,搞定.
 */
