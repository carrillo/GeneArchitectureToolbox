package geneArchitectureToolbox;

import gffFiles.Exon;
import gffFiles.ExonCollection;
import gffFiles.ExonGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import array.tools.ArrayListIntegerTools;

/**
 * Returns geneId, strand and a list of reported gene ends. 
 * 1. Generate exon collection from gff file. 
 * 2. Group into transcripts. 
 * 3. Identify transcript end. 
 * 4. Report per gene.  
 * @author carrillo
 *
 */
public class GetAnnotatedEnds {
	
	private File gffFile; 
	private ExonCollection exonCollection; 
	
	public GetAnnotatedEnds( final File gffFile )
	{
		setGffFile( gffFile );
	}
	
	/*
	 * Read gff file to exon collection. 
	 */
	public void readInput() throws IOException
	{
		setExonCollection( new ExonCollection( getGffFile() ) );
	}
	
	/*
	 * Analyze gene ends.  
	 * 1. Group exons in transcripts in genes. 
	 * 2. Extract transcript ends. 
	 */
	public void analyze() 
	{
		ArrayList<ArrayList<ExonGroup>> genes = getExonCollection().getGenes();
		String name; 
		String strand;
		String chrId; 
		ArrayList<Integer> ends; 
		for( ArrayList<ExonGroup> gene : genes ) 
		{
			name = getName( gene ); 
			strand = getStrand( gene ); 
			chrId = getChrId( gene ); 
			ends = getAnnotatedEnds( gene ); 
			 
			System.out.println( name + "," + strand + "," + chrId + "," + ArrayListIntegerTools.arrayListToString(ends, ";") ); 
		}
	}
	
	/*
	 * Get all annotated ends. 
	 */
	private ArrayList<Integer> getAnnotatedEnds( final ArrayList<ExonGroup> gene ) 
	{
		ArrayList<Integer> ends = new ArrayList<Integer>(); 
		for( ExonGroup transcript : gene ) 
		{
			ends.add( (int) getLastExon( transcript ).getThreePrimePos() );  
		}
		return ends; 
	}
	
	/*
	 * Get most 3' exon. 
	 * 1. Sort exon group by position. 
	 * 2. Get most 3' exon 
	 * 		a) last exon for + strand genes 
	 * 		b) first exon for - strand genes.    
	 */
	private Exon getLastExon( final ExonGroup transcript ) 
	{
		transcript.sortByPos();
		if( transcript.getExons().get( 0 ).isPlusStrand() ) 
		{
			return( transcript.getExons().get( transcript.getExons().size() - 1 ) ); 
		}
		else 
		{
			return( transcript.getExons().get( 0 ) ); 
		}
	}
	
	/*
	 * Get gene name. 
	 * Assert: all exons in gene should have the same gene id 
	 */
	private String getName( final ArrayList<ExonGroup> gene ) 
	{
		final String name = gene.get( 0 ).getExons().get( 0 ).getGeneId();
		HashSet<String> names = new HashSet<String>(); 
		for( ExonGroup transcript : gene ) 
		{
			for( Exon exon : transcript.getExons() )
			{
				names.add( exon.getGeneId() ); 
			}
		}
		
		assert names.size() == 1 : "More than one gene name. " + name;
		return name; 
	}
	
	/*
	 * Get gene strand. 
	 * Assert: all exons should have same strand  
	 */
	private String getStrand( final ArrayList<ExonGroup> gene ) 
	{
		boolean isPlus =  gene.get( 0 ).getExons().get( 0 ).isPlusStrand();
		boolean allSame = true; 
		for( ExonGroup transcript : gene ) 
		{
			for( Exon exon : transcript.getExons() )
			{
				if( exon.isPlusStrand() != isPlus ) 
				{
					allSame = false; 
					break; 
				}
			}
		}
		
		assert allSame : "Mixed plus and minus exons in one gene. " + getName( gene );
		String strand = "-"; 
		if( isPlus )
		{
			strand = "+"; 
		}
		
		return strand;  
	}
	
	/*
	 * Get chromosome id. 
	 * Assert: all exons should have same chromosome id  
	 */
	private String getChrId( final ArrayList<ExonGroup> gene ) 
	{
		String chrId =  gene.get( 0 ).getExons().get( 0 ).getChrom();
		boolean allSame = true; 
		for( ExonGroup transcript : gene ) 
		{
			for( Exon exon : transcript.getExons() )
			{
				if( !exon.getChrom().equals( chrId ) ) 
				{ 
					allSame = false; 
					break; 
				}
			}
		}
		
	
		
		assert allSame : "Exons have distinct chromosome ids. " + getName( gene );
		 
		
		return chrId;  
	}
	
	//Getter 
	public File getGffFile() { return this.gffFile; }
	public ExonCollection getExonCollection() { return this.exonCollection; }
	
	//Setter 
	private void setGffFile( final File gffFile ) { this.gffFile = gffFile; }
	private void setExonCollection( final ExonCollection exonCollection ) { this.exonCollection = exonCollection; } 

	public static void main(String[] args) throws IOException  
	{
		if( args.length != 1 )
		{
			final String info = "\n#######################\n" +
					"java -jar getAnnotatedEnds.jar input.gtf\n" +
					"#######################\n" + 
					"Returns annotated gene ends for all genes in gtf file to STDOUT.\n" +
					"Groups transcripts per genes and returns a csv containing geneId, strand, chrId and annotated gene end.\n\n";
			System.err.println( info ); 
		}
		else 
		{
			final String fileName = args[ 0 ];
			final File gffFile = new File( fileName );
			GetAnnotatedEnds gae = new GetAnnotatedEnds( gffFile );
			gae.readInput(); 
			gae.analyze();
		}
	}

}
