package systemTools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;



public class JRtaComboBox extends JComboBox implements ActionListener,PropertyChangeListener,FocusListener,KeyListener{
public Vector vec  = null;
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
public JRtaComboBox(Vector ve){
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
private void fillOneDimension(Vector ve){
	int lang = ve.size();
	for(int i = 0;i < lang;i++){
		addItem( (String) ve.get(i));
	}
}

private void fillCombo(Vector ve){
	int lang = ve.size()-1;
	for(int i = 0;i < lang;i++){
		addItem( (String) ((Vector)ve.get(i)).get(0));
	}
	
}
public Object getSecValue(){
	return ((Object)((Vector)vec.get(this.getSelectedIndex())).get(1) );
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