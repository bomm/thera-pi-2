package patientenFenster;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

public class MyAccessory extends JComponent implements PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2626268910546018039L;

	private Image image;

	public MyAccessory(JFileChooser chooser) {

		chooser.addPropertyChangeListener(this);

		setPreferredSize(new Dimension(150, 150));
		this.setBounds(0, 10, 0, 0);
		this.setBorder(BorderFactory.createEtchedBorder());
	}


	public void propertyChange(PropertyChangeEvent evt) {
		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt
				.getPropertyName())) {
			// Get the new selected file
			File newFile = (File) evt.getNewValue();

			// Prepare the preview data based on the new selected file
			try {
				image = Toolkit.getDefaultToolkit()
						.getImage(newFile.toString()).getScaledInstance(150, 150, Image.SCALE_FAST);
				MediaTracker mediaTracker = new MediaTracker(this);
				mediaTracker.addImage(image, 0);
				mediaTracker.waitForID(0);
			} catch (Exception ie) {
			}
			;
			repaint();
		}
	}

	public void paint(Graphics g) {
		try {
			g.drawImage(image, 10, 0, 150, 150, this.getBackground(), null);
		} catch (NullPointerException np) {

		}
	}
}
