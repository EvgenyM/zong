package com.xenoage.zong.musiclayout.layouter.beamednotation.alignment;

import static com.xenoage.zong.data.music.StemDirection.Up;

import com.xenoage.util.iterators.It;
import com.xenoage.zong.data.music.Beam;
import com.xenoage.zong.data.music.BeamWaypoint;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.Stem;
import com.xenoage.zong.data.music.StemDirection;
import com.xenoage.zong.musiclayout.layouter.ScoreLayouterStrategy;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.BeamDesign;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.DoubleBeamDesign;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.MultipleBeamDesign;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.SingleBeamDesign;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.TripleBeamDesign;
import com.xenoage.zong.musiclayout.layouter.cache.NotationsCache;
import com.xenoage.zong.musiclayout.notations.ChordNotation;
import com.xenoage.zong.musiclayout.notations.beam.BeamStemAlignments;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.musiclayout.notations.chord.StemAlignment;


/**
 * This is an implementation of a {@link BeamedStemAlignmentNotationsStrategy}
 * which can only compute beams that are all in the same measure and in
 * two adjacent staves.
 * 
 * The strategy is quite simple: The first and the last stem have the default lengths
 * (or the lengths the user has defined). All other stem alignments can not be
 * computed here, since the distance of the staves is still unknown. They have to be
 * computed later, meanwhile they are null.
 * 
 * @author Andreas Wenger
 */
public class SingleMeasureTwoStavesStrategy
	implements ScoreLayouterStrategy
{

	
	/**
	 * This strategy computes the lengths of the stems of the beamed chords.
	 */
	public NotationsCache computeNotations(Beam beam, NotationsCache notations)
	{
		NotesAlignment[] chordNa = new NotesAlignment[beam.getWaypointsCount()];
		int beamlines = beam.getMaxBeamLinesCount();
		int i = 0;
		for (BeamWaypoint waypoint : beam.getWaypoints())
		{
			Chord chord = waypoint.getChord();
			ChordNotation cn = notations.getChord(chord);
			chordNa[i] = cn.getNotesAlignment();
			i++;
		}
		Chord firstChord = beam.getFirstWaypoint().getChord();
		Stem firstStem = firstChord.getStem();
		StemDirection firstStemDirection = notations.getChord(firstChord).getStemDirection();
		Chord lastChord = beam.getLastWaypoint().getChord();
		Stem lastStem = firstChord.getStem();
		StemDirection lastStemDirection = notations.getChord(lastChord).getStemDirection();
				
		BeamStemAlignments bsa = computeStemAlignments(chordNa, beamlines,
			firstStem, lastStem, firstStemDirection, lastStemDirection);
		
		//compute new notations
		NotationsCache ret = new NotationsCache();
		It<BeamWaypoint> waypoints = beam.getWaypoints();
		for (BeamWaypoint waypoint : waypoints)
		{
			Chord chord = waypoint.getChord();
			ChordNotation oldCN = notations.getChord(chord);
			ret.set(oldCN.copy(bsa.getStemAlignments()[waypoints.getIndex()]), oldCN.getMusicElement());
		}
		
		return ret;
	}
	
	
	/**
	 * Computes the vertical positions of the first and the last stem
	 * of the given beam. All other stem endpoints are set to null (which
	 * means unknown).
	 * @param chordNa             the alignments of all chords of the beam
	 * @param beamLinesCount      the number of lines of the beam
	 * @param stemDirection       the direction of the stem
	 * @param firstStem           the stem of the first chord
	 * @param lastStem            the stem of the last chord
	 * @param firstStemDirection  the direction of the first chord
	 * @param lastStemDirection   the direction of the last chord
	 * @return  the alignments of all stems of the given chords                        
	 */
	public BeamStemAlignments computeStemAlignments(NotesAlignment[] chordNa,
		int beamLinesCount, Stem firstStem, Stem lastStem,
		StemDirection firstStemDirection, StemDirection lastStemDirection)
	{
		//get appropriate beam design
		BeamDesign beamDesign;
		switch (beamLinesCount)
		{
			//TIDY: we need only a small subset of the BeamDesign class. extract it?
			case 1:
				beamDesign = new SingleBeamDesign(firstStemDirection, 0);
				break;
			case 2:
				beamDesign = new DoubleBeamDesign(firstStemDirection, 0);
				break;
			case 3:
				beamDesign = new TripleBeamDesign(firstStemDirection, 0);
				break;
			default:
				beamDesign = new MultipleBeamDesign(firstStemDirection, 0, beamLinesCount);
		}

		//compute stem alignments
		int chordsCount = chordNa.length;
		StemAlignment[] stemAlignments = new StemAlignment[chordsCount];
		for (int i = 0; i < chordsCount; i++)
		{
			StemAlignment stemAlignment = null; //unknown
			if (i == 0 || i == chordsCount - 1)
			{
				Stem stem = (i == 0 ? firstStem : lastStem);
				StemDirection stemDirection = (i == 0 ? firstStemDirection : lastStemDirection);
				
				//start LP
				float startLP;
				if (stemDirection == Up)
				{
					startLP = chordNa[i].getLinePositions().getBottom(); 
				}
				else
				{
					startLP = chordNa[i].getLinePositions().getTop(); 
				}
				
				//end LP
				float endLP;
				if (stem.getLength() != null)
				{
					//use user-defined length
					endLP = startLP + stemDirection.getSignum() * 2 * stem.getLength();
				}
				else
				{
					//compute length
					endLP = startLP + stemDirection.getSignum() * 2 * beamDesign.getMinimumStemLength(); 
				}
				
				stemAlignment = new StemAlignment(startLP, endLP);
			}
			stemAlignments[i] = stemAlignment;
		}
		BeamStemAlignments beamstemalignments = new BeamStemAlignments(stemAlignments,
			BeamDesign.BEAMLINE_WIDTH, beamDesign.getDistanceBetweenBeamLines(), beamLinesCount);
		
		return beamstemalignments;
	}
	
	
}
