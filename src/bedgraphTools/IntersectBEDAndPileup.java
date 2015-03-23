package bedgraphTools;

import gffFiles.BEDentry;
import inputOutput.TextFileAccess;
import inputOutput.ZipFileAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import array.tools.IntArrayTools;

public class IntersectBEDAndPileup 
{
	private File pileupFile, bedFile; 
	
	public IntersectBEDAndPileup( final File pileupFile, final File bedFile )
	{
		this.pileupFile = pileupFile; 
		this.bedFile = bedFile; 
	}
	
	private void parsePileup() throws IOException
	{
		BufferedReader in = ZipFileAccess.openGZipFileRead( pileupFile ); 
		
		String[] entries; 
		String currentChr = "empty";
		HashMap<Integer, Integer> posValueHash = new HashMap<Integer, Integer>();
		int count = 0; 
		while( in.ready() )
		{
				
			entries = in.readLine().split( "\t" );
			if( count % 1000000 == 0 )
				System.err.println( "Current chromosome: " + entries[ 0 ] + " position " + entries[ 1 ] ); 
			
			if( currentChr.equals( entries[ 0 ] ) )
			{
				posValueHash.put( Integer.parseInt( entries[ 1 ] ), Integer.parseInt( entries[ 3 ] ) ); 
			}
			else
			{
				if( !currentChr.equals( "empty" ) )
				{
					intersectCurrentChromosome(currentChr, posValueHash );
				}
				
				posValueHash = new HashMap<Integer, Integer>(); 
				currentChr = entries[ 0 ];
			} 
			count++; 
		}
		
		intersectCurrentChromosome( currentChr, posValueHash );
	}
	
	private void intersectCurrentChromosome( final String currentChrId, final HashMap<Integer, Integer> currentChrHash ) throws IOException
	{
		BufferedReader in = TextFileAccess.openFileRead( this.bedFile ); 
		
		BEDentry entry; 
		while( in.ready() )
		{
			entry = new BEDentry( in.readLine() );
			if( entry.getChrom().equals( currentChrId ) )
			{
				intersect( entry, currentChrHash );
			}
			 
		}
		
		in.close(); 
	}
	
	private void intersect( final BEDentry entry, final HashMap<Integer, Integer> posValueHash )
	{
		final int[] values = new int[ entry.getChromEnd() - entry.getChromStart() ];
		int currentValue; 
		for( int i = 0; i < values.length; i++ )
		{
			if( posValueHash.containsKey( entry.getChromStart() + i ) )
			{
				values[ i ] = posValueHash.get( ( entry.getChromStart() + i ) ); 	
			}
		}
		
		System.out.println( entry.getName() + "\t" + IntArrayTools.arrayToString(values, "," ) ); 
	}
	
	public static void main(String[] args) throws IOException
	{
		
		if( args.length < 2 || args.length > 6 )
		{
			final String info = "\n#######################\n" +
					"java -jar intersectBedAndPileup input.pileup input.BED\n" +
					"#######################\n" + 
					"Returns values of pileup defined in bed file.\n" +
					"\n"; 
			System.err.println( info ); 
		}
		else 
		{
			final File pileupFile = new File( args[ 0 ] );
			final File bedFile = new File( args[ 1 ] );
			 
			IntersectBEDAndPileup ibp = new IntersectBEDAndPileup( pileupFile, bedFile );
			ibp.parsePileup(); 
		}
		
		/*
		final File bedFile = new File( "/Users/carrillo/Desktop/temp/extendDown.bed" );
		final File pileupFile = new File( "/Users/carrillo/Desktop/temp/chr10.pileup.gz" );
		
		IntersectBEDAndPileup ibp = new IntersectBEDAndPileup( pileupFile, bedFile );
		ibp.parsePileup();
		*/
	}

}
