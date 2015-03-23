package geneArchitectureToolbox;

import gffFiles.BEDentry;
import inputOutput.TextFileAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class ExtendBED 
{
	private BufferedReader in; 
	private int extUp, extDown; 
	private String idAppend; 
	
	
	public ExtendBED( final int extUp, final int extDown, final File fileIn )
	{
		this.extUp = extUp; 
		this.extDown = extDown;
		this.idAppend = "_extended_" + extUp + "upAnd_" + extDown + "down"; 
		this.in = TextFileAccess.openFileRead( fileIn ); 
	}
	
	public void parse() throws IOException 
	{
		BEDentry currentBed = null; 
		while( in.ready() )
		{
			currentBed = new BEDentry( in.readLine() );	 
			System.out.println( extendBed( currentBed ) ); 
		}
	}
	
	/**
	 * Extend BED down and upstream. 
	 * 1. Test if BED entry is plus or minus. 
	 * 2. Add extension to the respective values.
	 * @param in
	 * @return
	 */
	private BEDentry extendBed( final BEDentry in )
	{ 
		if( in.isPlusStrand() )
		{
			in.setChromStart( in.getChromStart() - extUp );
			in.setChromEnd( in.getChromEnd() + extDown );
		}
		else 
		{
			in.setChromStart( in.getChromStart() - extDown );
			in.setChromEnd( in.getChromEnd() + extUp );
		}
		
		
		in.setName( in.getName() + this.idAppend );
		in.setBlockCount( 1 );
		in.setBlockSizes( String.valueOf( in.getChromEnd() - in.getChromStart() ) );
		in.setBlockStarts( "0" );
		 
		return in; 
	}

	public static void main(String[] args) throws IOException
	{
		
		if( args.length != 3 )
		{
			final String info = "\n#######################\n" +
					"java -jar extendBED -rangeUpstream=[Integer] -rangeDownstream=[Integer] input.BED\n" +
					"#######################\n" + 
					"Returns the input bed file extended by number of nt extended up- and/or downstream.\n" +
					"Extended by the number nucleotides upstream of the feature (rangeUpstream) and downstream of the entry (rangeDownstream)\n";  
			System.err.println( info ); 
		}
		else 
		{
			try 
			{ 
				final int rangeUpstream = Integer.parseInt( args[ 0 ].substring( args[ 0 ].indexOf("=") + 1).toLowerCase() ); 
				//System.out.println( rangeUpstream ); 
				
				final int rangeDownstream = Integer.parseInt( args[ 1 ].substring( args[ 1 ].indexOf("=") + 1).toLowerCase() ); 
				//System.out.println( rangeDownstream );
				
				final File in = new File( args[ 2 ] ); 
				if( !in.exists() )
				{
					throw new IOException(); 
				}
				
				final ExtendBED exBed = new ExtendBED( rangeUpstream, rangeDownstream, in );
				exBed.parse(); 
			}
			catch (Exception e) 
			{
				System.err.println( "Please provide the range as an Integer." );
				System.exit( 1 ); 
			}
			
		}
		/*
		final int upExt = 0; 
		final int downExt = 50; 
		final File in = new File( "/Users/carrillo/workspace/AlternativeSplicingAnalysis/output/spliceJunctionSpliceSitePositions.bed" );
		
		
		final ExtendBED exBed = new ExtendBED( upExt, downExt, in );
		exBed.parse();
		 */
	}
}
