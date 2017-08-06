package miscelleaneous;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regular_pw {
	static String pw = "  ` ";
	public static void main(String[] s){
		
		// 生成Pattern对象并且编译一个简单的正则表达式"Kelvin"
		Pattern p = Pattern.compile("Kelvin");
		// 用Pattern类的matcher()方法生成一个Matcher对象
		String target = "Kelvin Li and Kelvin Chan are both working in Kelvin Chen's KelvinSoftShop company";
		Matcher m = p.matcher(target);
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		int i = 0;
		// 使用find()方法查找第一个匹配的对象
		boolean result = m.find();
		System.out.println(m.start()+" "+m.end()); //这里得到的是0 6, 即end()加了1,指向后面 内容. 要先执行.find()方法才能执行其它.
		// 使用循环将句子里所有的kelvin找出并替换再将内容加到sb里
		while (result) {
			i++;
			m.appendReplacement(sb, "Kevin");	//m是个匹配器,它的匹配公式(正则表达式)是p. 匹配器套用公式 p 对目标字符串进行匹配. 匹配成功一次,就把这次匹配到的字符串换成方法\
			//appendReplacement()的第二个参数,然后赋给第一个参数. 同时原匹配器 字符串的index跑到被匹配到的字符串的后面,比如此例,第1 次匹配后,原target字符串的索引成了6,即前面的m.end().
			System.out.println("第" + i + "次匹配后sb的内容是：" + sb);
			
			result = m.find();
		}
		// 最后调用appendTail()方法将最后一次匹配后的剩余字符串加到sb里；
		//It is intended to be invoked after one or more invocations of the appendReplacement() in order to copy the remainder of the input sequence.
		m.appendTail(sb);
		System.out.println("调用m.appendTail(sb)后sb的最终内容是:" + sb.toString());
		m.appendTail(sb2);
		System.out.println("调用m.appendTail(sb2)后sb2的最终内容是:" + sb2.toString());
		
		
		/**
		 * basic 
		 */
		Pattern p_alphanumeric  = Pattern.compile("[a-zA-Z0-9_]"); //等效于\w
		Pattern p_start = Pattern.compile("^[0-9]"); //以数字开头
		Pattern p_non_word = Pattern.compile("\\W"); //包含换行,制表符等.
		Pattern p_word = Pattern.compile("\\w");
		Pattern p_visible = Pattern.compile("\\S"); //任何非空白字符,即不包括空格,换页,制表符等.
		
		Matcher m_alphanumeric = p_alphanumeric .matcher(pw);
		Matcher m_start = p_start.matcher(pw);
		Matcher m_char = p_non_word.matcher(pw);
		Matcher m_word = p_word.matcher(pw);
		Matcher m_visible = p_visible.matcher(pw);
		
		System.out.println(m_alphanumeric.find());
		System.out.println(m_start.find());
		System.out.println(m_char.find());
		System.out.println(m_word.find());
		System.out.println(m_visible.find());
	}

}
