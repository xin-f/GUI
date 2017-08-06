package security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;

public class SetPassword {
	static BufferedReader in = null;
	static int succ = 0;
	private static int timeout;
	private static String curpw;
	private static String newpw;
	private static String pwNotSet = "Confirm password:";//只有密码没设，网页里才有此字符串
	private static String pwSet = "The password has been set or changed";
	private static String boundary = null;

	private static URL url = null;
	private static URLConnection connection = null;
	
	public static File file = null;
//	private static FileOutputStream fos = null;
	public static BufferedWriter bw = null; //保存尝试过的密码。要在点Reset后写入，所以要public.
	private static Formatter fmt = null;
	public static boolean streamClosed;
	public static boolean end;

	static PrintWriter out = null;
	private static boolean setPwSucc;
	private static boolean pwExist;		//密码已设置
	private static boolean pwInitInternal;	//密码是否由本程序初始化
	private static boolean pwInitOut_BeChangedAtLeastOnce;
	public static Timer t = null;//Make the timer be public so that the pw setting process can be terminated by 'Reset' in Frame.java by t.cancel()

//	public static void main(String[] s) {
	public static void set() {
		pwInitInternal = false; // 默认假设密码不是由本程序初始化的.->用于判断在改密码时,旧密码的来源.
								// 最外层if-else-语句,else部分要衔接一个来自if语句的变量 newpw
		pwInitOut_BeChangedAtLeastOnce = false; // 默认密码已设,且由外部设置.第一次修改密码时, 从外部输入一个字符串作为原密码,修改成功把此变量置true,
						// 不再接受外部字符串. 即用来区别修改密码时第一次和后面其它次老密码的来源.		
		if(Frame.savePw) {
			streamClosed = false;
			try {
//				System.out.println("create new bw");
				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		timeout = Frame.period*1000;
		t = new Timer();
		SetPasswordCycically setPasswordCycically = new SetPasswordCycically();
		t.schedule(setPasswordCycically, 0, timeout);
	}

	/**
	 * 由于EN100是HTTP短连接,设密码成功后若不再次请求setpassword,EN100不能正确上送密码已设置的状态.
	 */
	static class SetPasswordCycically extends TimerTask {
		
		String str = null;
		String result = "";
		

		public void run() {
			if (!Frame.stop) {
				end = false;
				try {
					url = new URL(Frame.ip);
					setPwSucc = false;
					pwExist = true; // 默认已经设过密码

					HttpURLConnection conn = setConnect((HttpURLConnection) url.openConnection());
					boundary = "---------------------wowothisisunique-sxf";

					// 加入有没有密码的判断
					in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					while ((str = in.readLine()) != null) {
						if (str.indexOf(pwNotSet) > -1) { // 一检测到相关字符串,就认为密码没设过.
							pwExist = false;
							break;
						}
					}
					conn.disconnect(); // HttpURLConnection只能先写后读,且断开后不能复用.
										// 此处取到标志位,断开conn, 后面另起一个connection.

					HttpURLConnection connection = setConnect((HttpURLConnection) url.openConnection());
					connection.setDoInput(true);
					connection.setDoOutput(true);
					if (!pwExist) {
						// System.out.println("in if");
						StringBuffer set = new StringBuffer();
						// newpw = Random.getRandomString();
						newpw = Frame.commonpw; // 第一次设密码，用输入框里的。
						set.append(boundary + "\r\n")
								.append("Content-Disposition: form-data; name=\"init_password\"\r\n\r\n").append(newpw)
								.append("\r\n" + boundary + "--\r\n")
								.append("Content-Disposition: form-data; name=\"con_password\"\r\n\r\n").append(newpw)
								.append("\r\n" + boundary + "--\r\n");
						out = new PrintWriter(connection.getOutputStream());
						out.write(set.toString());
						out.flush();
						in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
						while ((str = in.readLine()) != null) {
							result += (str + "\n");
							if (str.indexOf(pwSet) != -1) {
								succ += 1;
								setPwSucc = true;
								pwInitInternal = true;
								Frame.updateTextArea("Password has been initialized.\n");
							}
						}
					}

					else {
						if (pwInitInternal)
							curpw = newpw;
						else {
							if (!pwInitOut_BeChangedAtLeastOnce) {
								curpw = Frame.commonpw; //从fwpw-->commonpw
							} else
								curpw = newpw;
						}
						StringBuffer change = new StringBuffer();
						if (Frame.negative)
							newpw = Random.getRandomString();
						else
							newpw = Random.getValidRandomString();
						change.append(boundary + "\r\n")
								.append("Content-Disposition: form-data; name=\"curr_password\"\r\n\r\n").append(curpw)
								.append("\r\n" + boundary + "--\r\n")
								.append("Content-Disposition: form-data; name=\"init_password\"\r\n\r\n").append(newpw)
								.append("\r\n" + boundary + "--\r\n")
								.append("Content-Disposition: form-data; name=\"con_password\"\r\n\r\n").append(newpw)

								.append("\r\n" + boundary + "--\r\n");

						out = new PrintWriter(connection.getOutputStream());
						// PrintWriter out_save = new PrintWriter(new
						// FileWriter(file));
						// BufferedWriter bw = new BufferedWriter( new
						// FileWriter(file)); //文件类最好用PrintWriter.
						out.write(change.toString());
						out.flush();

						in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
						while ((str = in.readLine()) != null) {
							result += (str + "\n");
							if (str.indexOf(pwSet) != -1) {
								// System.out.println(str);
								succ += 1;
								setPwSucc = true;
								pwInitOut_BeChangedAtLeastOnce = true;
							}
						}
					}
					/*
					 * out_save.write(result); out_save.flush(); out_save.close();
					 */
					System.out.println("Password has been set successfully for " + succ + " times.");
					Frame.updateTextAreacnt(succ);
					//e.g.: abc123!@	Invalid		fail
					if(Frame.savePw) {
//						bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("logs.txt"))));
						fmt = new Formatter();
						fmt.format("%-26s", newpw);
						bw.write(fmt.toString()); //手动停止时，点了Reset，输出流即关闭，然而已经由定时器起到的当前这一次操作（即最后一次），要用到这个输出流，
								//故可能会出现NPE。 
					}
					if (!setPwSucc) {
						if(!Frame.negative) {
							//密码操作失败，且密码都是合规的。
							System.out.println("Set password failed. Process terminated. Is the current pw correct?");
							Frame.updateTextArea("Set password failed. Process terminated.\nIs the current pw correct?\n");						
							t.cancel();
							if(Frame.savePw) {
								bw.write("Valid    "+"FAIL");
								bw.newLine();
								bw.flush();
								bw.close(); //有t.cancel()的地方需要close()
								streamClosed = true;
							}
							if(Frame.fwpwRunning) Frame.fwpwRunning = false;
							if(Frame.digpwRunning) Frame.digpwRunning = false;
						}else {
						    //密码操作失败，此时的密码字符串未必是合规的，要作判断。
							if(Random.judge(newpw)) {
								System.out.println("Set password failed. Process terminated. Is the current pw correct?");
								Frame.updateTextArea("Set password failed. Process terminated.\nIs the current pw correct?\n");	
//								t.cancel();
								if(Frame.savePw) {
									bw.write("Valid    "+"FAIL");
									bw.newLine();
									bw.flush();
//									bw.close(); //有t.cancel()的地方需要close()
//									streamClosed = true;
								}
//								if(Frame.fwpwRunning) Frame.fwpwRunning = false;
//								if(Frame.digpwRunning) Frame.digpwRunning = false;
							}else {
								System.out.println("Invalid password is rejected by EN100.");
								Frame.updateTextArea("Invalid password is rejected by EN100.\n");
								if(Frame.savePw) {
									bw.write("Invld    "+"PASS");
									bw.newLine();
									bw.flush();
								}
							}
						}
						
					}else {//密码操作成功						
						if(Frame.negative) {
							//密码操作成功，密码字串可能是有效也可能是无效的。
							if(!Random.judge(newpw)) {
								//密码操作成功但密码不合规
								System.out.println("Invalid password is accepted by EN100!!!!");
								Frame.updateTextArea("Invalid password is accepted by EN100!!!!\n");	
//								t.cancel();
								if(Frame.savePw) {
									bw.newLine();
									bw.flush();
//									bw.close(); //有t.cancel()的地方需要close()
//									streamClosed = true;
								}
//								if(Frame.fwpwRunning) Frame.fwpwRunning = false;
//								if(Frame.digpwRunning) Frame.digpwRunning = false;
							}else {
								//密码操作成功,密码合规
								if(Frame.savePw) {
									bw.newLine();
									bw.flush();
								}
							}
						}else {
							//密码操作成功，密码串都是有效的。
							if(Frame.savePw) {
								bw.newLine();
								bw.flush();
							}
						}
					}
					in.close();
					out.close();
					end = true;
				} catch (MalformedURLException e) {
					// new URL()
					e.printStackTrace();
				} catch (IOException e) {
					Frame.updateTextArea(
							"Connection not established.\nIf the IP is correct, try again or check it via browser.\n ");
					e.printStackTrace();
				}
			} else
				t.cancel();
		}

		private HttpURLConnection setConnect(HttpURLConnection c) {
			c.setRequestProperty("User-Agent", "sxf");
			c.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			c.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			c.setRequestProperty("Accept-Encoding", "gzip, deflate");
			c.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			c.setConnectTimeout(timeout); // timeout
			c.setReadTimeout(timeout);
			return c;
		}
	}
}
