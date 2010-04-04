package com.xenoage.zong.musiclayout.spacing;

import static com.xenoage.zong.io.score.ScoreController.getInterlineSpace;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.music.MP;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureLeadingSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.MeasureSpacing;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;


/**
 * This class contains the horizontal spacing
 * for one measure column, that means
 * one measure over several staves.
 * 
 * This is a list of the offsets of each multiused
 * beat in the measure, that is beat 0,
 * the beats that are used by more than 1 voice
 * (or by one voice, if it is the only one), and
 * the beat at the end of the measure.
 *
 * The units are measured in mm, since the staves may
 * have different heights, and interline space heights
 * would not be unique.
 *
 * @author Andreas Wenger
 */
public final class MeasureColumnSpacing
{
	
	//for every staff one measure spacing
  private final MeasureSpacing[] measureSpacings;
	
	//the offsets of the relevant beats (notes and rests)
  //and, if needed, of mid-measure barlines (otherwise null)
  private final BeatOffset[] beatOffsets; //TIDY: rename to noteOffsets ?
  private final BeatOffset[] barlineOffsets;
  
  
  //width of the leading space (cached for performance reasons) in mm
  private final float leadingWidth;
  
  private final Score score;
  
  
  /**
   * Creates a new {@link MeasureColumnSpacing}.
   * @param measureSpacings  for every staff one measure spacing
   * @param beatOffsets      the offsets of the relevant beats (notes) in mm
   * @param barlineOffsets   the offsets of the relevant beats (barlines) in mm, where
   *                         at least the position of the start barline and the end barline
   *                         must be given (even if they are invisible).
   */
  public MeasureColumnSpacing(Score score,
  	MeasureSpacing[] measureSpacings, BeatOffset[] beatOffsets, BeatOffset[] barlineOffsets)
  {
  	this.score = score;
  	this.measureSpacings = measureSpacings;
  	this.beatOffsets = beatOffsets;
  	if (barlineOffsets.length < 2)
  		throw new IllegalArgumentException("At least two barline offsets (start and end) must be given");
  	this.barlineOffsets = barlineOffsets;
  	//compute width of the leading and voice space
  	this.leadingWidth = computeLeadingSpacingWidth(score);
  }
  
  
  public Score getScore()
  {
  	return score;
  }
  
  
  /**
   * Gets the width of the measure in mm.
   * This is the width of the leading spacing plus
   * the width of the voices.
   */
  public float getWidth()
  {
    return getLeadingWidth() + getVoicesWidth();
  }


  /**
   * Gets the beat offsets of this measure in mm.
   * 
   * TIDY: don't return array. return element by given index
   */
  public BeatOffset[] getBeatOffsets()
  {
    return beatOffsets;
  }
  
  
  /**
   * Gets the barline offsets of this measure in mm.
   * 
   * TIDY: don't return array. return element by given index
   */
  public BeatOffset[] getBarlineOffsets()
  {
    return barlineOffsets;
  }
  
  
  /**
   * Returns the offset of the given MusicElement in IS, or
   * 0 if not found. The index of the staff and voice must also be
   * given.
   */
  public float getOffset(MusicElement element, int staffIndex, int voiceIndex)
  {
  	MeasureSpacing measure = measureSpacings[staffIndex];
    SpacingElement[] voice = measure.getVoice(voiceIndex).getSpacingElements();
    for (SpacingElement se : voice)
    {
      if (se.getElement() == element)
      {
        return se.getOffset();
      }
    }
    return 0;
  }
  
  
  /**
   * Gets the list of measure spacings (each staff of the
   * measure has its own spacing).
   * 
   * TIDY: not return array, but element by given index
   */
  public MeasureSpacing[] getMeasureSpacings()
  {
    return measureSpacings;
  }

  
  /**
   * Gets the width of the leading spacing in mm.
   * If there is no leading spacing, 1 is returned. TODO
   */
  private float computeLeadingSpacingWidth(Score score)
  {
    float ret = 1; //TODO
    //find the maximum width of the leading spacings
    //of each staff
    for (int i = 0; i < measureSpacings.length; i++)
    {
    	MeasureSpacing measureSpacing = measureSpacings[i];
      MeasureLeadingSpacing leadingSpacing = measureSpacing.getLeadingSpacing();
      if (leadingSpacing != null)
      {
        float width = leadingSpacing.getWidth() *
        	getInterlineSpace(score, MP.atStaff(i));
        if (width > ret)
          ret = width;
      }
    }
    return ret;
  }


	/**
	 * Gets the width of the leading space in mm.
	 */
	public float getLeadingWidth()
	{
		return leadingWidth;
	}


	/**
	 * Gets the width of the voices space in mm.
	 */
	public float getVoicesWidth()
	{
    return barlineOffsets[barlineOffsets.length - 1].getOffsetMm();
	}
	
	
	/**
   * Gets the offset of the given beat (or the offset of the preceding
   * beat, if the given beat is unknown, or 0, if before the first known beat).
   * 
   * UNUSED, thus deprecated
   */
  @Deprecated public float getOffset(Fraction beat)
  {
  	float ret = 0;
  	for (BeatOffset bo : beatOffsets)
  	{
  		if (bo.getBeat().compareTo(beat) <= 0)
  			ret = bo.getOffsetMm();
  	}
  	return ret;
  }
  
  
  /**
   * Gets the offset of mid-measure barline at the given beat (or 0 if unknown).
   */
  public float getBarlineOffset(Fraction beat)
  {
  	for (BeatOffset bo : barlineOffsets)
  	{
  		if (bo.getBeat().compareTo(beat) == 0)
  			return bo.getOffsetMm();
  	}
  	return 0;
  }
  
  
}
