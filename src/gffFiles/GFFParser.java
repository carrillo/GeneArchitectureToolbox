package gffFiles;

import inputOutput.TextFileAccess;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import org.biojava3.genome.parsers.gff.Feature;
import org.biojava3.genome.parsers.gff.FeatureI;
import org.biojava3.genome.parsers.gff.FeatureList;
import org.biojava3.genome.parsers.gff.GFF3Reader;
import org.biojava3.genome.parsers.gff.Location;

public class GFFParser 
{
	private File gffFile; 
	
	public void initiate( final File gffFile )
	{
		setGffFile( gffFile );
	}
	
	/**
	 * Retrieve all exons. 
	 * 1. Identify exons
	 * 2. Add to ArrayList 
	 * @return
	 * @throws IOException
	 */
	public ArrayList<FeatureI> getExons() throws IOException
	{
		ArrayList<FeatureI> exons = new ArrayList<FeatureI>(); 
		
		FeatureList features = GFF3Reader.read( getGffFile().getAbsolutePath() );

		//Select all exons identified by the type/feature "exon" 
		for( int i = 0; i < features.size(); i++ )
		{
			if( features.get( i ).type().equals("exon") )
			{
				exons.add( features.get( i ) ); 
			} 
		}
		
		return exons; 
	}
	
	
	/**
	 * Remove those exons with the same starting and end locations
	 * @param exons
	 * @return
	 */
	public ArrayList<FeatureI> removeDuplicateExons( final ArrayList<FeatureI> exons )
	{ 
		HashMap<String, FeatureI> exonMap = new HashMap<String, FeatureI>();
		for( FeatureI exon : exons )
		{ 
			final String currentPos = "" +  exon.location().bioStart() + ";" + exon.location().bioEnd(); 
			//System.out.println( currentPos ); 
			exonMap.put( currentPos, exon );   
		}
		
		ArrayList<FeatureI> out = new ArrayList<FeatureI>(); 
		Collection<FeatureI> uniq = exonMap.values(); 
		for( FeatureI f : uniq )
			out.add( f ); 
		
		
		sortFeatureListByPos( out );
		
		for( FeatureI f : out )
			System.out.println( f ); 
		
		return out; 
	}
	
	public void sortFeatureListByPos( final ArrayList<FeatureI> list )
	{
		Comparator<FeatureI> comp = new Comparator<FeatureI>() 
		{
			public int compare(FeatureI o1, FeatureI o2) {
				
				if( o1.location().bioStart() > o2.location().bioStart() )
					return 1;
				else if( o1.location().bioStart() < o2.location().bioStart() )
					return -1;
				else
				{
					if( o1.location().bioEnd() > o2.location().bioEnd() )
						return 1;
					else if( o1.location().bioEnd() < o2.location().bioEnd() )
						return -1;
					else 
						return 0; 
				}
			}
		};
		
		Collections.sort(list, comp );
	}
	
	/**
	 * Extract introns for gff file. 
	 * @throws IOException
	 */
	public ArrayList<FeatureI> getIntrons() throws IOException {
		
		// Collect all exons for each transcript. 
		String transcript; 
		final HashMap<String, ArrayList<FeatureI>> transcripts = new HashMap<String, ArrayList<FeatureI>>(); 
		for( FeatureI exon : getExons() ) {
			transcript = exon.getAttribute("transcript_id"); 
			if( !transcripts.containsKey(transcript ) ) {
				transcripts.put(transcript, new ArrayList<FeatureI>());  
			} 
			transcripts.get(transcript).add( exon ); 
		}
		

		// Extract introns. 
		ArrayList<FeatureI> introns = new ArrayList<FeatureI>(); 
		for( String id : transcripts.keySet() ) { 
			ArrayList<FeatureI> exons = transcripts.get(id); 
			sortFeatureListByPos(exons);
			for( int i = 0; i < exons.size() - 1; i++ ) {
				FeatureI exonUpstream = exons.get( i ); 
				FeatureI exonDownstream = exons.get( i+1 ); 
				
			 
				Location location; 
				if( exonUpstream.location().getBegin() > 0 ) {
					location = new Location( exonUpstream.location().getEnd(), exonDownstream.location().getBegin() );
				} else {
					location = new Location( exonDownstream.location().getBegin(), exonUpstream.location().getEnd() );
				}
				 
				String attributes = "";
				for( String attribute : exonUpstream.getAttributes().keySet() ) {
					if( !attribute.contains("exon") ) {
						attributes += attribute + " " + "\"" + exonUpstream.getAttribute(attribute) + "\"" + ";"; 
					}					 
				}
					
				introns.add(new Feature(exonUpstream.seqname(), exonUpstream.getAttribute("gene_biotype"), "intron", location, 0.0, -1, attributes ) );
			}
		} 
		return introns; 
	}
	
	public void streamGFF3( final ArrayList<FeatureI> features, final PrintStream out) {
		 
		String strand, start, end, line; 
		for( FeatureI f : features ) {
			//System.out.println(f); 
			String[] entries = f.toString().split("\t");  
			
			if( Integer.parseInt(entries[3]) < 0 ) {
				strand = "-"; 
				start = entries[4].substring(1); 
				end = entries[3].substring(1);
			} else {
				strand = "+"; 
				start = entries[3]; 
				end = entries[4]; 
			}
			
			line = entries[0] + "\t" + entries[1] + "\t" + entries[2] + "\t" 
					+ start + "\t" + end + "\t" + "." + "\t" + strand + "\t" + entries[5] + "\t" + entries[7];
			out.println(line); 
		}
		out.flush(); 
		out.close();
	}
	
	//Getter 
	public File getGffFile() { return this.gffFile; }
	
	//Setter
	private void setGffFile( final File gffFile ) { this.gffFile = gffFile; } 
	
	public static void main(String[] args) throws IOException 
	{
		final File gffFile = new File("/Users/carrillo/workspace/AlternativeSplicingAnalysis/resources/zv9genesChr10.gtf");  
		
		GFFParser gffParser = new GFFParser(); 
		gffParser.initiate( gffFile ); 
		gffParser.streamGFF3( gffParser.getIntrons(), System.out );
		
	}

}
