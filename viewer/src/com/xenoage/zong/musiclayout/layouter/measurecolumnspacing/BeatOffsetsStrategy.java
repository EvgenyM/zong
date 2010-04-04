package com.xenoage.zong.musiclayout.layouter.measurecolumnspacing;

import static com.xenoage.util.math.Fraction.fr;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.SortedList;
import com.xenoage.util.iterators.It;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.MusicElement;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.VoiceElement;
import com.xenoage.zong.core.music.util.Column;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.cache.VoiceSpacingsCache;
import com.xenoage.zong.musiclayout.spacing.horizontal.BeatOffset;
import com.xenoage.zong.musiclayout.spacing.horizontal.SpacingElement;
import com.xenoage.zong.musiclayout.spacing.horizontal.VoiceSpacing;


/**
 * This strategy creates {@link BeatOffset}s for the given
 * measure column.
 * 
 * A {@link BeatOffset} is created for each used beat.
 * 
 * @author Andreas Wenger
 */
public class BeatOffsetsStrategy
	implements ScoreLayouterStrategy
{
	
	float guaranteedMinimalDistance = 1.5f; //TODO: config
	
	
	/**
   * Computes the offsets of the all used beats, including
   * at least beat 0 and the beat at the end of the measure.
   * The beats containing the notes with the lowest valuation
   * (or that needs accidentals) dictate the spacing.
   * See "Ross: The Art of Music Engraving", page 79.
   */
  public PVector<BeatOffset> computeBeatOffsets(
  	Column measureColumn, VoiceSpacingsCache voiceSpacings, Fraction measureBeats)
  {
  	//collect voices
  	LinkedList<VoiceSpacing> vss = new LinkedList<VoiceSpacing>();
  	It<Measure> measures = new It<Measure>(measureColumn);
  	for (Measure measure : measures)
  	{
  		It<Voice> voices = new It<Voice>(measure.getVoices());
  		for (Voice voice : voices)
  		{
  			VoiceSpacing vs = voiceSpacings.get(voice);
  			if (vs == null)
  				throw new IllegalStateException("VoiceSpacing for voice at (measure " +
  					measures.getIndex() + ", voice " + voices.getIndex() + ") is missing!");
  			vss.add(vs);
  		}
  	}
  	//compute voice spacings
  	return computeBeatOffsetsFromVoiceSpacings(vss, measureBeats);
  }
	
	
  PVector<BeatOffset> computeBeatOffsetsFromVoiceSpacings(
  	LinkedList<VoiceSpacing> voiceSpacings, Fraction measureBeats)
  {
  	
    //the list of all used beats of the measure
    SortedList<Fraction> beats = computeVoicesBeats(voiceSpacings);
    beats.add(measureBeats); //add final beat

    //the resulting offsets for each used beat
    LinkedList<BeatOffset> ret = new LinkedList<BeatOffset>();
    
    //compute the offset of beat 0
    float offsetMm = getOffsetBeat0InMm(voiceSpacings);
    Fraction lastBeat = Fraction._0;
    ret.add(new BeatOffset(lastBeat, offsetMm));
    
    //if there is only one voice, it's easy to compute the offsets.
    //Otherwise we must find the dominant parts within the voices
    if (voiceSpacings.size() == 1)
    {
      //only one voice
    	float interlineSpace = voiceSpacings.getFirst().getInterlineSpace();
      for (SpacingElement se : voiceSpacings.getFirst().getSpacingElements())
      {
      	//if last beatoffset has same beat, overwrite it
      	if (ret.getLast().getBeat().equals(se.getBeat()))
      		ret.removeLast();
        ret.add(new BeatOffset(se.getBeat(), se.getOffset() * interlineSpace));
      }
    }
    else
    {
      //more than one voice
    	
    	//use the following algorithm:
    	//for each beat, compute the offset, by asking each voice how much space
    	//it requires between the last computed beat offset and the current one.
    	//each time, take the greatest distance required.
    	Iterator<Fraction> beatsIterator = beats.iterator();
    	beatsIterator.next(); //ignore beat 0, we have handled it before
    	while (beatsIterator.hasNext())
      {
    		Fraction beat = beatsIterator.next();
    		
        //find dominating voice and its minimal required distance
    		float minimalDistance = 0;
    		for (VoiceSpacing voiceSpacing : voiceSpacings)
    		{
    			float interlineSpace = voiceSpacing.getInterlineSpace();
    			float voiceMinimalDistance = computeMinimalDistance(lastBeat, beat, beat.equals(measureBeats),
    				voiceSpacing.getVoice(), voiceSpacing.getSpacingElements(), ret, interlineSpace);
    			minimalDistance = Math.max(minimalDistance, voiceMinimalDistance);
    			
    			//a minimal distance of 0 is possible, see "BeatOffsetsStrategyTest-3.xml" for an example.
      		//but we do not want to have different beats at the same offset, so add a small distance.
      		if (minimalDistance < guaranteedMinimalDistance * interlineSpace)
      		{
      			minimalDistance = guaranteedMinimalDistance * interlineSpace;
      		}
    		}
    		
    		//add beat
    		offsetMm += minimalDistance;
    		ret.add(new BeatOffset(beat, offsetMm));
    		
    		lastBeat = beat;
    	}
    	
    }
    
    //TEST
    //for (BeatOffset b : ret) System.out.println(b.getBeat() + ": " + b.getOffsetMm());
    
    return new PVector<BeatOffset>(ret);
  }
  
  
  /**
   * Returns a sorted list of all beats, where
   * chords or rests begin, from the given list of voice spacings.
   * There are no duplicate beats. The ending beats of the voices are not added.
   */
  SortedList<Fraction> computeVoicesBeats(LinkedList<VoiceSpacing> voiceSpacings)
  {
  	SortedList<Fraction> beats = new SortedList<Fraction>(false); //no duplicates
    Fraction beat;
    for (VoiceSpacing voiceSpacing : voiceSpacings)
    {
      beat = Fraction._0;
      for (SpacingElement spacingElement : voiceSpacing.getSpacingElements())
      {
      	MusicElement element = spacingElement.getElement();
      	if (element != null && element instanceof VoiceElement)
      	{
	        //add beat
	      	beats.add(beat);
	        //find the next beat
        	beat = beat.add(((VoiceElement)element).getDuration());
      	}
      }
      //do not add beat here, because the ending beat of a incomplete measure
      //is not interesting for computing beat offsets.
    }
    return beats;
  }
  
  
  /**
   * Computes and returns the minimal distance in mm
   * within the given spacing elements of the given voice
   * between the given starting and ending beat (ending beat
   * without its width).
   * 
   * Beats may be multiused. The last element with the given start beat
   * and also the last element of the given end beat are used
   * (because the important offset of a beat is the position of the note
   * or rest, not the position of a clef or key signature).
   * 
   * If both the starting and ending beat are used,
   * computing their minimal distance is simple.
   * 
   * If the ending beat is unused, 0 is returned, since the given
   * voice does not need any space because it has no element to place there.
   * 
   * If the starting beat is unused, we have to compute
   * the distance in the following way:
   * 
   * The following example shows 2 voices:
   * 
   *  #         #         #   ?      { #: there the offsets are already known and given
   * 1/4       1/4       1/4  |
   *                      |   |
   * 1/4       3/8        *  1/8     { this voice is given. *: startBeat is not used  
   *                      |   |
   *            startBeat_|   |_endBeat
   *                       
   * Because startBeat is not used, we compute the distance
   * from the last used beat to the end beat, which is known
   * from the given spacing elements:
   * 
   * 1/4       3/8        *  1/8
   *            |_____________|
   *           distanceToEndBeat
   *         
   * And we subtract distance between the already computed offset of
   * the last used beat and the also already computed offset of
   * the starting beat (both given in the list of beat offsets):
   * 
   * 1/4       3/8        *  1/8
   *            |_________|
   *         distanceToLastUsedBeat
   *         
   * The result is the distance between the starting beat
   * and the ending beat:
   * 
   * 1/4       3/8        *  1/8
   *                      |___|
   *                     return
   *  
   * This value is the minimal distance the given voice needs to
   * place the elements up to the given ending beat.
   * 
   * //LAYOUT-PERFORMANCE (needed 4 of 60 seconds)
   */
  float computeMinimalDistance(Fraction startBeat, Fraction endBeat, boolean endBeatIsMeasureEnd,
    Voice voice, SpacingElement[] spacings, LinkedList<BeatOffset> alreadyComputedBeatOffsets,
    float interlineSpace)
  {
  	//end beat used? (measure end beat is always used)
  	if (endBeatIsMeasureEnd || voice.isBeatUsed(endBeat))
  	{
  		//yes
  		//when measure is incomplete: use last available beat
  		if (endBeatIsMeasureEnd)
  		{
  			endBeat = voice.getLastUsedBeat(endBeat);
  		}
  		float endOffset = getLastOffset(spacings, endBeat) * interlineSpace;
  		//start beat used?
  		if (voice.isBeatUsed(startBeat))
  		{
  			//yes
  			float startOffset = getLastOffset(spacings, startBeat) * interlineSpace;
  			//return the distance between this two beats
  			return endOffset - startOffset;
  		}
  		else
  		{
  			//no, start beat is not used. use the algorithm described above
  			Fraction lastUsedBeat = voice.getLastUsedBeat(startBeat);
  			//get offset of the last used beat in the voice spacing
  			float lastUsedBeatVoiceSpacingOffset = 0;
  			for (SpacingElement spacing : spacings)
  			{
  				if (spacing.getBeat().equals(lastUsedBeat))
  				{
  					lastUsedBeatVoiceSpacingOffset = spacing.getOffset() * interlineSpace;
  					break;
  				}
  			}
  			//compute minimal distance from last used beat to end beat
  			float distanceToEndBeat = endOffset - lastUsedBeatVoiceSpacingOffset;
  			//get offset of the last computed beat from the list of already computed beat offsets
  			float lastComputedBeatOffset = alreadyComputedBeatOffsets.getLast().getOffsetMm();
  			//get offset of the last used beat from the list of already computed beat offsets
  			float lastUsedBeatBeatOffsetsOffset = 0;
  			for (BeatOffset beatOffset : alreadyComputedBeatOffsets)
  			{
  				if (beatOffset.getBeat().equals(lastUsedBeat))
  				{
  					lastUsedBeatBeatOffsetsOffset = beatOffset.getOffsetMm();
  					break;
  				}
  			}
  			//compute distance between these two offsets
  			float distanceToLastUsedBeat = lastComputedBeatOffset - lastUsedBeatBeatOffsetsOffset;
  			//return the distance between the last computed beat offset and the end beat
  			return distanceToEndBeat - distanceToLastUsedBeat;
  		}
  	}
  	else
  	{
  		//no, end beat is not used
  		//since there is no element, we need no space
  		return 0;
  	}
  }
  
  
  /**
   * Computes and returns the maximum offset of beat 0
   * for the given voice spacings in mm.
   * If no voice uses beat 0 or the list is empty, 0 is returned.
   */
  private float getOffsetBeat0InMm(List<VoiceSpacing> voiceSpacings)
  {
    float maxOffset = 0;
    for (VoiceSpacing voiceSpacing : voiceSpacings)
    {
      SpacingElement[] elements = voiceSpacing.getSpacingElements();
      float offset = getLastOffset(elements, fr(0)) * voiceSpacing.getInterlineSpace();
      if (offset > maxOffset)
        maxOffset = offset;
    }
    return maxOffset;
  }
  
  
  /**
   * Computes and returns the offset of the last
   * occurrence of the given beat in interline spaces.
   * If the beat is not found, 0 is returned.
   */
  private float getLastOffset(SpacingElement[] spacings, Fraction beat)
  {
    for (int i = 0; i < spacings.length; i++)
    {
      //find first occurrence of the beat
      if (spacings[i].getBeat().equals(beat))
      {
        //find last occurrence of the beat
        while (i+1 < spacings.length && spacings[i+1].getBeat().equals(beat))
        {
          i++;
        }
        return spacings[i].getOffset();
      }
    }
    return 0;
  }
  

}
