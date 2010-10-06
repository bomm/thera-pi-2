package openOfficeorg;
/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                     						*
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG                                            *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag																												*
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/

/*
 * Last changes made by $Author: thera-pi $, $Date: 2010-10-06 18:35:54 $
 */
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.event.ICloseEvent;
import ag.ion.bion.officelayer.event.ICloseListener;
import ag.ion.bion.officelayer.event.IEvent;

/**
 * The close listener that should be used for all snippets. The details of the listener 
 * are discussed in Snippet04.
 * This listener is used in all Snippets starting from Snippet05.
 * 
 * @author Sebastian R�sgen
 * @version $Revision: 1.3 $
 * @date 17.03.2006
 */
public class XRehaDocumentCloseListener implements ICloseListener {

	public IOfficeApplication officeAplication = null;
	
  //----------------------------------------------------------------------------
	/**
	 * Constructs a new SnippetDocumentCloseListener
	 * 
	 * @author Sebastian R�sgen
	 * @date 17.03.2006
	 */
	public XRehaDocumentCloseListener(IOfficeApplication officeAplication) {
		this.officeAplication = officeAplication;
	}
  //----------------------------------------------------------------------------
  /**
   * Is called when someone tries to close a listened object. Not needed in
   * here.
   * 
   * @param closeEvent close event
   * @param getsOwnership information about the ownership
   * 
   * @author Sebastian R�sgen
	 * @date 17.03.2006
   */ 
	public void queryClosing(ICloseEvent closeEvent, boolean getsOwnership) {
		//nothing to do in here
	}
  //----------------------------------------------------------------------------
  /**
   * Is called when the listened object is closed really.
   * 
   * @param closeEvent close event
   * 
   * @author Sebastian R�sgen
	 * @date 17.03.2006
   */


	public void notifyClosing(ICloseEvent closeEvent) {
		/*try {
			//officeAplication.deactivate(); // this is really necessary
			//System.out.println("Office application deactivated.");
			//System.out.println(closeEvent.getSourceObject());
		}/* 	 
		catch (OfficeApplicationException exception) {
			System.err.println("Error closing office application!");
			exception.printStackTrace();
		}*/			
	}
	

  //----------------------------------------------------------------------------
  /**
   * Is called when the broadcaster is about to be disposed. 
   * 
   * @param event source event
   * 
   * @author Sebastian R�sgen
	 * @date 17.03.2006
   */
	public void disposing(IEvent event) {

		//nothing to do in here
	}
  //----------------------------------------------------------------------------
	
}