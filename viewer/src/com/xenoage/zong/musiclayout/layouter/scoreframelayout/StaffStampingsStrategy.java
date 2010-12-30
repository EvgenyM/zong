package com.xenoage.zong.musiclayout.layouter.scoreframelayout;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.CollectionUtils.alist;
import static com.xenoage.util.CollectionUtils.llist;
import static com.xenoage.util.Range.range;

import java.util.ArrayList;
import java.util.LinkedList;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.math.Fraction;
import com.xenoage.util.math.Point2f;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.musiclayout.BeatOffset;
import com.xenoage.zong.musiclayout.FrameArrangement;
import com.xenoage.zong.musiclayout.MeasureMarks;
import com.xenoage.zong.musiclayout.StaffMarks;
import com.xenoage.zong.musiclayout.SystemArrangement;
import com.xenoage.zong.musiclayout.layouter.scoreframelayout.util.StaffStampings;
import com.xenoage.zong.musiclayout.spacing.ColumnSpacing;
import com.xenoage.zong.musiclayout.stampings.StaffStamping;


/**
 * This strategy creates the staves of all systems of a
 * given {@link FrameArrangement}.
 * 
 * @author Andreas Wenger
 */
public class StaffStampingsStrategy
{
	
	
	public StaffStampings createStaffStampings(Score score, FrameArrangement frameArr)
	{
		int systemsCount = frameArr.getSystems().size();
		int stavesCount = score.getStavesCount();
		LinkedList<StaffStamping> allStaves = llist();
		
		//go through the systems
    for (int iSystem : range(systemsCount))
    { 
    	SystemArrangement system = frameArr.getSystems().get(iSystem);
    	
      //create staves of the system
    	StaffStamping[] systemStaves = new StaffStamping[stavesCount];
    	float systemXOffset = system.getMarginLeft();
      float yOffset = system.getOffsetY();
      for (int iStaff = 0; iStaff < stavesCount; iStaff++)
      {
      	yOffset += system.getStaffDistance(iStaff);
      	StaffStamping staff = new StaffStamping(new Point2f(systemXOffset, yOffset),
      		system.getWidth(), 5, score.getInterlineSpace(iStaff), null); 
      	systemStaves[iStaff] = staff;
        yOffset += system.getStaffHeight(iStaff);
      }
    
    	//create position marks
      PVector<ColumnSpacing> css = system.getColumnSpacings();
      int measuresCount = system.getColumnSpacings().size();
      float[] measureMarkersLeft = new float[measuresCount];
      float[] measureMarkersLeading = new float[measuresCount];
      float[] measureMarkersRight = new float[measuresCount];
      float xOffset = 0; //start at the beginning of the staff
      for (int iMeasure : range(measuresCount))
      {
      	float xLeft = xOffset;
      	float xLeading = xLeft + css.get(iMeasure).getLeadingWidth();
      	xOffset += system.getColumnSpacings().get(iMeasure).getWidth();
      	float xRight = xOffset;
      	//mark measure offset
      	measureMarkersLeft[iMeasure] = xLeft;
      	measureMarkersLeading[iMeasure] = xLeading;
      	measureMarkersRight[iMeasure] = xRight;
      }
      
      //compute beat positions
      for (int iStaff : range(stavesCount))
      {
      	
      	//collect beat offsets
      	xOffset = 0; //start at the beginning of the staff
        ArrayList<PVector<BeatOffset>> staffBeats = alist(measuresCount);
        for (int iMeasure : range(measuresCount))
        {
        	ColumnSpacing cs = css.get(iMeasure);
        	PVector<BeatOffset> measureBeats = pvec();
        	for (Fraction beat : cs.getMeasureSpacings().get(iStaff).getUsedBeats())
        	{
        		BeatOffset bo = cs.getBeatOffset(beat);
        		if (bo == null)
        			throw new IllegalStateException("No offset defined for beat " + beat +
        				" in system " + iSystem + ", staff " + iStaff + ", measure " + iMeasure);
        		bo = bo.withOffsetMm(xOffset + bo.getOffsetMm() + cs.getLeadingWidth());
        		measureBeats = measureBeats.plus(bo);
        	}
        	staffBeats.add(measureBeats);
          xOffset += cs.getWidth();
        }
      
        //create StaffMarks for each staff
      	PVector<MeasureMarks> measureMarks = pvec();
      	for (int iMeasure : range(measuresCount))
      	{
      		PVector<BeatOffset> beatOffsets = staffBeats.get(iMeasure);
      		if (beatOffsets.size() == 0)
      		{
      			throw new IllegalStateException("No beat markers for measure " + iMeasure +
      				" in staff " + iStaff + " in system " + 
      				iSystem + " on frame beginning with measure " + frameArr.getStartMeasureIndex());
      		}
      		else
      		{
      			measureMarks = measureMarks.plus(new MeasureMarks(measureMarkersLeft[iMeasure],
	      			measureMarkersLeading[iMeasure], measureMarkersRight[iMeasure], beatOffsets));
      		}
      	}
      	StaffMarks staffMarks = new StaffMarks(iSystem, iStaff, system.getStartMeasureIndex(), measureMarks);
      	
      	StaffStamping ss = systemStaves[iStaff].withStaffMarks(staffMarks);
      	systemStaves[iStaff] = ss;
      	allStaves.add(ss);
      }
      
    }
		
    return new StaffStampings(allStaves, systemsCount, stavesCount);
	}
	

}
