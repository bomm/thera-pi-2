package openOfficeorg;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.event.DocumentAdapter;
import ag.ion.bion.officelayer.event.IDocumentEvent;
import ag.ion.bion.officelayer.event.IDocumentListener;
import ag.ion.bion.officelayer.event.IEvent;

public class XRehaDocumentListener extends DocumentAdapter implements IDocumentListener{
	public IOfficeApplication officeAplication = null;
	
	public XRehaDocumentListener(IOfficeApplication officeAplication) {
		this.officeAplication = officeAplication;
	}
	@Override
	public void onAlphaCharInput(IDocumentEvent arg0) {
		//System.out.println("OO"+1);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFocus(IDocumentEvent arg0) {
		//System.out.println("OO Focus"+2);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInsertDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInsertStart(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoad(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadFinished(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModifyChanged(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseOut(IDocumentEvent arg0) {
		//System.out.println("OO"+2);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseOver(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNew(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNonAlphaCharInput(IDocumentEvent arg0) {
		//System.out.println("OO"+2);		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSave(IDocumentEvent arg0) {
		//System.out.println("OO"+3);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSaveAs(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSaveAsDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSaveDone(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSaveFinished(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnload(IDocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disposing(IEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void notify(IDocumentEvent arg0) {
		//System.out.println(arg0);
	
	}

}
