package T104TimeSync;
//一个文件中最多有一个public类(可以一个都没有), 并且此public类必须与文件名相同.没有public 类时,文件可与任一个类同名.
//任何一个都可以有main方法。可以在一个main方法中调用另外一个类的main方法。有main函数的class，不一定是主class.编译后编译器会将class分开

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//68 14 控制字节1 控制字节2 控制字节3 控制字节4  67（ASDU） 1（信息体个数）06 00（传送原因） addl addh 00 00 00（信息体地址）msl msh min hour day month year
 class TimeHandling {
	public static int year, month, day, hour, minute,second,msl,msh,ms;
	public static long millionsecond;
	public static String CUR_TIME;
	
	//Get time  获得当前时间
	public static void getTime()  {
		String str,strh,strl = null;
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR)-2000;
		month = c.get(Calendar.MONTH)+1;
		day = c.get(Calendar.DATE);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		second = c.get(Calendar.SECOND);
		millionsecond = c.getTimeInMillis();
		str = Long.toString(millionsecond);
	//	System.out.println(c.getTime());
	//	System.out.println(hour);
		str=str.substring(str.length()-3, str.length());
	//	System.out.println(str);
		ms = Integer.parseInt(str)+second*1000;
		str = Integer.toHexString(ms);
		if(ms < 16){
			str = "000"+str;
		}else if(ms < 256){
			str = "00"+str;
		}
		else if(ms < 4096){
			str = "0"+str;
		}
	//	System.out.println(str);
		strl = str.substring(2, 4);
		strh = str.substring(0, 2);
		msl = Integer.parseInt(strl,16);
		msh = Integer.parseInt(strh,16);
	//	System.out.println("msh: "+msh);System.out.println("msl: "+msl);
	//	System.out.println(c.get(Calendar.ZONE_OFFSET));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CUR_TIME = df.format(c.getTime());
	//	System.out.println(CUR_TIME);
	}
}

