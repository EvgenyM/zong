package com.xenoage.zong.io.musicxml.in.readers;

import static com.xenoage.pdlib.PMap.pmap;
import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.CollectionUtils.map;
import static com.xenoage.util.Range.range;
import static com.xenoage.util.iterators.MultiIt.multiIt;

import java.util.HashMap;

import com.xenoage.pdlib.PMap;
import com.xenoage.pdlib.PVector;
import com.xenoage.pdlib.Vector;
import com.xenoage.util.ArrayTools;
import com.xenoage.zong.core.music.Part;
import com.xenoage.zong.core.music.StavesList;
import com.xenoage.zong.core.music.group.BarlineGroup;
import com.xenoage.zong.core.music.group.BracketGroup;
import com.xenoage.zong.musicxml.types.MxlAttributes;
import com.xenoage.zong.musicxml.types.MxlGroupBarline;
import com.xenoage.zong.musicxml.types.MxlGroupSymbol;
import com.xenoage.zong.musicxml.types.MxlPartGroup;
import com.xenoage.zong.musicxml.types.MxlPartList;
import com.xenoage.zong.musicxml.types.MxlScorePart;
import com.xenoage.zong.musicxml.types.MxlScorePartwise;
import com.xenoage.zong.musicxml.types.choice.MxlMusicDataContent;
import com.xenoage.zong.musicxml.types.choice.MxlPartListContent;
import com.xenoage.zong.musicxml.types.choice.MxlMusicDataContent.MxlMusicDataContentType;
import com.xenoage.zong.musicxml.types.choice.MxlPartListContent.PartListContentType;
import com.xenoage.zong.musicxml.types.enums.MxlStartStop;
import com.xenoage.zong.musicxml.types.partwise.MxlMeasure;
import com.xenoage.zong.musicxml.types.partwise.MxlPart;


/**
 * This reads an empty {@link StavesList} from the
 * score-part and part-group elements of a partwise MusicXML 2.0 document.
 * 
 * This class also creates a map for matching MusicXML part-IDs
 * and staff indices to the correct application's staff indices.
 *
 * @author Andreas Wenger
 */
public final class StavesListReader
{
	
	public static final class Value
	{
		public final StavesList stavesList;
		public final PMap<String, Integer> partsIDtoIndex;
		
		public Value(StavesList stavesList, PMap<String, Integer> partsIDtoIndex)
		{
			this.stavesList = stavesList;
			this.partsIDtoIndex = partsIDtoIndex;
		}
	}
	
	private static abstract class PartsGroup
	{
		public int startPartIndex = -1, stopPartIndex = -1;
		
		@Override public String toString()
		{
			return "[" + startPartIndex + " to " + stopPartIndex + "]";
		}
	}
	
	private static final class PartsBarlineGroup extends PartsGroup
	{
		public BarlineGroup.Style style = null;
	}
	
	private static final class PartsBracketGroup extends PartsGroup
	{
		public BracketGroup.Style style = null;
	}
	
	private static final class PartsGroups
	{
		public PartsBarlineGroup barlineGroup = null;
		public PartsBracketGroup bracketsGroup = null;
	}
  
  
  /**
   * Creates a {@link StavesList} from the given partwise MusicXML 2.0 document.
   */
  public static Value read(MxlScorePartwise mxlScore)
  {
  	//list of parts
  	PVector<Part> parts = pvec();
  	PMap<String, Integer> partsIDtoIndex = pmap();
  	//list of groups
  	PVector<PartsBarlineGroup> barlineGroups = pvec();
  	PVector<PartsBracketGroup> bracketGroups = pvec();
    //open groups with number as index
  	PartsBarlineGroup[] openBarlineGroups = new PartsBarlineGroup[6];
  	PartsBracketGroup[] openBracketGroups = new PartsBracketGroup[6];
    //read score-part and part-group elements
    //each score-part is a part in our application
    MxlPartList mxlPartList = mxlScore.getScoreHeader().getPartList();
    int currentPartIndex = 0;
    for (MxlPartListContent mxlItem : mxlPartList.getContent())
    {
      //score-part
    	if (mxlItem.getPartListContentType() == PartListContentType.ScorePart)
      {
    		MxlScorePart mxlPart = (MxlScorePart) mxlItem;
        Part part = PartReader.readPart(mxlPart);
        parts = parts.plus(part);
        partsIDtoIndex = partsIDtoIndex.plus(mxlPart.getID(), currentPartIndex);
        currentPartIndex++;
      }
      //part-group
      else if (mxlItem.getPartListContentType() == PartListContentType.PartGroup)
      {
        PartsGroups group = readPartGroup(currentPartIndex,
        	(MxlPartGroup) mxlItem, openBarlineGroups, openBracketGroups);
        if (group != null)
        {
        	//a group was closed, add it
        	barlineGroups = barlineGroups.plusNotNull(group.barlineGroup);
        	bracketGroups = bracketGroups.plusNotNull(group.bracketsGroup);
        }
      }
    }
    //if there are unclosed score-groups, throw an exception
    if (!ArrayTools.containsOnlyNull(openBarlineGroups) ||
    	!ArrayTools.containsOnlyNull(openBracketGroups))
    {
      throw new RuntimeException("There are unclosed score-groups");
    }
    //count the number of staves and measures used by each part
    HashMap<String, Integer> partsStaves = countStaves(mxlScore);
    for (String partID : partsStaves.keySet())
    {
    	Integer partIndex = partsIDtoIndex.get(partID);
    	if (partIndex == null)
    		throw new RuntimeException("Unknown part \"" + partID + "\"");
    	Integer partStaves = partsStaves.get(partID);
    	if (partStaves == null)
    		throw new RuntimeException("Unused part \"" + partID + "\"");
    	if (partStaves > 1)
    		parts = parts.with(partIndex, parts.get(partIndex).withStavesCount(partStaves));
    }
    //creates the final StavesList for this document
    StavesList stavesList = createStavesList(parts, barlineGroups, bracketGroups);
    //return staves list and index mapping
    return new Value(stavesList, partsIDtoIndex);
  }


