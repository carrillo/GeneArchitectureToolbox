package gffFiles;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.biojava3.genome.parsers.gff.FeatureI;

public class ExonCollection 
{
	private ArrayList<Exon> exons; 
	
	/**
	 * Instantciate ExonCollection from gff file. 
	 * @param gffFile
	 * @throws IOException
	 */
	public ExonCollection( final File gffFile ) throws IOException
	{
		GFFParser parser = new GFFParser(); 
		parser.initiate( gffFile );
		
		ArrayList<Exon> exons = new ArrayList<Exon>(); 
		for( FeatureI f : parser.getExons() )
		{
			exons.add( new Exon( f ) ); 
		}
		setExons( exons );
	}
	
	/**
	 * Removes dublicated entries from the exon list. 
	 * These entries are present due to constitutive exons in multiple isoforms of the same gene. 
	 */
	public void removeDuplicatedEntries() 
	{
		System.out.println( "Removing duplicated entries."); 
		
		final HashSet<String> idHash = new HashSet<String>(); 
		final ArrayList<Exon> newExonList = new ArrayList<Exon>(); 
		for( Exon e : getExons() ) 
		{
			if( !idHash.contains( e.getName() ) ) 
			{
				idHash.add( e.getName() ); 
				newExonList.add( e ); 
			}
		}
		
		System.out.println( "Removing duplicated entries. Done. Removed " + ( getExons().size() - newExonList.size() ) + " entries.\n-----");
		setExons( newExonList );
	}
	
	/**
	 * Groups exons into transcripts and genes.
	 * 1. Group exons into transcripts. 
	 * 2. Group transcripts into genes. 
	 * @return
	 */
	public ArrayList<ArrayList<ExonGroup>> getGenes()
	{	
		HashMap<String, ArrayList<ExonGroup>> geneHash = new HashMap<String, ArrayList<ExonGroup>>();
		String currKey; 
		
		/*
		 * Sort Transcript by gene id. 
		 */
		final class TranscriptComparator implements Comparator<ExonGroup> {
			public int compare(ExonGroup arg0, ExonGroup arg1) {
				final String id0 = arg0.getExons().get( 0 ).getGeneId();
				final String id1 = arg1.getExons().get( 0 ).getGeneId();
				return id0.compareTo( id1 );
			}
		}
		
		ArrayList<ExonGroup> transcripts = getTranscripts(); 
		Collections.sort( transcripts, new TranscriptComparator() );
		
		for( ExonGroup transcript : transcripts )
		{
			currKey = transcript.getExons().get( 0 ).getGeneId();   
			
			if( !geneHash.containsKey( currKey ) )
			{
				geneHash.put(currKey, new ArrayList<ExonGroup>() ); 	
			}
			
			geneHash.get( currKey ).add( transcript ); 
		}
	
		
		final ArrayList<ArrayList<ExonGroup>> genes = new ArrayList<ArrayList<ExonGroup>>( geneHash.values() ); 
		
		return genes; 
	}
	
	/**
	 * Group exons into transcripts. 
	 * @return
	 */
	public ArrayList<ExonGroup> getTranscripts() 
	{
		HashMap<String, ExonGroup> transcriptHash = new HashMap<String, ExonGroup>();
		String currKey; 
		for( Exon exon : getExons() )
		{
			currKey = exon.getTranscriptId();   
			if( !transcriptHash.containsKey( currKey ) )
			{ 
				transcriptHash.put( currKey, new ExonGroup( currKey ) ); 				
			}
			
			transcriptHash.get( currKey ).addExon( exon );
		}
	
		final ArrayList<ExonGroup> transcripts = new ArrayList<ExonGroup>( transcriptHash.values() );
		
		return transcripts; 
	
	}
	
	//Getter 
	public ArrayList<Exon> getExons() { return this.exons; }
	
	//Setter 
	private void setExons( final ArrayList<Exon> exons ) { this.exons = exons; } 
	
	public static void main(String[] args) throws IOException
	{
		final File gffFile = new File("resources/genesChr1.gtf");
		ExonCollection exonColl = new ExonCollection( gffFile ); 
		
		ArrayList<ArrayList<ExonGroup>> genes = exonColl.getGenes();
		HashSet<String> ids = new HashSet<String>(); 
		for( ArrayList<ExonGroup> gene : genes ) {
			ids.add( gene.get( 0 ).getExons().get( 0 ).getGeneId() ); 
		} 
	}
}
