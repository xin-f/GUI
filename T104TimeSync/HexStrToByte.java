package T104TimeSync;
import java.util.Arrays;
/*hexString长度为奇数时,最后一个落单的char没处理,即Byte数组的最后一个元素不对*/
public class HexStrToByte {

		 public static byte[] hexStringToBytes(String hexString) {  
		        if (hexString == null || hexString.equals("")) {  
		            return null;  
		        }  
//		        System.out.println("HexStrToByte in");
		        hexString = hexString.toUpperCase();  
		//        System.out.println("ts frame in HexStrToByte: "+hexString);
		        int length = hexString.length();
		        int length_arr = (hexString.length() % 2==0)?hexString.length()/2:hexString.length()/2+1;  
		        char[] hexChars = hexString.toCharArray(); 
		        byte[] d = new byte[length_arr];  
		        for (int i = 0; i < length; i+=2) {  
		            int[] twoHex=new int[2];
		            Arrays.fill(twoHex, 0);
		            for(int j = 0; j<2; j++){	//每两个字符组合成一个字节.
		            if(hexChars[i+j]>='A'&&hexChars[i+j]<='F')
		            	{twoHex[j] = hexStringToBytesLetter(hexChars[i+j]); }
		            else if (hexChars[i+j]>='0'&&hexChars[i+j]<='9')
		            	{twoHex[j] = hexStringToBytesNumber(hexChars[i+j]);}
		            else 
		            	System.out.println("invalid input:Not HEX string. i is: "+i+" and j: is "+j+" and hexChars[i+j] is: "+hexChars[i+j]);
		            d[i/2] = (byte)(d[i/2] | twoHex[j] << 4*(1-j));
		            }   
		        }  
//		        System.out.println("HexStrToByte out");
		        return d;  
		    }  
		 public static int hexStringToBytesNumber(char c){
			 int i = 0;
			 i = (int)c - 48;
			 return i;
		 }
		 public static int hexStringToBytesLetter(char c){
			 int i = 0;
			 i = (int)c - 55;
			 return i;
		 }
}



