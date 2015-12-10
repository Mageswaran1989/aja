import java.nio.ByteBuffer
import java.nio.charset.{Charset, CharsetEncoder}

val str = "Mageswaran"

str.isEmpty


str.getClass
val arr = str.getBytes(Charset.forName("UTF-8"))

arr.length


str.toCharArray()

str.getBytes("UTF-8").length

Charset.forName("UTF-8").encode(str)


//// do this once to setup
//CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder();
//
//// for each string
//int len = str.length();
//byte b[] = new byte[len + 1];
//ByteBuffer bbuf = ByteBuffer.wrap(b);
//enc.encode(CharBuffer.wrap(str), bbuf, true);
//// you might want to ensure that bbuf.position() == len
//b[len] = 0;

//val str1 = "Aja"
//val  enc : CharsetEncoder = Charset.forName("ISO-8859-1").newEncoder();
//val length = str1.length
//val byte: Array[Byte] = new Byte(length+1)
//val bbuf : ByteBuffer = ByteBuffer.wrap()



