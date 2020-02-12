package mjpeg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MjpegMain {
    private static final String TAG = "MJPEG_Main";
	public static void main(String[] args) {
        try {
        	//Connect via url and open up the input-stream
    		String url = "http://your-address:8000/stream.mjpg";
			InputStream input = new URL(url).openStream();
			
			//start the receiver
			MjpegReceiver MjpegRe = new MjpegReceiver(input);
			Thread t1 = new Thread(MjpegRe);
			t1.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
