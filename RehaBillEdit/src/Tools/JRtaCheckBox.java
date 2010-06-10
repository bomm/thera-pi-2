package Tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;



public class JRtaCheckBox extends JCheckBox implements ActionListener,PropertyChangeListener,FocusListener,KeyListener{

/**
	 * 
	 */
	private static final long serialVersionUID = -7927719190910584318L;
public JRtaCheckBox(){
	super();
	addKeyListener(this);
	addFocusListener(this);
	addActionListener(this);
	setOpaque(false);
}
public JRtaCheckBox(String ss){
	super(ss);
	addKeyListener(this);
	addFocusListener(this);	
	addActionListener(this);
	setOpaque(false);
}
public void listenerLoeschen(){
	this.removeFocusListener(this);
	this.removeKeyListener(this);
	this.removeActionListener(this);
}


@Override
public void propertyChange(PropertyChangeEvent arg0) {
	// TODO Auto-generated method stub
	//this.getParent().dispatchEvent(arg0);	
}

@Override
public void focusGained(FocusEvent arg0) {
	// TODO Auto-generated method stub
	////System.out.println("Focus "+arg0);
	try{
		this.getParent().dispatchEvent(arg0);
	}catch(Exception ex){
		
	}
}

@Override
public void focusLost(FocusEvent arg0) {
	// TODO Auto-generated method stub
	
}

@Override
public void keyPressed(KeyEvent arg0) {
	// TODO Auto-generated method stub
	////System.out.println("Pressed "+arg0);
	int code = arg0.getKeyCode();
	
	if(code == KeyEvent.VK_ENTER || code == 40){
		arg0.consume();
		this.transferFocus();
		this.getParent().dispatchEvent(arg0);
		return;
	}
	if(code == KeyEvent.VK_ESCAPE){
		try{
			this.getParent().dispatchEvent(arg0);
			this.getParent().getParent().dispatchEvent(arg0);
			this.getParent().getParent().getParent().dispatchEvent(arg0);
			this.getParent().getParent().getParent().getParent().dispatchEvent(arg0);
		}catch(Exception ex){
			
		}
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
	////System.out.println("Released "+arg0);
	
}

@Override
public void keyTyped(KeyEvent arg0) {
	// TODO Auto-generated method stub
	////System.out.println("Typed "+arg0);
	
}
@Override
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	try{
		this.getParent().dispatchEvent(arg0);
	}catch(Exception ex){
		
	}
}

}