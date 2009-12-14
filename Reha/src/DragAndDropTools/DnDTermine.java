package DragAndDropTools;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

public class DnDTermine {
	DropTargetListener dropTargetListener = null;
	public DnDTermine(){
		@SuppressWarnings("unused")
		DropTargetListener dropTargetListener =
			 new DropTargetListener() {

			  // Die Maus betritt die Komponente mit
			  // einem Objekt
			  public void dragEnter(DropTargetDragEvent e) {
				  System.out.println("Enter Es wurde gedroppt an Position -> "+e.getLocation());				  
			  }

			  // Die Komponente wird verlassen 
			  public void dragExit(DropTargetEvent e) {
				  System.out.println("Es wurde gedroppt an Position -> ");
			  }

			  // Die Maus bewegt sich �ber die Komponente
			  public void dragOver(DropTargetDragEvent e) {
				  System.out.println("Over Es wurde gedroppt an Position -> "+e.getLocation());				  
			  }

			  public void drop(DropTargetDropEvent e) {
			    try {
			      Transferable tr = e.getTransferable();
			      DataFlavor[] flavors = tr.getTransferDataFlavors();
			      for (int i = 0; i < flavors.length; i++)
			       if (flavors[i].isFlavorJavaFileListType()) {
			        // Zun�chst annehmen
			        e.acceptDrop (e.getDropAction());
			       //List files = (List) tr.getTransferData(flavors[i]);
			        // Wir setzen in das Label den Namen der ersten 
			        // Datei
			        //label.setText(files.get(0).toString());
			        System.out.println("Es wurde gedroppt an Position -> "+e.getLocation());
			        e.dropComplete(true);
			        return;
			       }
			    } catch (Throwable t) { t.printStackTrace(); }
			    // Ein Problem ist aufgetreten
			    e.rejectDrop();
			  }
			   
			  // Jemand hat die Art des Drops (Move, Copy, Link)
			  // ge�ndert
			  public void dropActionChanged(
			         DropTargetDragEvent e) {}
			};

		
	}
	public DropTargetListener getDndListener(){
		return dropTargetListener; 
	}
}	

