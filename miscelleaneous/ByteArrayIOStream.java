package miscelleaneous;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArrayIOStream {
	public static void main(String args[]) throws IOException {
	      ByteArrayOutputStream bOutput = new ByteArrayOutputStream(12);
	      //写入流,从键盘输入字符,写到流里. 当流里的字符串长度到5时,不再接收新的输入.
	      while( bOutput.size()!= 5 ) {
	         bOutput.write(System.in.read()); //把从键盘输入的数据写到bOutput.
	      }
	      byte b [] = bOutput.toByteArray();//把流里的数据赋给字节数组.
	      for(int x= 0 ; x < b.length; x++) {
	         System.out.print((char)b[x]  + " "); 
	      }
	      System.out.print("\n" );
	      int c;
	      //一个读入流,把从字节数组b里的数据读到这个流里
	      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
	      for(int y = 0 ; y < 1; y++ ) {
	         while(( c= bInput.read())!= -1) {
	            System.out.print(Character.toUpperCase((char)c));
	         }
	         bInput.reset(); 
	      }
	   }

}
