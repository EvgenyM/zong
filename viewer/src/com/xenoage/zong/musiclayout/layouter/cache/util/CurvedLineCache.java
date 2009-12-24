package com.xenoage.zong.musiclayout.layouter.cache.util;

import com.xenoage.util.enums.VSide;
import com.xenoage.zong.data.music.CurvedLine;
import com.xenoage.zong.musiclayout.continued.ContinuedCurvedLine;
import com.xenoage.zong.musiclayout.stampings.NoteheadStamping;


/**
 * This class is used by the layouter to save layouting information about
 * the starting and ending point of a {@link CurvedLine}.
 * 
 * The global index of the stave this curved line belongs to is also saved here.
 *
 * @author Andreas Wenger
 */
public class CurvedLineCache
{
	
	private final ContinuedCurvedLine continuedCurvedLine; //use this class to save information
	private final NoteheadStamping startNotehead; 
	private final float startDistanceIS;
	private final int startSystem;
  
  //set when known
  private NoteheadStamping stopNotehead = null;
  private float stopDistanceIS = 0;
  private int stopSystem = -1;
  
  
  /**
   * Creates a {@link CurvedLineCache} instance for a new slur
   * where the start notehead and system is known.
   */
  public static CurvedLineCache createNew(CurvedLine curvedLine, VSide side,
  	int staffIndex, NoteheadStamping startNotehead, float startDistanceIS, int startSystem)
	{
  	return new CurvedLineCache(
  		new ContinuedCurvedLine(curvedLine, side, staffIndex, 1), //1: TODO
  		startNotehead, startDistanceIS, startSystem);
	}
  
  
  /**
   * Creates a {@link CurvedLineCache} instance for a continued slur.
   */
  public static CurvedLineCache createContinued(ContinuedCurvedLine continuedCurvedLine)
	{
  	return new CurvedLineCache(continuedCurvedLine, null, 0, -1);
	}
  
  
  /**
   * Creates a {@link CurvedLineCache} instance using
   * the given information about the {@link Tie} or {@link Slur} and the
   * {@link NoteheadStamping} of the start note (if known, otherwise null)
   * together with additional distance in IS (e.g. because there are articulations)
   * and index of the start system (if known, otherwise -1).
   */
  private CurvedLineCache(ContinuedCurvedLine continuedCurvedLine,
  	NoteheadStamping startNotehead, float startDistanceIS, int startSystem)
  {
    this.continuedCurvedLine = continuedCurvedLine;
    this.startNotehead = startNotehead;
    this.startDistanceIS = startDistanceIS;
    this.startSystem = startSystem;
  }
  
  
  /**
   * Gets the {@link CurvedLine} instance this
   * data belongs to.
   */
  public CurvedLine getCurvedLine()
  {
    return continuedCurvedLine.getMusicElement();
  }
  
  
  /**
   * Gets the notehead stamping of the start note, or null,
   * if it is unknown, because it is a continued curved line.
   */
  public NoteheadStamping getStartNoteheadStamping()
  {
  	return startNotehead;
  }
  
  
  /**
   * Gets the additional distance from the start notehead stamping,
   * that is e.g. needed when there are articulations.
   */
  public float getStartDistanceIS()
  {
  	return startDistanceIS;
  }
  
  
  /**
   * Gets the system index, where the slur starts. If it is
   * a continued curved line, -1 is returned.
   */
  public int getStartSystem()
  {
  	return startSystem;
  }
  
  
  /**
   * Gets the notehead stamping of the end note, or null,
   * if it was not created up to now.
   */
  public NoteheadStamping getStopNoteheadStamping()
  {
  	return stopNotehead;
  }
  
  
  /**
   * Gets the additional distance from the end notehead stamping,
   * that is e.g. needed when there are articulations.
   */
  public float getStopDistanceIS()
  {
  	return stopDistanceIS;
  }
  
  
  /**
   * Gets the notehead stamping of the end note, or -1,
   * if the stop notehead stamping was not created up to now.
   */
  public int getStopSystem()
  {
  	return stopSystem;
  }
  
  
  /**
   * Sets the notehead stamping of the end note,
   * (the additional distance from the end notehead stamping,
   * that is e.g. needed when there are articulations) and its system index.
   */
  public void setStop(NoteheadStamping stopNotehead, float stopDistanceIS, int stopSystem)
  {
  	this.stopNotehead = stopNotehead;
  	this.stopDistanceIS = stopDistanceIS;
  	this.stopSystem = stopSystem;
  }
  
  
  /**
   * Gets the placement of the slur: above or below.
   * TODO: add more possibilities like S-curved slurs.
   */
  public VSide getSide()
  {
  	return continuedCurvedLine.getSide();
  }


	/**
	 * Gets the global staff index this curved line belongs to.
	 */
	public int getStaffIndex()
	{
		return continuedCurvedLine.getStaffIndex();
	}
	
	
	/**
	 * Gets the {@link ContinuedCurvedLine} of this cache, which can be used
	 * to indicate that a continuation of this curved line is needed.
	 */
	public ContinuedCurvedLine getContinuedCurvedLine()
	{
		return continuedCurvedLine;
	}
	
	
	/**
	 * Returns true, if the start of this slur (notehead and system) is known.
	 */
	public boolean isStartKnown()
	{
		return startSystem != -1;
	}
	
	
	/**
	 * Returns true, if the stop of this slur (notehead and system) is known.
	 */
	public boolean isStopKnown()
	{
		return stopSystem != -1;
	}
  

}
