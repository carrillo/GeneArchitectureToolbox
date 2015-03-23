package geneArchitectureToolbox;

import java.io.File;
import java.io.IOException;

import gffFiles.GFFParser;

public class GetIntronsFromGFF {

	public static void main(String[] args) throws IOException {
		if( args.length != 1 )
		{
			final String info = "\n#######################\n" + 
					"java -jar getExonsFromGFF path_to_gff_file\n" +
					"#######################\n" + 
					"Extracts all exons from the input GFF file (path_to_gff_file).\n" +
					"Returns a GFF entry for each intron.\n" +
					"The result is printed to STDOUT. Store to file by redirecting (java -jar getExonsFromGFF.jar path_to_gff_file > outputFile.gff).\n\n";
			System.err.println( info ); 
		}
		else 
		{	
			final File gff = new File(args[0]);
			GFFParser gffParser = new GFFParser(); 
			gffParser.initiate(gff);
			gffParser.streamGFF3( gffParser.getExons(), System.out );
		}
	}

}
