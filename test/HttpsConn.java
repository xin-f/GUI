package test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsConn {
	public static void main(String[] arg) {
		Timer t = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				System.out.println(getRandomString());
			}
		};
		t.schedule(timerTask, 0, 100);
	}

	public static String getRandomString() {
		String cha = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
		String upp = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String low = "abcdefghijklmnopqrstuvwxyz";
		String dig = "0123456789";
		String all = cha + upp + low + dig;
		int length = all.length();

		StringBuffer target = new StringBuffer();
		int random = (int) Math.round(Math.random() * 23); // 目标字符串,1到24位.
		for (int i = 0; i <= random; i++) {
			int random_all = (int) Math.round(Math.random() * (length - 1)); // 字符串的index
			target.append(all.charAt(random_all));
		}
		return target.toString();
	}

	public static boolean judge(String target) {
		Pattern p_low = Pattern.compile("[a-z]");
		Pattern p_up = Pattern.compile("[A-Z]");
		Pattern p_dig = Pattern.compile("[\\d]");
		Pattern p_cha = Pattern.compile("[ !\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~]");
		Matcher m_low = p_low.matcher(target);
		Matcher m_up = p_up.matcher(target);
		Matcher m_dig = p_dig.matcher(target);
		Matcher m_cha = p_cha.matcher(target);

		if (m_low.find() && m_up.find() && m_cha.find() && m_dig.find() && (target.length() >= 8))
			return true;
		else
			return false;
		/*
		* else { if (!m_low.find()) { System.out.println("low========"+target); } if
		* (!m_up.find()) { System.out.println("up========"+target); } if
		* (!m_cha.find()) { System.out.println("cha============="+target); } if
		* (!m_dig.find()) { System.out.println("dig=========="+target); }
		*/
	}

}

