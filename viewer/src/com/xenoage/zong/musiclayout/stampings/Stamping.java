package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.util.math.Shape;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.renderer.RenderingParams;


/**
 * Class for an stamping. Stampings can
 * be visible objects like notes, clefs, texts, but
 * also invisible objects like empty rooms between
 * staves and so on are possible.
 * 
 * Stamps were used in the early days of music notation to
 * paint the symbols. This class is called stamping, because it
 * is the result of placing a stamp, that means, in most cases,
 * a given symbol at a given position. 
 * 
 * Stampings can be painted of course. Each stamping
 * can delegate the painting to another class, e.g.
 * special renderers for this stamping.
 *
 * @author Andreas Wenger
 */
public abstract class Stamping
{
  
  //level
	public enum Level
	{
		/** empty space */
		EmptySpace,
		/** staff */
		Staff,
		/** notes, barlines, ... */
		Music,
		/** text, dynamic symbols, ... */
		Text;
	}
  private final Level level;
  
  //parent staff stamping
  protected final StaffStamping parentStaff;
  
  //the musical element for which this stamping was created,
  //or null, if not availabe (e.g. for staves)
  //this may be another element than expected, e.g. an accidental layout
  //element may refer to a chord musical element.
  private final MusicElement musicElement;

  //bounding geometry
  private final Shape boundingShape;
  

  /**
   * Creates a new stamping that belongs to the given
   * staff element. It may also belong to more than only this
   * staff.
   * @param parentStaff    the parent staff stamping
   * @param level          the layer of this stamping
   * @param musicElement   the musical element for which this stamping was created, or null
   * @param boundingShape  the bounding geometry
   */
  public Stamping(StaffStamping parentStaff, Level level, MusicElement musicElement,
  	Shape boundingShape)
  {
    this.level = level;
    this.parentStaff = parentStaff;
    this.musicElement = musicElement;
    this.boundingShape = boundingShape;
  }
  
  
  /**
   * Gets the layer of this stamping.
   */
  public Level getLevel()
  {
    return level;
  }
  
  
  /**
   * Gets the bounding geometry.
   */
  public Shape getBoundingShape()
  {
    return boundingShape;
  }


  /**
   * Gets the parent staff stamping of this staff or null.
   * This is important for the renderer, when it needs some
   * information from the parent staff of this element.
   */
  public StaffStamping getParentStaff()
  {
    return parentStaff;
  }
  
  
  /**
   * Gets the musical element for which this stamping was created, or null.
   */
  public MusicElement getMusicElement()
  {
    return musicElement;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  public abstract void paint(RenderingParams params);
  
  
}
