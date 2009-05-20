package roogle;

import org.jdesktop.swingworker.SwingWorker;

public class OptiSuche extends SwingWorker<Void,Void>{
	SuchenSeite thisSuche;
	public static boolean sollStop;
	
	public void init(SuchenSeite thisSuch){
		thisSuche = thisSuch;
	}
	public static void setStop(boolean lwert){
		sollStop = lwert;
	}
	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		while(!sollStop){
			
		}
		return null;
	}

}



class OptiSperre extends SwingWorker<Void,Void>{
	SuchenSeite thisSuche;
	public void init(SuchenSeite thisSuch){
		thisSuche = thisSuch;
	}

	@Override
	protected Void doInBackground() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
