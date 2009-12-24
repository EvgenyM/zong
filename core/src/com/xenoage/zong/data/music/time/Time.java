package com.xenoage.zong.data.music.time;

import java.util.ArrayList;

import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.music.NoVoiceElement;


/**
 * Base class for time signatures.
 * 
 * @author Andreas Wenger
 * @author Uli Teschemacher
 */
public abstract class Time
	extends NoVoiceElement
{

	/**
	 * Gets the beats per measure this time signature allows, e.g. (1/1) for a
	 * (4/4) time signature or (3/4) for a (3/4) time signature. null means:
	 * Undefined, so as many beats as needed are allowed.
	 */
	public abstract Fraction getBeatsPerMeasure();


	/**
	 * Gets the beats that are accentuated. For example a 4/4 time has the
	 * StrongBeat 0/4 and the WeakBeats 1/4, 2/4 and 3/4.
	 * If there are no accentuated Beats (SenzaMisura) an empty list is returned.
	 * @return
	 */
	public abstract ArrayList<Tuple2<Fraction, BeatWeight>> getBeatWeights();
}
