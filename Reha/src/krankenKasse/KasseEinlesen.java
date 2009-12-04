package krankenKasse;

import hauptFenster.Reha;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class KasseEinlesen {
	
	public void KasseEinlesen(){
		oeffneKostentraeger();
	    System.out.println("Austritt aus KasseEinlesen");
 			

	}
	private void oeffneKostentraeger(){
		final JFileChooser chooser = new JFileChooser("Kostenträger-Datei wählen");
	    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    final File file = new File(Reha.proghome+"KostenTraeger/");

	    chooser.setCurrentDirectory(file);

	    chooser.addPropertyChangeListener(new PropertyChangeListener() {
	        public void propertyChange(PropertyChangeEvent e) {
	            if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
	                    || e.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)) {
	                final File f = (File) e.getNewValue();
	            }
	        }
	    });
	    chooser.setVisible(true);

	    final int result = chooser.showOpenDialog(null);

	    if (result == JFileChooser.APPROVE_OPTION) {
	        File inputVerzFile = chooser.getSelectedFile();
	        String inputVerzStr = inputVerzFile.getPath();
	        System.out.println("Eingabepfad:" + inputVerzStr);
	        if(inputVerzStr.trim().toUpperCase().indexOf(".KE") < 0){
	        	JOptionPane.showMessageDialog(null,"Keine gültige Kostenträgerdatei!");
	        }else{
	        	/*
	        	try {
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				*/

	        }
	 
	    System.out.println("Abbruch");
	    chooser.setVisible(false); 			
		
	    }
		
	}
}	
	
	


