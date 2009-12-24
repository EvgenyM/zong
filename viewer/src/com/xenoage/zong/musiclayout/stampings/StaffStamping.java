package com.xenoage.zong.musiclayout.stampings;

import com.xenoage.util.math.Fraction;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Rectangle2f;
import com.xenoage.util.math.Size2f;
import com.xenoage.zong.data.ScorePosition;
import com.xenoage.zong.musiclayout.StaffMarks;
import com.xenoage.zong.renderer.RenderingParams;
import com.xenoage.zong.renderer.screen.StaffStampingScreenInfo;
import com.xenoage.zong.renderer.stampings.StaffStampingRenderer;


/**
 * Class for a staff stamping.
 *
 * @author Andreas Wenger
 */
public class StaffStamping
  extends Stamping
{
  
	//general information about the staff
	private Point2f position;
	private float length;
	private float interlineSpace;
	private int linesCount;
	
	//musical position marks, e.g. to convert layout coordinates to musical positions
  private StaffMarks staffMarks = null;
  
  //cached information for screen display
  private StaffStampingScreenInfo screenInfo;
  
  
  /**
   * TIDY
   * @param systemIndex  system index relative to frame
   * @param staffIndex  global staff index
   * @param startMeasureIndex
   * @param endMeasureIndex
   * @param position        left border, top line
   * @param length
   * @param linesCount
   * @param interlineSpace
   */
  public StaffStamping(int systemIndex, int staffIndex, int startMeasureIndex, int endMeasureIndex,
    Point2f position, float length, int linesCount, float interlineSpace)
  {
    super(Stamping.LEVEL_STAFF, null);
    
    this.position = position;
    this.length = length;
    this.linesCount = linesCount;
    this.interlineSpace = interlineSpace;
    //create bounding rectangle
    Rectangle2f bounds = new Rectangle2f(position,
    	new Size2f(length, (linesCount - 1) * interlineSpace /*TODO: line width! */));
    addBoundingShape(bounds);
    
    //create cache for screen display information
    screenInfo = new StaffStampingScreenInfo(this);
  }

  
  /**
   * Gets positioning information about this staff stamping,
   * or null, if unknown
   */
  public StaffMarks getStaffMarks()
  {
  	return staffMarks;
  }
  
  
  /**
   * Sets positioning information about this staff stamping.
   */
  public void setStaffMarks(StaffMarks staffMarks)
  {
  	this.staffMarks = staffMarks;
  }
  
  
  public Point2f getPosition()
  {
    return position;
  }
  
  
  public float getLength()
  {
    return length;
  }
  
  
  public int getLinesCount()
  {
    return linesCount;
  }

  
  public float getInterlineSpace()
  {
    return interlineSpace;
  }
  
  
  /**
   * Paints this stamping using the given
   * rendering parameters.
   */
  @Override public void paint(RenderingParams params)
  {
    StaffStampingRenderer.paint(this, params);
  }
  
  
  /**
   * Gets the width of the line in mm.
   */
  public float getLineWidth() //TODO: everything in interline spaces
  {
    return interlineSpace / 8f; //TODO: allow custom values
  }
  
  
  /**
   * Gets cached information about the staff
   * for screen display.
   */
  public StaffStampingScreenInfo getScreenInfo()
  {
  	return screenInfo;
  }
  
  
  /**
   * Computes and returns the y-coordinate of an object
   * on the given line position in mm.
   * Also non-integer values (fractions of interline spaces)
   * are allowed.
   */
  public float computeYMm(float lp)
  {
    return position.y + (linesCount - 1) * interlineSpace -
      lp * interlineSpace / 2 + getLineWidth() / 2; 
  }
  
  
  /**
   * Computes and returns the y-coordinate of an object
   * at the given vertical position in mm as a line position.
   * Also non-integer values are allowed.
   */
  public float computeYLP(float mm)
  {
    return (position.y + (linesCount - 1) * interlineSpace +
    	getLineWidth() / 2 - mm) * 2 / interlineSpace;
  }
  
  
  /**
   * Gets the start position in mm of the measure with the given global index,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public float getMeasureStartMm(int measureIndex)
  {
  	ensureStaffMarksSet();
  	return staffMarks.getMeasureMarksAt(measureIndex).getStartMm();
  }
  
  
  /**
   * Gets the end position in mm of the leading spacing of the measure with the given global index,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public float getMeasureLeadingMm(int measureIndex)
  {
  	ensureStaffMarksSet();
  	return staffMarks.getMeasureMarksAt(measureIndex).getLeadingMm();
  }
  
  
  /**
   * Gets the end position in mm of the measure with the given global index,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public float getMeasureEndMm(int measureIndex)
  {
  	ensureStaffMarksSet();
  	return staffMarks.getMeasureMarksAt(measureIndex).getEndMm();
  }
  
  
  /**
   * Gets the global index of the first measure in this staff,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public int getStartMeasureIndex()
	{
  	ensureStaffMarksSet();
  	return staffMarks.getStartMeasureIndex();
	}


  /**
   * Gets the global index of the last measure in this staff,
   * or throws an {@link IllegalStateException} if positions are unknown.
   */
  public int getEndMeasureIndex()
	{
  	ensureStaffMarksSet();
  	return staffMarks.getEndMeasureIndex();
	}


	/**
	 * See {@link StaffMarks#getScorePositionAt(float)}.
   * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public ScorePosition getScorePositionAtX(float positionX)
	{
		ensureStaffMarksSet();
	  return staffMarks.getScorePositionAt(positionX);
	}


	/**
	 * See {@link StaffMarks#getXMmAt(int, Fraction)}.
	 * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public Float getXMmAt(int measureIndex, Fraction beat)
	{
		ensureStaffMarksSet();
	  return staffMarks.getXMmAt(measureIndex, beat);
	}
	
	
	/**
	 * See {@link StaffMarks#getXMmAt(int, Fraction)}.
	 * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public Float getXMmAt(ScorePosition sp)
	{
		ensureStaffMarksSet();
	  return staffMarks.getXMmAt(sp.getMeasure(), sp.getBeat());
	}
  

	/**
	 * Gets the system index of this staff element, relative to its parent frame.
	 * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public int getSystemIndex()
	{
		ensureStaffMarksSet();
	  return staffMarks.getSystemIndex();
	}


	/**
	 * Gets the scorewide staff index of this staff element.
	 * Throws an {@link IllegalStateException} if positions are unknown.
	 */
	public int getStaffIndex()
	{
		ensureStaffMarksSet();
	  return staffMarks.getStaffIndex();
	}
	
	
	private void ensureStaffMarksSet()
	{
		if (staffMarks == null)
			throw new IllegalStateException("StaffMarks unknown");
	}
  
  
}
