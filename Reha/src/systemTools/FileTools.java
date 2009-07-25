package systemTools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

public class FileTools {
	public static boolean delDirAndFile(File dir){
		if (dir.isDirectory()){
				String[] entries = dir.list();
				for (int x=0;x<entries.length;x++){
					File aktFile = new File(dir.getPath(),entries[x]);
					delDirAndFile(aktFile);
				}
				if (dir.delete())
					return true;
				else
					return false;
			}
			else{
				if (dir.delete())
					return true;
				else
					return false;
			}
	}	
	public static boolean delFileWithSuffixAndPraefix(File dir,String xpraefix,String xsuffix){
		final String suffix = xsuffix;
		final String praefix = xpraefix;
		FilenameFilter fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return ( name.startsWith(praefix) && name.endsWith(suffix));
			}

	    };
		File[] files = dir.listFiles(fileFilter);
		boolean ok = true;
		for (int i = 0; i < files.length; i++) {
			if(!files[i].delete()){
				ok = false;
			}
		}
		return (files.length == 0 || !ok ? false : true);
	}
	public static ArrayList<File> searchFile(File dir, String find) {

		File[] files = dir.listFiles();
		ArrayList<File> matches = new ArrayList<File> ();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().equalsIgnoreCase(find)) { // überprüft ob der Dateiname mit dem Suchstring
										 // übereinstimmt. Groß-/Kleinschreibung wird
										 // ignoriert.
					matches.add(files[i]);
				}
				if (files[i].isDirectory()) {
					matches.addAll(searchFile(files[i], find)); // fügt der ArrayList die ArrayList mit den
										    // Treffern aus dem Unterordner hinzu
				}
			}
		}
		return matches;
	}	

}
