package gffFiles;

import java.util.ArrayList;
import java.util.Collections;

import com.lowagie.text.html.simpleparser.IncCell;

import array.tools.LongArrayTools;

public class ExonGroup 
{
	private String id; 
	private boolean isPlusStrand; 
	private ArrayList<Exon> exons;
	
	public ExonGroup( final String id ) 
	{ 
		setId( id );
	}
	
	/*
	 * Add exon to group 
	 */
	public void addExon( final Exon exon )
	{		
		if( getExons() == null )
		{
			setExons( new ArrayList<Exon>() );
			setPlusStrand(  exon.isPlusStrand() ); 
		}
		getExons().add( exon ); 
	}
	
	/*
	 * Sorts exon by chromosomal position. 
	 */
	public void sortByPos() 
	{
		Collections.sort( getExons(), new ExonPosComparator() );
	}
	
	/*
	 * Get exon positions as array 
	 */
	private ArrayList<Integer[]> getPositions()
	{
		final ArrayList<Integer[]> positions = new ArrayList<Integer[]>();  
		for( Exon exon : getExons() )
		{
			positions.add( new Integer[]{ exon.getChromStart(), exon.getChromEnd() } );   
		}
		return positions; 
	}
	
	public String toString() 
	{
		final ArrayList<Integer[]> pos = getPositions(); 
		String out = "";
		for( int i = 0; i < pos.size(); i++ ) {
			out += pos.get( i )[ 0 ] + ";" + pos.get( i )[ 1 ];
			if( i != ( pos.size() - 1 ) ) 
			{
				 out += "\t"; 
			}
		}
		return out; 
	}
	
	//Getter
	public ArrayList<Exon> getExons() { return this.exons; }
	public String getId() { return this.id; } 
	public boolean isPlusStrand() { return this.isPlusStrand; }
	
	//Setter 
	private void setExons( final ArrayList<Exon> exons ) { this.exons = exons; }
	private void setId( final String id ) { this.id = id; } 
	private void setPlusStrand( final boolean isPlusStrand ) { this.isPlusStrand = isPlusStrand; }
	
}
