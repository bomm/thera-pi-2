package lvaEntlassmitteilung;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.JComboBox;



public class JRtaComboBox extends JComboBox implements ActionListener,PropertyChangeListener,FocusListener,KeyListener{
public Vector vec  = null;
public int cmbdisplay;
public int cmbretvalue;
public JRtaComboBox(){
	super();
	addKeyListener(this);
	addActionListener(this);	
}
public JRtaComboBox(String[] ss){
	super(ss);
	addKeyListener(this);
	addActionListener(this);
}
public JRtaComboBox(Vector<String> ve){
	super();
	this.vec = ve;
	if(this.vec.get(0) instanceof Vector){
		fillCombo(this.vec);		
	}else{
		fillOneDimension(this.vec);
	}

	addKeyListener(this);
	addActionListener(this);
}

public JRtaComboBox(Vector<Vector<String>> ve,int item,int ret){
	super();
	this.vec = ve;
	this.cmbdisplay = item;
	this.cmbretvalue = ret;
	if(vec.size()>0){
		if(this.vec.get(0) instanceof Vector){
			fillCombo(this.vec);		
		}else{
			fillOneDimension(this.vec);
		}
	}else{
		this.removeAllItems();
	}

	addKeyListener(this);
	addActionListener(this);
}

public void listenerLoeschen(){
	this.removeFocusListener(this);
	this.removeKeyListener(this);
	this.removeActionListener(this);
}

public void setDataVectorVector(Vector<Vector<String>> ve,int item,int ret){
	this.removeAllItems();
	this.vec = ve;
	this.cmbdisplay = item;
	this.cmbretvalue = ret;
	if(this.vec.size()>0){
		if(this.vec.get(0) instanceof Vector){
			fillCombo(this.vec);
		}else{
			fillOneDimension(this.vec);
		}
	}
}
public void setSelectedVecIndex(int index, String vergleich){
	int lang = getItemCount();
	for(int i = 0;i < lang;i++){
		if(((String)((Vector)this.vec.get(i)).get(index)).equals(vergleich)){
			setSelectedIndex(i);
			break;
		}
	}
}
public void setDataVector(Vector <String> ve){
	this.vec = ve;
	fillOneDimension(this.vec);
}
private void fillOneDimension(Vector ve){
	int lang = ve.size();
	System.out.println(ve);
	removeAllItems();
	for(int i = 0;i < lang;i++){
		addItem( (String) ve.get(i));
	}
}

private void fillCombo(Vector ve){
	int lang = ve.size();
	for(int i = 0;i < lang;i++){
		addItem( (String) ((Vector)ve.get(i)).get(this.cmbdisplay));
		System.out.println("i="+i+" - "+(String) ((Vector)ve.get(i)).get(this.cmbdisplay));
	}
	
}
public Vector getDataVector(){
	return this.vec;
}

public Object getSecValue(){
	return ((Object)((Vector)vec.get(this.getSelectedIndex())).get(this.cmbretvalue) );
}
public Object getValue(){
	return ((String)((Vector)vec.get(this.getSelectedIndex())).get(this.cmbretvalue) );
}
public Object getValueAt(int pos){
	return ((String)((Vector)vec.get(this.getSelectedIndex())).get(pos) );
}

@Override
public void propertyChange(PropertyChangeEvent arg0) {
	// TODO Auto-generated method stub
	//this.getParent().dispatchEvent(arg0);	
}

@Override
public void focusGained(FocusEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void focusLost(FocusEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void keyPressed(KeyEvent arg0) {
	// TODO Auto-generated method stub
	int code = arg0.getKeyCode();
	if(code == KeyEvent.VK_ENTER){
		arg0.consume();
		this.transferFocus();
		this.getParent().dispatchEvent(arg0);
		return;
	}
	if(code == KeyEvent.VK_ESCAPE){
		this.getParent().dispatchEvent(arg0);
		this.getParent().getParent().dispatchEvent(arg0);
		this.getParent().getParent().getParent().dispatchEvent(arg0);
		this.getParent().getParent().getParent().getParent().dispatchEvent(arg0);
		return;
	}
	if(code == 38){
		arg0.consume();
		this.transferFocusBackward();
		this.getParent().dispatchEvent(arg0);
		return;
	}	
}

@Override
public void keyReleased(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	try{
		this.getParent().dispatchEvent(arg0);
	}catch(java.lang.NullPointerException ex){
		
	}
}

}