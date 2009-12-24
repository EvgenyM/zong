package com.xenoage.zong.musiclayout.notations;

import com.xenoage.zong.data.music.time.NormalTime;
import com.xenoage.zong.musiclayout.spacing.horizontal.ElementWidth;


/**
 * This class contains layout information
 * about a normal time signature.
 *
 * @author Andreas Wenger
 */
public class NormalTimeNotation
  implements Notation
{
  
  private NormalTime element;
  private ElementWidth width;
  private float numeratorOffset;
  private float denominatorOffset;
  private float digitGap;
  
  
  /**
   * Creates a new NormalTimeElementLayout for the given time signature.
   * @param element            the time signature element
   * @param width              front gap, element width and rear gap
   * @param numeratorOffset    the horizontal offset of the numerator in interline spaces
   * @param denominatorOffset  the horizontal offset of the denominator in interline spaces
   * @param digitGap           the gap between the digits in interline spaces
   */
  public NormalTimeNotation(NormalTime element, ElementWidth width,
    float numeratorOffset, float denominatorOffset, float digitGap)
  {
    this.element = element;
    this.width = width;
    this.numeratorOffset = numeratorOffset;
    this.denominatorOffset = denominatorOffset;
    this.digitGap = digitGap;
  }
  
  
  public ElementWidth getWidth()
  {
    return width;
  }
  
  
  /**
   * Gets the time.
   */
  public NormalTime getMusicElement()
  {
    return element;
  }

  
  public float getNumeratorOffset()
  {
    return numeratorOffset;
  }

  
  public float getDenominatorOffset()
  {
    return denominatorOffset;
  }


  
  public float getDigitGap()
  {
    return digitGap;
  }
  

}
