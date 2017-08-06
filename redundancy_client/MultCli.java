package redundancy_client;

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class MultCli {

	private static JFrame frame;
	private JTextField serverIP;
	private JTextField serverPORT;
	private JTextField client1IP;
	private JTextField client1PORT;
	private JTextField client2IP;
	private JTextField client2PORT;
	private JTextField client3IP;
	private JTextField client3PORT;
	private JTextField client4IP;
	private JTextField client4PORT;
	private JTextField client5IP;
	private JTextField client5PORT;
	private JTextField client6IP;
	private JTextField client6PORT;
	private static JTextArea teleArea;
	public static MultCli window;
	public static int sendNr = 0;
	public static int T1 = 0;
	public static int T3 = 0;
	public static int clientNum = 0;

	public static String ServerIP;
	public static String C1IP;
	public static String C1PORT;
	public static String C2IP;
	public static String C2PORT;
	public static String C3IP;
	public static String C3PORT;
	public static String C4IP;
	public static String C4PORT;
	public static String threadName;

	static Thread t1 = null;
	static Thread t2 = null;
	static Thread t3 = null;
	static Thread t4 = null;

	public WebAccess w = null;
	private static JTextField textFieldt3;
	private static JTextField textFieldt1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MultCli(); // 生成类实例,运行类的构造函数,即后面只有一行代码initialize();的函数.
					buildTextArea();

					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MultCli() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 401, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Server");
		lblNewLabel.setBounds(10, 11, 41, 20);
		frame.getContentPane().add(lblNewLabel);

		serverIP = new JTextField();
		serverIP.setText("172.20.1.60");
		serverIP.setBounds(54, 11, 86, 20);
		frame.getContentPane().add(serverIP);
		serverIP.setColumns(10);

		serverPORT = new JTextField();
		serverPORT.setText("2404");
		serverPORT.setBounds(150, 11, 33, 20);
		frame.getContentPane().add(serverPORT);
		serverPORT.setColumns(10);
		
		
		JLabel lblClient = new JLabel("C 1");
		lblClient.setBounds(10, 33, 41, 20);
		frame.getContentPane().add(lblClient);

		client1IP = new JTextField();
		client1IP.setText("172.20.1.1");
		client1IP.setColumns(10);
		client1IP.setBounds(54, 33, 86, 20);
		frame.getContentPane().add(client1IP);

		client1PORT = new JTextField();
		client1PORT.setText("50000");
		client1PORT.setColumns(10);
		client1PORT.setBounds(150, 33, 40, 20);
		frame.getContentPane().add(client1PORT);

		JLabel lblClient_1 = new JLabel("C 2");
		lblClient_1.setBounds(10, 59, 41, 20);
		frame.getContentPane().add(lblClient_1);

		client2IP = new JTextField();
		client2IP.setText("172.20.1.3");
		client2IP.setColumns(10);
		client2IP.setBounds(54, 59, 86, 20);
		frame.getContentPane().add(client2IP);

		client2PORT = new JTextField();
		client2PORT.setText("50001");
		client2PORT.setColumns(10);
		client2PORT.setBounds(150, 59, 40, 20);
		frame.getContentPane().add(client2PORT);

		/*
		 * JLabel lblClient_2 = new JLabel("C 3"); lblClient_2.setBounds(10, 81,
		 * 41, 20); frame.getContentPane().add(lblClient_2);
		 * 
		 * client3IP = new JTextField(); client3IP.setText("172.20.1.60");
		 * client3IP.setColumns(10); client3IP.setBounds(54, 81, 86, 20);
		 * frame.getContentPane().add(client3IP);
		 * 
		 * client3PORT = new JTextField(); client3PORT.setText("2404");
		 * client3PORT.setColumns(10); client3PORT.setBounds(150, 81, 40, 20);
		 * frame.getContentPane().add(client3PORT);
		 * 
		 * JLabel lblClient_3 = new JLabel("C 4"); lblClient_3.setBounds(10,
		 * 102, 41, 20); frame.getContentPane().add(lblClient_3);
		 * 
		 * client4IP = new JTextField(); client4IP.setText("172.20.1.60");
		 * client4IP.setColumns(10); client4IP.setBounds(54, 102, 86, 20);
		 * frame.getContentPane().add(client4IP);
		 * 
		 * client4PORT = new JTextField(); client4PORT.setText("2404");
		 * client4PORT.setColumns(10); client4PORT.setBounds(150, 102, 40, 20);
		 * frame.getContentPane().add(client4PORT);
		 * 
		 * JLabel lblClient_4 = new JLabel("C 5"); lblClient_4.setBounds(10,
		 * 124, 41, 20); frame.getContentPane().add(lblClient_4);
		 * 
		 * client5IP = new JTextField(); client5IP.setText("172.20.1.60");
		 * client5IP.setColumns(10); client5IP.setBounds(54, 124, 86, 20);
		 * frame.getContentPane().add(client5IP);
		 * 
		 * client5PORT = new JTextField(); client5PORT.setText("2404");
		 * client5PORT.setColumns(10); client5PORT.setBounds(150, 124, 40, 20);
		 * frame.getContentPane().add(client5PORT);
		 * 
		 * JLabel lblClient_5 = new JLabel("C 6"); lblClient_5.setBounds(10,
		 * 146, 41, 20); frame.getContentPane().add(lblClient_5);
		 * 
		 * client6IP = new JTextField(); client6IP.setText("172.20.1.60");
		 * client6IP.setColumns(10); client6IP.setBounds(54, 146, 86, 20);
		 * frame.getContentPane().add(client6IP);
		 * 
		 * client6PORT = new JTextField(); client6PORT.setText("2404");
		 * client6PORT.setColumns(10); client6PORT.setBounds(150, 146, 40, 20);
		 * frame.getContentPane().add(client6PORT);
		 */

		JButton btnStart1 = new JButton("Start");
		btnStart1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				C1IP = window.client1IP.getText();
				C1PORT = window.client1PORT.getText();
				ServerIP = window.serverIP.getText();
				// WebAccess.name = "Client 1";
				T1 = Integer.parseInt(window.textFieldt1.getText());
				T3 = Integer.parseInt(window.textFieldt3.getText());
				try {
					w = new WebAccess(InetAddress.getByName(ServerIP), Integer.parseInt(window.serverPORT.getText()),
							InetAddress.getByName(C1IP), Integer.parseInt(C1PORT));
					if (t1 == null)
						t1 = new Thread(w, "Client I");
					t1.start();
					threadName = t1.getName();
					updateTextArea("I'm working...\n");
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		});
		btnStart1.setBounds(193, 32, 68, 23);
		frame.getContentPane().add(btnStart1);

		JButton btnStop1 = new JButton("Stop");
		btnStop1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (w != null) {
					System.out.println("w is not null");
					w.timer.cancel();
				}
				try {
					w.socket.close();
					System.out.println("socket close");
				} catch (IOException e) {
					e.printStackTrace();
				}
				t1 = null; // 没用布尔变量stop, 而是直接让线程为null, 行吗?
			}
		});
		btnStop1.setBounds(260, 32, 68, 23);
		frame.getContentPane().add(btnStop1);

		JButton btnStart2 = new JButton("Start");
		btnStart2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				C2IP = window.client2IP.getText();
				C2PORT = window.client2PORT.getText();
				ServerIP = window.serverIP.getText();
				// WebAccess.name = "Client 2";
				T1 = Integer.parseInt(window.textFieldt1.getText());
				T3 = Integer.parseInt(window.textFieldt3.getText());
				try {
					w = new WebAccess(InetAddress.getByName(ServerIP), Integer.parseInt(window.serverPORT.getText()),
							InetAddress.getByName(C2IP), Integer.parseInt(C2PORT));
					if (t2 == null)
						t2 = new Thread(w, "Client II");
					t2.start();
					threadName = t2.getName();
					updateTextArea("I'm working...\n");
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IllegalThreadStateException e) {
					e.printStackTrace();
					MultCli.updateTextArea("Pls wait for some time then restart this client\n");
				}
			}
		});
		btnStart2.setBounds(193, 56, 68, 23);
		frame.getContentPane().add(btnStart2);

		JButton btnStop2 = new JButton("Stop");
		btnStop2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (w != null) {
					System.out.println("w is not null");
					w.timer.cancel();
				}
				try {
					w.socket.close();
					System.out.println("socket close");
				} catch (IOException e) {
					e.printStackTrace();
				}
				t1 = null;
			}
		});
		btnStop2.setBounds(260, 56, 68, 23);
		frame.getContentPane().add(btnStop2);
		

		/*
		 * JButton btnStart3 = new JButton("Start"); btnStart3.setBounds(193,
		 * 78, 68, 23); frame.getContentPane().add(btnStart3);
		 * 
		 * JButton btnStop3 = new JButton("Stop"); btnStop3.setBounds(260, 78,
		 * 68, 23); frame.getContentPane().add(btnStop3);
		 * 
		 * JButton btnStart4 = new JButton("Start"); btnStart4.setBounds(193,
		 * 99, 68, 23); frame.getContentPane().add(btnStart4);
		 * 
		 * JButton btnStop4 = new JButton("Stop"); btnStop4.setBounds(260, 99,
		 * 68, 23); frame.getContentPane().add(btnStop4);
		 * 
		 * JButton button_6 = new JButton("Start"); button_6.setBounds(193, 121,
		 * 68, 23); frame.getContentPane().add(button_6);
		 * 
		 * JButton button_7 = new JButton("Stop"); button_7.setBounds(260, 121,
		 * 68, 23); frame.getContentPane().add(button_7);
		 * 
		 * JButton button_8 = new JButton("Start"); button_8.setBounds(193, 143,
		 * 68, 23); frame.getContentPane().add(button_8);
		 * 
		 * JButton button_9 = new JButton("Stop"); button_9.setBounds(260, 143,
		 * 68, 23); frame.getContentPane().add(button_9);
		 */

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				teleArea.setText("");
			}
		});
		btnClear.setBounds(260, 10, 68, 23);
		frame.getContentPane().add(btnClear);

		JLabel lblT = new JLabel("t1");
		lblT.setBounds(193, 14, 14, 14);
		frame.getContentPane().add(lblT);

		textFieldt1 = new JTextField();
		textFieldt1.setText("15");
		textFieldt1.setColumns(10);
		textFieldt1.setBounds(206, 11, 20, 20);
		frame.getContentPane().add(textFieldt1);

		textFieldt3 = new JTextField();
		textFieldt3.setText("20");
		textFieldt3.setColumns(10);
		textFieldt3.setBounds(243, 11, 20, 20);
		frame.getContentPane().add(textFieldt3);

		JLabel lblt3 = new JLabel("t3");
		lblt3.setBounds(230, 14, 14, 14);
		frame.getContentPane().add(lblt3);

		// 建JTextArea的代码要改造就下后挪到buildTextArea()里去,如果这里保留,会有两个area叠加,乱了.如果在这里buildTextArea(),
		// 看下面说明.
		/*
		 * teleArea = new JTextArea(); teleArea.setBounds(10, 176, 313, 149);
		 * frame.getContentPane().add(teleArea); teleArea.setColumns(10);
		 */
	}

	/**
	 * 创建TextArea.
	 * 之所以单独用一个方法创建TextArea而不是在下面updateTextArea()方法中在更新前创建,是因为如果更新和创建在
	 * 同一个方法里,每次更新时,TextArea都被重新创建,原来显示的信息都没有, 看上去就跟没更新一样.
	 */
	public static void buildTextArea() {
		teleArea = new JTextArea();
		JScrollPane js = new JScrollPane(teleArea);// 要把JTextArea加到JScrollPane里面,才会有滚动条出来.JScrollPane的尺寸与位置抄前面JTextArea自动生成的
		js.setBounds(10, 176, 313, 149);
		frame.getContentPane().add(js);
		

		DefaultCaret caret = (DefaultCaret) teleArea.getCaret(); // 这两行让文本框的滚动条自动在最下面.
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// /不知道为什么不能加这两行??
		// js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js.setVisible(true);
	}

	/**
	 * 更新TextArea,显示报文内容及解析. 单独创建这个公用方法而不是在前面initialize()里创建,是为了让别的类调用它.有其它方法吗?
	 */
	public static void updateTextArea(String str) {
		teleArea.append(str);
	}
}
