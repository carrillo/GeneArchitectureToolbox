package gffFiles;

import net.imglib2.Localizable;

public class ChromosomalFeature implements Localizable 
{
	protected String chrom;
	protected long pos; 
	
	@Override
	public int getIntPosition(int arg0) 
	{
		return (int) getLongPosition( arg0 );
	}

	@Override
	public long getLongPosition(int arg0) 
	{
		return getPos();
	}

	@Override
	public void localize(int[] arg0) 
	{
		arg0[ 0 ] = getIntPosition( 0 ); 
	}

	@Override
	public void localize(long[] arg0) 
	{
		arg0[ 0 ] = getLongPosition( 0 );	
	}

	@Override
	public double getDoublePosition(int arg0)
	{
		return getLongPosition( arg0 );
	}

	@Override
	public float getFloatPosition(int arg0) 
	{
		return getLongPosition( arg0 );
	}

	@Override
	public void localize(float[] arg0) 
	{
		arg0[ 0 ] = getLongPosition( 0 ); 
	}

	@Override
	public void localize(double[] arg0) 
	{
		arg0[ 0 ] = getLongPosition( 0 );
	}

	@Override
	public int numDimensions() 
	{
		return 1;
	}
	
	public String getChrom() { return chrom; }
	public void setChrom(String chrID) { this.chrom = chrID; }
	
	public void setPos( final long pos ) { this.pos = pos; }  
	public long getPos() { return pos; } 
}
