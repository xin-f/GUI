package T104TimeSync;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

/**
  * 想用MouseListener监听鼠标,没做成.....
  */
class MyMouse extends JFrame implements MouseListener {	//监听鼠标时最好用java.awt.event.MouseAdapter, 可只重写要用的方法.
														//MouseListener须重写所有成员函数,如本例,即使没用到,也要有个空函数体.
	 
	MyMouse(){
	System.out.println("mouse not cli");}
	 public void mouseClicked(MouseEvent e)    //点击,后面还可继续检查是左右中键  
     {
		 System.out.println("mouse clicked");
     }
	 public void mouseEntered(MouseEvent e)   //鼠标进入组件  
     {}  
      
     public void mouseExited(MouseEvent e)     //鼠标离开组件  
     {}  
      
     public void mousePressed(MouseEvent e)   //鼠标被按下  
     {}  
      
     public void mouseReleased(MouseEvent e)   //鼠标被放开  
     {}  
}

public class VisibleFrame {
	public static int port;
	public static int comaddr;
	public static String host = null;
	public static VisibleFrame window;
	public static Object[] TX_TIME_SELECTED;
	public static String SetTime;
	public static Timer timer = new Timer();
	public static String displayedTime;
	public static boolean chkbx_SetTime = false;
	private static JFrame frame;
	private JTextField TX_IP;
	private JTextField TX_PORT;
	private JTextField TX_TIME;
	private static JTextArea teleArea;
	private static JButton btnClear;
	private JTextField TX_COMADDR;//JTextField 类型的变量默认是static, 要去掉static才能被非static型的函数initialize()访问
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new VisibleFrame();	//生成类实例,运行类的构造函数,即后面只有一行代码initialize();的函数.
					buildTextArea();
			//		updateTextArea();
					VisibleFrame.frame.setVisible(true);
					timerUpdating(timer);
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VisibleFrame() {		
		initialize();
	}
	
	/**
	 * convert the string value TX_PORT to int
	 */
	public static int convertStringtoint(String str){
		return Integer.parseInt(str);		
	}
	
	/**
	 * A timer to update the TIME displayed cyclically.
	 */
	public static void timerUpdating(Timer t){
		t.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				TimeHandling.getTime();
				window.TX_TIME.setText(TimeHandling.CUR_TIME);
			}
		}, 100, 1000);
	//	System.out.println("timer updating");
	}
	
	/**
	 * 停止textField里的时间更新,以便手动修改时间.
	 */
	public static void timeCancel(Timer t) {  
        t.cancel(); 
   //   System.out.println("timer canneled");
    }  
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("T104 Time Sync");
		frame.setBounds(100, 100, 480, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblIp = new JLabel("IP");
		lblIp.setBounds(10, 11, 28, 14);
		frame.getContentPane().add(lblIp);
		
		TX_IP = new JTextField();
		TX_IP.setText("172.20.1.60");
		TX_IP.setColumns(10);
		TX_IP.setBounds(10, 27, 97, 21);
		frame.getContentPane().add(TX_IP);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(112, 11, 28, 14);
		frame.getContentPane().add(lblPort);
		
		TX_PORT = new JTextField();
		TX_PORT.setText("2404");
		TX_PORT.setColumns(10);
		TX_PORT.setBounds(112, 27, 34, 21);
		frame.getContentPane().add(TX_PORT);
		
		JLabel lblComadd = new JLabel("ComAd");
		lblComadd.setBounds(150, 11, 40, 14);
		frame.getContentPane().add(lblComadd);	
		
		TX_COMADDR = new JTextField();
		TX_COMADDR.setText("1");
		TX_COMADDR.setColumns(10);
		TX_COMADDR.setBounds(148, 27, 42, 21);
		frame.getContentPane().add(TX_COMADDR);		
		
		TX_TIME = new JTextField();
		TX_TIME.setBounds(10, 51, 136, 21);
		frame.getContentPane().add(TX_TIME);
		TX_TIME.setColumns(10);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				VisibleFrame.teleArea.setText("");
			}
		});
		btnClear.setBounds(192, 50, 69, 23);
		frame.getContentPane().add(btnClear);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				displayedTime = window.TX_TIME.getText();	//取JTextField 显示的时间, 在SetTime复选框选中时用做时钟源对装置对时.
				port = convertStringtoint(window.TX_PORT.getText());
				comaddr = convertStringtoint(window.TX_COMADDR.getText());
				host = window.TX_IP.getText();
				Main.timeSync();
			
			
				
			}
		});
		btnSend.setBounds(192, 23, 69, 23);
		frame.getContentPane().add(btnSend);
		
		/**
		 * introduce this CheckBox to 'froze' the time cyclic updating so that the Time in TextField can be changed then used as time source	 
		 */
		JCheckBox chkbxSetTime = new JCheckBox("Set Time");
		chkbxSetTime.setBounds(10, 78, 82, 21);
		frame.getContentPane().add(chkbxSetTime);
		
		final JFrame j=new JFrame();
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		JMenuItem About = new JMenuItem("About"); 
		mnHelp.add(About);	//要手动调用add()方法把MenuItem绑到Menu上?
		
		ActionListener al =new ActionListener(){
			   public void actionPerformed(ActionEvent e) {
			       String buttonString=e.getActionCommand();	
				if(buttonString.equals("About"))
				JOptionPane.showMessageDialog(j, 	"This is an immature but maybe helpful widget for T104\n"
						+ 		"time synchronization test. Your suggestion is warmly\n"
						+ 		"welcome although maybe it will not be treated.");
				}
			};
		About.addActionListener(al);
		j.setBounds(180, 300, 300, 100);	
		j.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
//		j.setVisible(true);
		
		chkbxSetTime.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){				
		         TX_TIME_SELECTED = chkbxSetTime.getSelectedObjects();
		         SetTime = (TX_TIME_SELECTED == null)?"Check box is not selected":TX_TIME_SELECTED[0].toString();
		//         System.out.println(SetTime);
		         if(!(SetTime.equals("Set Time"))){
						timerUpdating(timer);
						chkbx_SetTime = false;
				//		System.out.println(chkbx_SetTime);
					}
		         else{
		        	 timeCancel(timer);
		        	 timer = new Timer();	//坑啊!!已经被cancel掉的Timer不能再重新启动, 必须要重新new 一个.
		        	 chkbx_SetTime = true;
		      //  	 System.out.println(chkbx_SetTime);
					}
		    }
		});	//监听复选框结束.				
	}
	
	/**
	 * 创建TextArea. 之所以单独用一个方法创建TextArea而不是在下面updateTextArea()方法中在更新前创建,是因为如果更新和创建在
	 * 同一个方法里,每次更新时,TextArea都被重新创建,原来显示的信息都没有, 看上去就跟没更新一样.
	 */
	public static void buildTextArea() { 
		teleArea = new JTextArea();
	//	teleArea.setBounds(10, 126, 414, 126);
	//	frame.getContentPane().add(teleArea);
		JScrollPane js=new JScrollPane(teleArea);//要把JTextArea加到JScrollPane里面,才会有滚动条出来.JScrollPane的尺寸与位置抄前面JTextArea自动生成的
		js.setBounds(15, 126, 435, 270);
		frame.getContentPane().add(js);
	//	js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); /不知道为什么不能加这两行??
	//	js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js.setVisible(true);
		
    }  

	/**
	 * 更新TextArea,显示报文内容及解析. 单独创建这个公用方法而不是在前面initialize()里创建,是为了让别的类调用它.有其它方法吗?
	 */
	public static void updateTextArea(String str) { 
		teleArea.append(str);
    }  
}




