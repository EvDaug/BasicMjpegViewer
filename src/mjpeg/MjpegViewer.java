package mjpeg;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MjpegViewer extends JFrame {
    private static String TAG = "MJPEG_Viewer";
	private JFrame frame;
	
	public MjpegViewer()
	{
		//Create the frame
		frame = new JFrame("main");
	    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    frame.setVisible(true);
	}

	public void setImage(BufferedImage image)
	{
		//Take an 'image' and place it within the frame
		JLabel picLabel = new JLabel(new ImageIcon(image));
		JPanel jPanel = new JPanel();
		jPanel.add(picLabel);

		frame.setSize(new Dimension(image.getWidth(), image.getHeight()));
		frame.add(jPanel);
		frame.setVisible(true);
	}

	    
}