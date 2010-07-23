package hauptFenster;


	/**
	 * @author Tom
	 *
	 */
	public class ListUIManagerValues {
	 
	    /**
	     * @param args
	     */
	    public static void main(String[] args) throws Exception {
	    	/*
	        LookAndFeelInfo[] lookAndFeelInfos = UIManager
	                .getInstalledLookAndFeels();
	        for(int i = 0; i < lookAndFeelInfos.length; i++){
	        	//System.out.println(lookAndFeelInfos[i].getName());
	        }
	        try{
		        for (LookAndFeelInfo lookAndFeelInfo : lookAndFeelInfos) {
		            //System.out.println("LookAndFeel: " + lookAndFeelInfo.getName());
		            //System.out.println("*************************************************************");
		            LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(
		                    lookAndFeelInfo.getClassName()).newInstance();
		            UIDefaults defaults = lookAndFeel.getDefaults();
		            for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
		            	//System.out.println(lookAndFeelInfo.getName());
		                //System.out.println(entry.getKey() + ": " + entry.getValue());
		                
		            }
		            //System.out.println("######################################");
		        }
	        }catch(Exception ex){
	        	//System.out.println("Fehler bei...");
	        }
            LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(
            		"com.jgoodies.looks.plastic.PlasticXPLookAndFeel").newInstance();
            UIDefaults defaults = lookAndFeel.getDefaults();
            for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
            	//System.out.println("jgoodies");
                //System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            //System.out.println("############Swing-X Look&Feel##########################");
            lookAndFeel = (LookAndFeel) Class.forName(
    		"org.jdesktop.swingx.plaf.windows.WindowsClassicStatusBarUI").newInstance();  
            try{
                for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
                	//System.out.println("jgoodies");
                    //System.out.println(entry.getKey() + ": " + entry.getValue());
                }
    	    }catch(Exception ex){
    	    	//System.out.println("Fehler....");
    	    }
    */	    
	}
	
	}	    

