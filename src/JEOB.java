import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;


public class JEOB {
	
	File f;
	RandomAccessFile raf;
	
	// header data
	int magic;
	int const_code_size;
	int data_size;
	int data_bss_size;
	
	byte [] code_image;
	byte [] data_image;
	
	byte[] md4;
	
	int readInt () throws IOException
	{
		int b0 = raf.read();
		int b1 = raf.read();
		int b2 = raf.read();
		int b3 = raf.read();
		
		return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
	}
	
	JEOB (final File f) throws Exception
	{
		this.f = f;
		raf = new RandomAccessFile(f, "r");
		
		magic = readInt();
		if (magic != 0x00424f45) {
			throw new Exception(String.format("Not an EOB: \"%s\"", f.getAbsolutePath()));
		}
		
		raf.seek(0xa4);
		const_code_size = readInt(); 
		data_size = readInt(); 
		raf.seek(0xb8);
		data_bss_size = readInt();
		
		if (data_size > data_bss_size) {
			throw new Exception(String.format("Implausible data sizes: data_size=%d greater than data_bss_size=%d", data_size, data_bss_size));
		}
		
		code_image = new byte [const_code_size];
		data_image = new byte [data_bss_size];

		raf.seek(236);
		int code_bytes_read = raf.read(code_image, 0, const_code_size);
		if (code_bytes_read != const_code_size) {
			throw new Exception(String.format("Error reading code image. Size mismatch: #read=%d not eq #expected=%d", code_bytes_read, const_code_size));
		}
		int data_bytes_read = raf.read(data_image, 0, data_size);
		if (data_bytes_read != data_size) {
			throw new Exception(String.format("Error reading data image. Size mismatch: #read=%d not eq #expected=%d", data_bytes_read, data_size));
		}
		
		MessageDigest md = MessageDigest.getInstance("MD4");
		md.update(code_image);
		md.update(data_image);
		md4 = md.digest();
	}
	

}
