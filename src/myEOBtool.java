import java.io.File;
import java.security.Provider;
import java.security.Security;


public class myEOBtool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("myEOBtool v1.00, 18.02.2016");
		
		if (args.length < 1) {
			System.err.println ("usage: myEOBtool <EOB-File or EOB-Directory>\n");
			System.exit(-1);
		};
		
		
		try {
			Security.addProvider(new Provider("myProvider", 1.0, "Provider for MD4") {
				private static final long serialVersionUID = 1L;
				{ 
					put ("MessageDigest.MD4", "org.apache.mina.proxy.utils.MD4"); 
				}
			});
			
			final File folder = new File (args[0]);
	        if (folder.isDirectory()) {
				parseDir (0, folder);
	        }
	        else {
	    		final File eobFile = new File (args[0]);
	    		processEOB (0, eobFile);
	        }
			
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
       	System.out.println("ready.\n");
		
	}

	static void processEOB (int level, final File eobFile) throws Exception
	{
		JEOB jeob= new JEOB (eobFile);

       	System.out.println(java.lang.String.format("EOB: \"%s\"", eobFile.getName()));
       	System.out.println(java.lang.String.format("	const_code_size = %d = 0x%08x", jeob.const_code_size, jeob.const_code_size));
       	System.out.println(java.lang.String.format("	data_size = %d = 0x%08x", jeob.data_size, jeob.data_size));
       	System.out.println(java.lang.String.format("	data_bss_size = %d = 0x%08x", jeob.data_bss_size, jeob.data_bss_size));
       	
       	System.out.println(java.lang.String.format("	md4(const_code_size+data_bss_size) = [%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x]", 
      			jeob.md4[0], jeob.md4[1], jeob.md4[2], jeob.md4[3], jeob.md4[4], jeob.md4[5], jeob.md4[6], jeob.md4[7],
      			jeob.md4[8], jeob.md4[9], jeob.md4[10], jeob.md4[11], jeob.md4[12], jeob.md4[13], jeob.md4[14], jeob.md4[15]));
       	
//       	FileOutputStream of = new FileOutputStream("D:/tmp/eobdmp.txt");
//       	of.write(jeob.code_image);
//       	of.write(jeob.data_image);
//       	of.close();
	}

	static void parseDir (int level, final File folder) throws Exception
	{
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
//	            parseDir (level+1, fileEntry);
	        } else if (fileEntry.isFile()) {
	        	processEOB (level, fileEntry);
	        } else {
	        	System.err.println(fileEntry.getAbsolutePath());
	        }
	    }
	}

}
