package hauptFenster;


	import java.util.Map;
	 
	import javax.swing.LookAndFeel;
	import javax.swing.UIDefaults;
	import javax.swing.UIManager;
	import javax.swing.UIManager.LookAndFeelInfo;
	 
	/**
	 * @author Tom
	 *
	 */
	public class ListUIManagerValues {
	 
	    /**
	     * @param args
	     */
	    public static void main(String[] args) throws Exception {
	 
	        LookAndFeelInfo[] lookAndFeelInfos = UIManager
	                .getInstalledLookAndFeels();
	        for (LookAndFeelInfo lookAndFeelInfo : lookAndFeelInfos) {
	            System.out.println("LookAndFeel: " + lookAndFeelInfo.getName());
	            LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(
	                    lookAndFeelInfo.getClassName()).newInstance();
	            UIDefaults defaults = lookAndFeel.getDefaults();
	            for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
	                System.out.println(entry.getKey() + ": " + entry.getValue());
	            }
	            System.out.println("######################################");
	        }
            LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(
            		"com.jgoodies.looks.plastic.PlasticXPLookAndFeel").newInstance();
            UIDefaults defaults = lookAndFeel.getDefaults();
            for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
            	System.out.println("jgoodies");
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("############Swing-X Look&Feel##########################");
            lookAndFeel = (LookAndFeel) Class.forName(
    		"org.jdesktop.swingx.plaf.windows.WindowsClassicStatusBarUI").newInstance();            
            for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
            	System.out.println("jgoodies");
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            

	    }
	}

