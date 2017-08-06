package T104TimeSync;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class Stack {
	
	public static byte[] stack(DataInputStream in) throws IOException{
	 int start;
     do
     {
         start = in.read();
     } while (start != -1 && start != 0x68);
     if (start == -1)
         throw new EOFException();
     int length = in.readUnsignedByte();
     byte[] message = new byte[length];
     in.readFully(message);
     System.out.println("Stack: "+T104TimeSync.ByteToStr.byteToStr(message));
     return message;
	}
}