  /**
   * Reads a part-group element.
   * If a group was closed, it is returned. If a group was opened,
   * null is returned. While MusicXML groups can be combined barline and bracket groups,
   * these are separated values in Zong!. This is why they are returned as a tuple
   * (with null if not set).
   */
  private static PartsGroups readPartGroup(
  	int currentPartIndex, MxlPartGroup mxlPartGroup,
  	PartsBarlineGroup[] openBarlineGroups, PartsBracketGroup[] openBracketGroups)
  {
  	int number = mxlPartGroup.getNumber();
    MxlStartStop type = mxlPartGroup.getType();
    if (type == MxlStartStop.Start)
    {
      //group begins here
    	if (openBarlineGroups[number] != null || openBracketGroups[number] != null)
      {
        throw new RuntimeException("score-group \"" + number +
          "\" was already opened");
      }
      //read group-barline and group-symbol (bracket)
      BarlineGroup.Style barlineStyle = readBarlineGroupStyle(mxlPartGroup.getGroupBarline());
      if (barlineStyle != null)
      {
      	openBarlineGroups[number] = new PartsBarlineGroup();
      	openBarlineGroups[number].startPartIndex = currentPartIndex;
      	openBarlineGroups[number].style = barlineStyle;
      }
      BracketGroup.Style bracketStyle = readBracketGroupStyle(mxlPartGroup.getGroupSymbol());
      if (bracketStyle != null)
      {
      	openBracketGroups[number] = new PartsBracketGroup();
      	openBracketGroups[number].startPartIndex = currentPartIndex;
      	openBracketGroups[number].style = bracketStyle;
      }
      return null;
    }
    else if (type == MxlStartStop.Stop)
    {
      //group ends here
    	if (openBarlineGroups[number] == null && openBracketGroups[number] == null)
      {
        throw new RuntimeException(
          "score-group \"" + number + "\" was closed before it was opened");
      }
    	//close open barline group and/or bracket group
    	PartsBarlineGroup closedBarlineGroup = null;
    	if (openBarlineGroups[number] != null)
    	{
    		closedBarlineGroup = openBarlineGroups[number];
    		openBarlineGroups[number] = null;
    		closedBarlineGroup.stopPartIndex = currentPartIndex - 1;
    	}
    	PartsBracketGroup closedBracketGroup = null;
    	if (openBracketGroups[number] != null)
    	{
    		closedBracketGroup = openBracketGroups[number];
    		openBracketGroups[number] = null;
    		closedBracketGroup.stopPartIndex = currentPartIndex - 1;
    	}
    	PartsGroups ret = new PartsGroups();
    	ret.barlineGroup = closedBarlineGroup;
    	ret.bracketsGroup = closedBracketGroup;
      return ret;
    }
    return null;
  }
  
  
  /**
   * Counts the number of staves used in each part and returns them.
   * @return a hashmap which maps a part ID to the number of staves in this part
   */
  private static HashMap<String, Integer> countStaves(MxlScorePartwise mxlScore)
  {
  	HashMap<String, Integer> ret = map();
    //check all parts
    for (MxlPart mxlPart : mxlScore.getParts())
    {
      String id = mxlPart.getID();
      //heck all measures for attributes with staves-element and store the greatest value
      int maxStaves = 1;
      for (MxlMeasure mxlMeasure : mxlPart.getMeasures())
      {
        for (MxlMusicDataContent content : mxlMeasure.getMusicData().getContent())
        {
        	if (content.getMusicDataContentType() == MxlMusicDataContentType.Attributes)
        	{
	          Integer xmlStaves = ((MxlAttributes) content).getStaves();
	          if (xmlStaves != null)
	          {
	            maxStaves = Math.max(maxStaves, xmlStaves);
	          }
        	}
        }
      }
      //set the number of staves of the part
      ret.put(id, maxStaves);
    }
    return ret;
  }
  
  
  /**
   * Creates the (still empty) {@link StavesList} for this document.
   */
  private static StavesList createStavesList(Vector<Part> parts,
  	Vector<PartsBarlineGroup> barlineGroups, Vector<PartsBracketGroup> bracketGroups)
  {
  	StavesList ret = StavesList.empty();
    //add parts
    for (Part part : parts)
    {
      ret = ret.plusPart(part, 0);
    }
    //add groups
    for (PartsBarlineGroup barlineGroup : barlineGroups)
    {
      int startIndex = getFirstStaffIndex(barlineGroup.startPartIndex, parts);
      int endIndex = getLastStaffIndex(barlineGroup.stopPartIndex, parts);
      ret = ret.plusBarlineGroup(startIndex, endIndex, barlineGroup.style);
    }
    for (PartsBracketGroup bracketGroup : bracketGroups)
    {
      int startIndex = getFirstStaffIndex(bracketGroup.startPartIndex, parts);
      int endIndex = getLastStaffIndex(bracketGroup.stopPartIndex, parts);
      ret = ret.plusBracketGroup(startIndex, endIndex, bracketGroup.style);
    }
    //add implicit brace- and barline-groups for ungrouped
    //parts with more than one staff
    for (int i : range(parts))
    {
      if (parts.get(i).getStavesCount() > 1 && !isPartInGroup(i, barlineGroups, bracketGroups))
      {
        int startIndex = getFirstStaffIndex(i, parts);
        int endIndex = getLastStaffIndex(i, parts);
        ret = ret.plusBarlineGroup(startIndex, endIndex, BarlineGroup.Style.Common);
        ret = ret.plusBracketGroup(startIndex, endIndex, BracketGroup.Style.Brace);
      }
    }
    return ret;
  }
  
  
  /**
   * Gets the index of the first staff of the given part.
   */
  private static int getFirstStaffIndex(int partIndex, Vector<Part> parts)
  {
    int ret = 0;
    for (int i : range(partIndex))
      ret += parts.get(i).getStavesCount();
    return ret;
  }
  
  
  /**
   * Gets the index of the last staff of the given part.
   */
  private static int getLastStaffIndex(int partIndex, Vector<Part> parts)
  {
    return getFirstStaffIndex(partIndex, parts) +
    	parts.get(partIndex).getStavesCount() - 1;
  }
  
  
  /**
   * Returns true, if the part with the given index
   * is in at least one barline- or bracket-group.
   */
  private static boolean isPartInGroup(int partIndex,
  	Vector<PartsBarlineGroup> barlineGroups, Vector<PartsBracketGroup> bracketGroups)
  {
  	for (Object o : multiIt(barlineGroups, bracketGroups))
    {
  		PartsGroup group = (PartsGroup) o;
      if (group.startPartIndex >= partIndex && group.stopPartIndex <= partIndex)
        return true;
    }
    return false;
  }

  
  private static BracketGroup.Style readBracketGroupStyle(MxlGroupSymbol mxlGroupSymbol)
  {
  	if (mxlGroupSymbol != null)
  	{
	  	switch (mxlGroupSymbol.getValue())
	  	{
	  		case Brace: return BracketGroup.Style.Brace;
	  		case Bracket: return BracketGroup.Style.Bracket;
	  	}
  	}
  	return null;
  }
  
  
  private static BarlineGroup.Style readBarlineGroupStyle(MxlGroupBarline mxlGroupBarline)
  {
  	if (mxlGroupBarline != null)
  	{
	  	switch (mxlGroupBarline.getValue())
	  	{
	  		case Yes: return BarlineGroup.Style.Common;
	  		case No: return BarlineGroup.Style.Single;
	  		case Mensurstrich: return BarlineGroup.Style.Mensurstrich;
	  	}
  	}
  	return null;
  }


}
