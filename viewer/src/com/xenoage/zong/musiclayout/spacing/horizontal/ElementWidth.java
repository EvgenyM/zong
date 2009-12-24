package com.xenoage.zong.musiclayout.spacing.horizontal;


/**
 * This class stores the width of a MusicElement,
 * that consists of a front gap, the symbol's width, a rear gap
 * and the lyric's width. All units are measured in
 * interline spaces.
 * 
 * The front gap is the gap for example needed for
 * accidentals and left-suspended notes.
 * 
 * The symbol's width is the width of the symbol
 * of the MusicElement. If it is for example a
 * chord with right-suspended notes, the width
 * is about two times the width of a single notehead.
 * If it is a chord with left-suspended notes,
 * the width is the same as the width of a single
 * notehead, because the left-suspended note was
 * already used for the front gap.
 * Also the dots of a chord belong to the
 * symbol's width.
 * 
 * The rear gap is the empty space behind the
 * symbol. Chords and rests use this gap to
 * illustrate their duration.
 * 
 * The lyrics width is the amount of horizontal space
 * in interline spaces, which a lyric element needs
 * to be displayed (or, if there are more verses, its
 * the width of the widest of these elements).
 *
 * @author Andreas Wenger
 */
public final class ElementWidth
{
  
  private final float frontGap;
  private final float symbolWidth;
  private final float rearGap;
  private final float lyricWidth;
  

  /**
   * Creates a new {@link ElementWidth}.
   */
  public ElementWidth(float frontGap, float symbolWidth, float rearGap, float lyricWidth)
  {
    this.frontGap = frontGap;
    this.symbolWidth = symbolWidth;
    this.rearGap = rearGap;
    this.lyricWidth = lyricWidth;
  }
  
  
  /**
   * Creates a new {@link ElementWidth}, which only has a
   * symbol width but no front and rear gap and no lyric.
   */
  public ElementWidth(float symbolWidth)
  {
  	this(0, symbolWidth, 0, 0);
  }
  
  
  /**
   * Creates a new {@link ElementWidth}, which has no lyric.
   */
  public ElementWidth(float frontGap, float symbolWidth, float rearGap)
  {
  	this(frontGap, symbolWidth, rearGap, 0);
  }
   
  
	public float getFrontGap()
	{
		return frontGap;
	}

	
	public float getSymbolWidth()
	{
		return symbolWidth;
	}

	
	public float getRearGap()
	{
		return rearGap;
	}

	
	/**
	 * Gets the width of the lyric, or 0 if there is none.
	 */
	public float getLyricWidth()
	{
		return lyricWidth;
	}


	/**
	 * Gets the overall width. This is the front gap plus the symbol's width
	 * plus the rear gap.
	 * Notice, that the lyric's width may be greater than this value.
	 */
	public float getWidth()
  {
    return frontGap + symbolWidth + rearGap;
  }
	

}
