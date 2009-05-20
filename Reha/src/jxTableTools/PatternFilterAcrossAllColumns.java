package jxTableTools;

import java.util.regex.Pattern;

import org.jdesktop.swingx.decorator.PatternFilter;

public class PatternFilterAcrossAllColumns extends PatternFilter {
 
	 private int m_numberOfColumns = -1;

	 public PatternFilterAcrossAllColumns( String searchText, int matchFlags, int numberOfColumns ) 
	 { 
		 super( searchText, matchFlags, 0 ); // Default to first column for ease.

		 if( numberOfColumns < 0 ){
			 throw new IllegalArgumentException( "Number of columns cannot be negative." );
		 }
		 m_numberOfColumns = numberOfColumns; }

	 @Override public boolean test( int row ) { 
		 Pattern thePattern = getPattern();
		 if( thePattern == null ){
			 return false;
		 }

		 for( int i = 0; i < m_numberOfColumns; i++ ) { 
			 String text = getInputString( row, i ); 
			 if( text == null || text.length() == 0 ){
				 continue;
			 }
			 if( thePattern.matcher( ".*" + text + ".*" ).find() ){
				 return true; 
			 }
		 }

		 	return false; 
		}
	 } 	