//68 14 Ns1 Ns2 Nr3 Nr4  67（ASDU） 1（no.）06 00（COT） addl addh 00 00 00（IOA）msl msh min hour day month year
//对时激活报文的min 字节,最高位为0表Valid,第二位为0表Genuine time,其余六余为分钟; hour 字节最高位为0表 SU 为Standard time; day字节高三位为dayOfWeek,可不置.
//对时确认报文的dayOfWeek会被置位.
 class TimeFraming {
	
	public static int commadd;
	
		public static String framing(){
		String str = null;
		TimeHandling.getTime();
		if(VisibleFrame.chkbx_SetTime == false){	//复选框没选中,用系统时间对时
			str = "68140000000067010600"+getComaddr(VisibleFrame.comaddr)+"000000"+align(TimeHandling.msl)+align(TimeHandling.msh)
				+align(TimeHandling.minute)+align(TimeHandling.hour)+align(TimeHandling.day)+align(TimeHandling.month)+align(TimeHandling.year);
		}
		else{			//手动设置时间来对时.
			Date d = null;
			String timeModified = null;
			String timeInMs = null;
			String strl = null;
			String strh = null;
			String ms_str = null;
			int msl,msh;
			int ms = 0;
			int len_time = 0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			timeModified = VisibleFrame.displayedTime;
			len_time = timeModified.length();
			try{
				d = sdf.parse(timeModified);
			}catch(ParseException pe){
				VisibleFrame.updateTextArea("\n"+"  "+pe.getMessage());
			}
			timeInMs = Long.toString(d.getTime());
			ms = Integer.parseInt(timeInMs.substring(timeInMs.length()-3, timeInMs.length())); //最后三位,小于1秒的部分,即纯毫秒
			ms += (Integer.parseInt(timeModified.substring(len_time-2, len_time)))*1000;//秒转为毫秒
			ms_str = Integer.toHexString(ms);
			if(ms < 16){
				ms_str = "000"+ms_str;
			}else if(ms < 256){
				ms_str = "00"+ms_str;
			}
			else if(ms < 4096){
				ms_str = "0"+ms_str;
			}
			strl = ms_str.substring(2, 4);
			strh = ms_str.substring(0, 2);
			msl = Integer.parseInt(strl,16);
			msh = Integer.parseInt(strh,16);
			//timeModified "yyyy-MM-dd HH:mm:ss"
			System.out.println(timeModified);
			str = "68140000000067010600"+getComaddr(VisibleFrame.comaddr)+"000000"+align(msl)+align(msh)+align(Integer.parseInt(timeModified.substring(len_time-5, len_time-3)))
			+align(Integer.parseInt(timeModified.substring(len_time-8, len_time-6)))+align(Integer.parseInt(timeModified.substring(8, 10)))
			+align(Integer.parseInt(timeModified.substring(5, 7)))+align(Integer.parseInt(timeModified.substring(0, 4))-2000);
		}
		return str;
	}
		
		public static String align(int i){
			String str = "";
			str = Integer.toHexString(i);
			if (i<16){
				str = "0"+str;
			//	System.out.println("年月日时分毫秒单字节前加0");
			}
			return str;
		}
		
		public static String getComaddr(int arg){
		//	arg = VisibleFrame.comaddr;
			String str = Integer.toString(arg, 16);
			if (arg < 16) str = "000"+str;
			else if (arg < 256) str = "00"+str;
			else if (arg < 4096) str = "0"+str;
			return (str.substring(2, 4)+str.substring(0, 2));
			
		}

}
 
 
//681400000000670107000100000000D6D9B20C310411
//681400000000670107000100000000 D6D9 B2(1b of validity, 1b of Genuine time) 0C(1b of SU) 31(3b day of week) 04 11
//4/17/2017 12:51:10:072 Wed [Activation/Confirmation, Invalid, Genuine time, Standard time]
//VisibleFrame.updateTextArea("4/17/2017 12:51:10:072 [Activation, Invalid, Genuine time, Standard time]\n");
 class TimeParsing {
	/**
	 * 只用来分析时钟报文. 
	 * @param arg
	 * @return
	 */
	public static String parsing(String arg){
//	public static void main(String[] args){
		String str = null;
		String Date = null;
		String Day = null;
		String dayOfWeek = null;
		String Time = null;
		String Year = null;
		String Month = null;
		String Second = null;
		String MillionSecond = null;
		String Hour = null;
		String Minute = null;
		String Cos = null;
		boolean invalid=false;boolean genuine=false;boolean summer = false;
		int dayofweek = 0;
		int day = 0; int hour = 0; int minute = 0; int ms = 0;
//		String arg = "681400000000670107000100000000D6D9F20CF70A11";
		int len = arg.length();
		Day= arg.substring(len - 6, len -4);
		dayofweek=Integer.parseInt(Day,16);
		dayofweek = dayofweek>>5;
		switch(dayofweek){
		case 0:
			dayOfWeek = "";break;
		case 1:
			dayOfWeek = "Mon";break;
		case 2:
			dayOfWeek = "Tue";break;
		case 3:
			dayOfWeek = "Wed";break;
		case 4:
			dayOfWeek = "Thu";break;
		case 5:
			dayOfWeek = "Fri";break;
		case 6:
			dayOfWeek = "Sat";break;
		case 7:
			dayOfWeek = "Sun";break;
		}
		Year = Integer.toString(Integer.parseInt(arg.substring(len-2, len), 16)+2000);	
		Month = Integer.toString(Integer.parseInt(arg.substring(len-4, len-2), 16));
		day = Integer.parseInt(Day, 16) & 31; //清除高3位,保留低5位.
		Day = Integer.toString(day);
		str = "( "+ Month+"/"+Day+"/"+Year+" ";
		hour = Integer.parseInt(arg.substring(len-8, len-6), 16);		
		if((hour>>7)==1){
			summer = true;   //这一位,0表示standard time, 1 是不是表示summer time??
		}
		hour &= 127;
		Hour = Integer.toString(hour);
		minute = Integer.parseInt(arg.substring(len-10, len-8), 16);
		if (0 != (minute & 128)){	//最高位,为false表示  valid
			invalid = true;
		}
		if (0 != (minute & 64)){	//次高位,为false表示 Genuine time
			genuine = true;
		}
		minute &= 63;				//清除高两位.
		Minute = Integer.toString(minute);
		if(Minute.length()==1) Minute = "0"+Minute;
		MillionSecond = arg.substring(len-12, len-10) + arg.substring(len-14, len-12); 	//秒加毫秒一共.
		ms = Integer.parseInt(MillionSecond, 16);
		Second = Integer.toString(ms/1000);
		if(Second.length()==1) Second = "0"+Second;
		MillionSecond = Integer.toString(ms%1000);		//只是毫秒
		if(MillionSecond.length()==1) MillionSecond = "00"+MillionSecond;
		if(MillionSecond.length()==2) MillionSecond = "0"+MillionSecond;
		str = str + Hour + ":" + Minute +":" + Second + ":" + MillionSecond + " " + dayOfWeek;
		str += " [";
		//arg.substring(16, 18)的低6位表示传送原因,最高位为1表示test,次高位为1表示negative. <10>激活终止; <44>未知的类型标识;<45>未知的传送原因;<46>未知的应用服务数据单元公共地址;	<47>未知IOA

/*		if(arg.substring(16, 18).equals("06")){	
			str += "ACT,";
		}
		else if (arg.substring(16, 18).equals("07")){
			str += "CON,";
		}else if(arg.substring(16, 18).equals("10")){
			str += "ACT Termnt,";
		}else if(arg.substring(16, 18).equals("44")){
			str += "ACT Termnt,";
		}*/
		int cot = Integer.parseInt(arg.substring(16, 18), 16);
		if((cot & 128) == 128){
			str += "TEST,";
		}else if ((cot & 64) == 64){
			str += "NEG,";
		}else if (cot == 7){
			str += "CON,";
		}else if (cot == 6){
			str += "ACT,";
		}
		
		if(invalid){
			str += " Invalid,";
		}else{
			str += " Valid,";			
		}
		if(genuine){
			str += " None Gen time,";
		}else{
			str += " Genuine time,";
		}
		if(summer){
			str += " SU])";
		}else{
			str += " Standard time] )";
		}
		return (str);
		//VisibleFrame.updateTextArea("4/17/2017 12:51:10:072 [Activation, Invalid, Genuine time, Standard time]\n");
		//	return str;
	}

}
