package dialoge;

import java.awt.Component;
import java.lang.reflect.Method;
import java.util.EventListener;

public class ListenerTools {
	@SuppressWarnings("unchecked")
	public static void removeListeners(Component comp)
    {
        Method []methods = comp.getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];
            String name = method.getName();
            if (name.startsWith("remove") && name.endsWith("Listener"))
            {

                Class[]params = method.getParameterTypes();
                if (params.length == 1)
                {
                    EventListener []listeners = null;
                    try {
                        listeners =  comp.getListeners(params[0]);
                    }catch(Exception e){
                        // It is possible that someone could create a listener
                        // that doesn't extend from EventListener.  If so,ignore it
                        ////System.out.println("Listener " + params[0] + " does not extend EventListener");
                        continue;
                    }
                    for ( int j = 0; j < listeners.length; j++)
                    {
                        try {
                            method.invoke(comp, new Object[]{listeners[j]});
                            listeners[j] = null;
                            ////System.out.println("removed Listener " + name + "+ for comp " + comp );
                        }catch(Exception e){
                           // //System.out.println("Cannot invoke removeListener method " + e);
                            // Continue on.  The reason for removing alllisteners is to
                            // make sure that we don't have a listener holdingon to something
                            // which will keep it from being garbage collected.We want to
                            // continue freeing listeners to make sure we canfree as much
                            // memory has possible
                        }
                    }
                }else{
                    // The only Listener method that I know of that has more than
                    // one argument is removePropertyChangeListener.  If it is
                    // something other than that, flag it and move on.
                    //if (!name.equals("removePropertyChangeListener"))
                        //System.out.println("    Wrong number of Args " + name);
                }
            }
        }
        comp = null;
    }


}
