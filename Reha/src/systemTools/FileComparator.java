package systemTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


	    public class FileComparator implements FileNIONonDirectBufComparato {
	        int bufferSize;
	        ByteBuffer buffer1;
	        ByteBuffer buffer2;
	        
	        public FileComparator(int bufferSize) {
	            this.bufferSize = bufferSize;
	            buffer1 = ByteBuffer.allocate(bufferSize);
	            buffer2 = ByteBuffer.allocate(bufferSize);
	            System.out.println("Using: java.nio with non direct byte-buffers of size " + bufferSize + " bytes");
	        }
	        
	        private int readIn(FileChannel f, ByteBuffer buffer) throws IOException {
	            int x = 0, z = 0;
	            do {
	                x+=z;
	                z = f.read(buffer);
	            } while ((x < bufferSize) & (z != -1));
	            return x;
	        }
	        
	        private static boolean compare(ByteBuffer a, ByteBuffer b, int len) {
	            for(int i = 0; i < len; i++) if (a.get(i) != b.get(i)) return false;
	            return true;
	        }
	        
	        public int compareFiles(File source1, File source2) throws IOException {
	            FileInputStream s1 = null, s2 = null;
	            long source1Size = source1.length();
	            long source2Size = source2.length();
	            if (source1Size != source2Size) return 1;
	            try {
	                s1 = new FileInputStream(source1);
	                s2 = new FileInputStream(source2);
	                long alreadyReadedBytes = 0;
	                
	                while (alreadyReadedBytes < source1Size) {
	                    buffer1.clear();
	                    buffer2.clear();
	                    int size1 = readIn(s1.getChannel(), buffer1);
	                    /*int size2 = */readIn(s2.getChannel(), buffer2);
	                    //assert (size1 != size2) :"Files have different sizes in contradiction to earlier check";
	                    if (!compare(buffer1, buffer2, size1)){
	                    	
	                    	return 2;
	                    }
	                    alreadyReadedBytes += size1;
	                }
	                return 0;
	            } finally {
	                if (s1 != null) s1.close();
	                if (s2 != null) s2.close();
	            }
	        }
	    }
//public interface FileNIONonDirectBufComparator {
	    
	    interface FileNIONonDirectBufComparato {
	    	public int compareFiles(File source1, File source2) throws IOException;
	    	}
	    
