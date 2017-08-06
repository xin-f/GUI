package redundancy_client;

public class ByteToStr {
	public static String byteToStr(byte[] b) {
//		System.out.println("ByteToStr in");
		   String src = "0123456789ABCDEF";
		   String rslt = "";
		   char[] c = new char[b.length * 2];
		   for(int i =0;i<b.length;i++){
			   int h4 = b[i]&0xf0;
			   int l4 = b[i]&0x0f;
			   int pos = 2 * i;
			   c[pos] = src.charAt((char)(h4>>4));
			   c[pos+1] = src.charAt((char)l4);
			   }
		   rslt = String.valueOf(c);
//		   System.out.println("ByteToStr out");
		   return rslt;
	   }

}
