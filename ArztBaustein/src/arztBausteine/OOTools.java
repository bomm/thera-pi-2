package arztBausteine;

import ag.ion.bion.officelayer.text.ITextDocument;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.style.XStyle;
import com.sun.star.style.XStyleFamiliesSupplier;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.UnoRuntime;

public class OOTools {
	public static void setzePapierFormat(ITextDocument textDocument,int hoch,int breit) throws NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
		XTextDocument xTextDocument = textDocument.getXTextDocument();
		XStyleFamiliesSupplier xSupplier = (XStyleFamiliesSupplier) UnoRuntime.queryInterface(XStyleFamiliesSupplier.class,
		xTextDocument);
		XNameContainer family = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
		xSupplier.getStyleFamilies().getByName("PageStyles"));
		XStyle xStyle = (XStyle) UnoRuntime.queryInterface(XStyle.class, family.getByName("Standard"));
		XPropertySet xStyleProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,	xStyle);
		xStyleProps.setPropertyValue("Height", hoch);
		xStyleProps.setPropertyValue("Width", breit);
	}
	public static void setzeRaender(ITextDocument textDocument,int oben,int unten,int links,int rechts) throws NoSuchElementException, WrappedTargetException, UnknownPropertyException, PropertyVetoException, IllegalArgumentException{
    	XTextDocument xTextDocument = textDocument.getXTextDocument();
    	XStyleFamiliesSupplier xSupplier = (XStyleFamiliesSupplier) UnoRuntime.queryInterface(XStyleFamiliesSupplier.class,
    	xTextDocument);
    	XNameContainer family = (XNameContainer) UnoRuntime.queryInterface(XNameContainer.class,
    	xSupplier.getStyleFamilies().getByName("PageStyles"));
    	XStyle xStyle = (XStyle) UnoRuntime.queryInterface(XStyle.class, family.getByName("Standard") );
    	XPropertySet xStyleProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
    	xStyle);
    	xStyleProps.setPropertyValue("TopMargin",oben);
    	xStyleProps.setPropertyValue("BottomMargin",unten);
    	xStyleProps.setPropertyValue("LeftMargin",links);
    	xStyleProps.setPropertyValue("RightMargin",rechts);
	}	
}
