package mjpeg;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.imageio.ImageIO;

public class MjpegReceiver extends DataInputStream implements Runnable {
    private static String TAG = "MJPEG_Receiver";
    private static int maxFrameLength = 200000;//Change size depending size of Jpeg
	private byte[] JPEG_SOF = {(byte) 0xFF, (byte) 0xD8};//The bytes that represent the start of a Jpeg image
    private byte[] headerData = null;
    private byte[] frameData = new byte[maxFrameLength];
    private int frameContentLength = -1;
    private int headerLenPrev = -1;
    private MjpegViewer imageView = new MjpegViewer();

    public MjpegReceiver(InputStream in) {
        super(new BufferedInputStream(in, maxFrameLength));
    }
    
	@Override
	public void run() {
		do {
			try {
				readMjpegFrame();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}while(true);
	}
        
	 public int readMjpegFrame() throws IOException {
	        mark(maxFrameLength);//marks the current position of the input-stream and allows bytes up to maxFrameLength to be read
	        
	        //Get the length to when the Jpeg bytes start
	        int headersLength;
	        try {
	            headersLength = getStartOfJpeg(this, JPEG_SOF);
	        } catch (IOException e) {
	            reset();
	            return -1;
	        }
	        reset();//resets position in the input-stream back to the marked position

	        if (headerData == null || headersLength != headerLenPrev) 
	        {
	            headerData = new byte[headersLength];
	        }
	        headerLenPrev = headersLength;
	        readFully(headerData);//Store the bytes from the input-stream into the byte array until full
	        
	        //Get the size of the Jpeg frame
	        int ContentLengthCurrent;
	        ContentLengthCurrent = imageLength(headerData);
	        frameContentLength = ContentLengthCurrent;
	        
	        reset();
	        skipBytes(headersLength);//skips over the bytes containing the header
	        readFully(frameData, 0, frameContentLength);//reads the bytes of length 'frameContentLength' into 'frameData'

	        BufferedImage image = ImageIO.read( new ByteArrayInputStream( frameData ));
	        imageView.setImage(image);
	        return 0;
	    }

    private int getStartOfJpeg(DataInputStream in, byte[] sequence) throws IOException {
    	//This method will return the position/integer within the input-stream
    	//where the jpeg starts which is contained in the 'sequence'        
        int streamIndex = 0;
        byte streamByte;
        for (int i = 0; i < maxFrameLength; i++) {
        	streamByte = (byte) in.readUnsignedByte();
            if (streamByte == sequence[streamIndex]) {
            	streamIndex++;
                if (streamIndex == sequence.length) {
                    return ((i + 1) - sequence.length);
                }
            } else {
            	streamByte = 0;
            }
        }
        return -1;
    }
    
    private int imageLength(byte[] headerBytes) throws IOException  {
    	//This will search the header array for its properties
    	ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties headerProps = new Properties();
        headerProps.load(headerIn);

        //return the property that of content_length
        return Integer.parseInt(headerProps.getProperty("Content-Length"));
    }
}
