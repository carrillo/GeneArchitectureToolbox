package gffFiles;

import java.util.Comparator;

public class ExonPosComparator implements Comparator<Exon> 
{

	public int compare(Exon arg0, Exon arg1) 
	{
		if( arg0.getPos() > arg1.getPos() )
			return 1; 
		else if( arg0.getPos() < arg1.getPos() )
			return -1; 
		else
			return 0;
	}

}
