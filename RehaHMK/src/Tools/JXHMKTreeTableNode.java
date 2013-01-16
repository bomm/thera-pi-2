package Tools;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

public class JXHMKTreeTableNode extends DefaultMutableTreeTableNode {
	@SuppressWarnings("unused")
	private boolean enabled = false;
	public IndiKey key = null;
	public JXHMKTreeTableNode(String name,IndiKey key ,boolean enabled){
		super(name);
			this.key = key;
			if(key != null){
				this.setUserObject(key);
			}
	}
}