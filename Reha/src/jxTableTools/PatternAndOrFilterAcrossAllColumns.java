package jxTableTools;

import java.util.regex.Pattern;

import org.jdesktop.swingx.decorator.PatternFilter;

public class PatternAndOrFilterAcrossAllColumns extends PatternFilter {
	 
	 private int m_numberOfColumns = -1;
	 private String[] svgl;
	 private String such1;
	 private String such2;		 

	 public PatternAndOrFilterAcrossAllColumns(String  searchText, int matchFlags, int numberOfColumns,String[] vgl ) 
	 { 
		 super( searchText, matchFlags, 0 ); // Default to first column for ease.

		 if( numberOfColumns < 0 ){
			 throw new IllegalArgumentException( "Number of columns cannot be negative." );
		 }
		 
		 m_numberOfColumns = numberOfColumns;
		 svgl = vgl.clone();
		 such1 = svgl[0].trim();
		 such2 = svgl[1].trim();
	}

	 @Override public boolean test( int row ) { 
		 Pattern thePattern = getPattern();
		 if( thePattern == null ){
			 return false;
		 }
		 boolean schongefunden = false;
		 boolean gefunden1 = false;
		 boolean gefunden2 = false;	
		 int i = 0;
		 for(i = 0; i < m_numberOfColumns; i++ ) { 
			 String text = getInputString( row, i ); 

			 if( text == null || text.length() == 0 ){
				 continue;
			 }
			 if(!gefunden1){
				 gefunden1 = (text.contains(such1));				 
			 }
			 if(!gefunden2){
				 gefunden2 = (text.contains(such2));				 
			 }
			 if( gefunden1 && gefunden2 && schongefunden ){
				 return true; 
			 }
			 if( (gefunden1 || gefunden2) ){
				 if(gefunden1 && gefunden2){
					 return true;
				 }else{
					 schongefunden = true;					 
				 }
			 }
		 }
		 	return false; 
		}
	 } 	

