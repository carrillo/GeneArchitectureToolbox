package gffFiles;

import java.util.ArrayList;

import org.biojava3.genome.parsers.gff.Feature;
import org.biojava3.genome.parsers.gff.FeatureI;

import array.tools.ArrayListIntegerTools;
/**
 * Keeps the information for each Exon. 
 * info: The Gene-architecture details, connected genes, transcripts... in the FeatureI info. 
 * locatedSpliceJunctions: The Splice junction found residing on this exon (i.e. the splice junctions which pos is located on this exon)
 * connectedSpliceJunctions: The splice junctions found connecting to this exon (i.e. the splice junction pos + distance located on this exon). 
 * 
 * @author carrillo
 *
 */
public class Exon extends BEDentry
{
	private FeatureI info; 
	
	public Exon( final FeatureI info )
	{
		setFeature( info );
		setBEDParameters();
	}
	
	/**
	 * Set the parameters required for the BED entry. 
	 */
	private void setBEDParameters()
	{
		Feature info = (Feature) getInfo(); 
		setChrom( /*"chr" + */info.seqname() );
		setChromStart( info.location().bioStart() );
		setChromEnd( info.location().bioEnd() );
		
		setPos( getChromStart() + (getChromEnd() - getChromStart() )/2 );
		setName( info.getAttribute("exon_id") );
		
		setScore( (int) info.score() );
		setStrand( Character.toString( info.location().bioStrand() ) ); 
		setThickStart( getChromStart() ); 
		setThickEnd( getChromEnd() );
		
		setItemRgb( "0,0,0" );
		setBlockCount( 1 ); 
		final String blockSize = (getChromEnd() - getChromStart()) + ","; 
		setBlockSizes( blockSize );
		setBlockStarts( ( 0 + "," ) ); 
	}
	
	/*
	 * Get gene id. 
	 */
	public String getGeneId()
	{
		return getInfo().getAttribute( "gene_id" ); 
	}
	
	/*
	 * Get transcript id. 
	 */
	public String getTranscriptId() 
	{
		return getInfo().getAttribute( "transcript_id" ); 
	}
	
	//Getter 
	public FeatureI getInfo() { return this.info; } 
	public long getFivePrimePos() {
		if( isPlusStrand() )
			return getChromStart(); 
		else 
			return getChromEnd(); 
	}
	public long getThreePrimePos() {
		if( isPlusStrand() )
			return getChromEnd(); 
		else 
			return getChromStart(); 
	}
	//Setter 
	private void setFeature( final FeatureI info ) { this.info = info; } 
}
