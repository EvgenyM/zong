/**
 * 
 */
package com.xenoage.zong.io.midi.out;

import javax.sound.midi.Sequence;

import org.junit.Assert;
import org.junit.Test;

import com.xenoage.util.math.Fraction;
import com.xenoage.zong.data.Part;
import com.xenoage.zong.data.Score;
import com.xenoage.zong.data.music.Chord;
import com.xenoage.zong.data.music.Measure;
import com.xenoage.zong.data.music.Pitch;
import com.xenoage.zong.data.music.Staff;
import com.xenoage.zong.data.music.Voice;
import com.xenoage.zong.data.music.clef.Clef;
import com.xenoage.zong.data.music.clef.ClefType;
import com.xenoage.zong.data.music.directions.Dynamics;
import com.xenoage.zong.data.music.directions.DynamicsType;
import com.xenoage.zong.data.music.key.TraditionalKey;
import com.xenoage.zong.data.music.time.NormalTime;


/**
 * @author Uli
 * 
 */
public class MidiVelocityConverterTest
{
	@Test
	public void midiConverterTest()
	{
		/* TODO
		Score score = createTestScore();
		Sequence sequence = MidiConverter.convertToSequence(score, false, true).getSequence();
		byte[] message = sequence.getTracks()[0].get(0).getMessage().getMessage();
		Assert.assertEquals(58, message[2]);
		message = sequence.getTracks()[0].get(1).getMessage().getMessage();
		Assert.assertEquals(116, message[2]);
		//Message 2 is note-off event
		message = sequence.getTracks()[0].get(3).getMessage().getMessage();
		Assert.assertEquals(110, message[2]);
		//Message 4 and 5 are note-off events
		message = sequence.getTracks()[0].get(6).getMessage().getMessage();
		Assert.assertEquals(110, message[2]);
		//Message 7 is note-off event
		message = sequence.getTracks()[0].get(8).getMessage().getMessage();
		Assert.assertEquals(110, message[2]);
		message = sequence.getTracks()[0].get(9).getMessage().getMessage();
		Assert.assertEquals(116, message[2]);
		//Message 10 is note-off event
		message = sequence.getTracks()[0].get(11).getMessage().getMessage();
		Assert.assertEquals(58, message[2]);
		//Message 12 and 13 are note-off events
		message = sequence.getTracks()[0].get(14).getMessage().getMessage();
		Assert.assertEquals(58, message[2]);
		//Message15 is note-off event
		message = sequence.getTracks()[0].get(16).getMessage().getMessage();
		Assert.assertEquals(102, message[2]);
		message = sequence.getTracks()[0].get(17).getMessage().getMessage();
		Assert.assertEquals(58, message[2]);
		//Message 18 is note-off event
		message = sequence.getTracks()[0].get(19).getMessage().getMessage();
		Assert.assertEquals(58, message[2]);
		//Message 20 is note-off event
		message = sequence.getTracks()[0].get(21).getMessage().getMessage();
		Assert.assertEquals(58, message[2]);
		//Message 22, 23 and 24 are note-off events
		 */
	}
	
	
	public static Score createTestScore()
	{
		Score ret = new Score();
		Part pianoPart = ret.addPart(0, 1);
		Staff staff;

		ret.addEmptyMeasures(3);
		staff = ret.getStaff(ret.getPartStartIndex(pianoPart));

		Measure measure = staff.getMeasures().get(0);
		measure.addNoVoiceElement(new Clef(ClefType.G));
		measure.addNoVoiceElement(new TraditionalKey(-3));
		measure.addNoVoiceElement(new NormalTime(3, 4));
		
		Chord chord;

		Voice voice = measure.getVoices().get(0);
		chord = voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));
		chord.addDirection(new Dynamics(DynamicsType.pp));

		chord = voice.addNote(new Pitch('A', 0, 4), new Fraction(1, 4));
		chord.addDirection(new Dynamics(DynamicsType.ff));

		chord = voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));
		chord.addDirection(new Dynamics(DynamicsType.sfp));

		voice = measure.addVoice();
		
		chord = voice.addNote(new Pitch('C', 0, 4), new Fraction(1, 4));
		chord.addDirection(new Dynamics(DynamicsType.fff));


		measure = staff.getMeasures().get(1);

		voice = measure.getVoices().get(0);
		chord = voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));
		//chord.addDirection(new Dynamics(DynamicsType.pp));

		chord = voice.addNote(new Pitch('A', 0, 4), new Fraction(1, 4));
		chord.addDirection(new Dynamics(DynamicsType.pp));

		chord = voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));
		chord.addDirection(new Dynamics(DynamicsType.pp));

		voice = measure.addVoice();
		chord = voice.addNote(new Pitch('C', 0, 5), new Fraction(1, 4));

		
		measure = staff.getMeasures().get(2);

		voice = measure.getVoices().get(0);
		chord = voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));
		chord.addDirection(new Dynamics(DynamicsType.sfz));

		chord = voice.addNote(new Pitch('A', 0, 4), new Fraction(1, 4));
		//chord.addDirection(new Dynamics(DynamicsType.pp));

		chord = voice.addNote(new Pitch('G', 0, 4), new Fraction(1, 4));
		//chord.addDirection(new Dynamics(DynamicsType.pp));
		
		voice = measure.addVoice();
		voice = measure.addVoice();
		chord = voice.addNote(new Pitch('C',0,5), new Fraction(1,2));


		return ret;
	}
}
