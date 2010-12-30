package com.xenoage.zong.musiclayout.layouter.notation;

import static org.junit.Assert.*;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.zong.core.music.Pitch.pi;
import static com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment.*;

import com.xenoage.util.Delta;
import com.xenoage.zong.core.music.*;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.musiclayout.Constants;
import com.xenoage.zong.musiclayout.notations.chord.AccidentalsAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteSuspension;

import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for the {@link AccidentalsAlignmentStrategy}.
 *
 * @author Andreas Wenger
 */
public class AccidentalsAlignmentStrategyTest
{
  
	private AccidentalsAlignmentStrategy strategy;
	
  private MusicContext contextC, contextEb;
  private MusicContext contextAccD4, contextAccG4,
    contextAccB4, contextAccC5, contextAccD5;
  private MusicContext contextAccsD4B4;//, contextAccsG5A5;
  private float noteOffset = 0;
  
  private float widthGapAccToNote = WIDTH_GAP_ACCTONOTE;
  private float widthGapAccToAcc = WIDTH_GAP_ACCTOACC;
  private float widthDoubleSharp = WIDTH_DOUBLESHARP;
  private float widthSharp = WIDTH_SHARP;
  private float widthNatural = WIDTH_NATURAL;
  private float widthFlat = WIDTH_FLAT;
  private float widthFlatFlat = WIDTH_DOUBLEFLAT;
  
  private float widthQuarter = Constants.WIDTH_QUARTER;
  
  private NoteSuspension susLeft = NoteSuspension.Left;
  private NoteSuspension susRight = NoteSuspension.Right;
  
  
  
  @Before public void setUp()
  {
  	strategy = new AccidentalsAlignmentStrategy();
    ClefType clefG = ClefType.G;
    contextC = MusicContext.simpleInstance;
    //contextEb: key = Eb major, acc = Fbb5, G##5
    contextEb = new MusicContext(
      new Clef(clefG), new TraditionalKey(-3),
      new Pitch[]{pi(3, -2, 5), pi(4, 2, 5)}, 5);
    //contextAccD4: key = C major, acc = D#4
    contextAccD4 = new MusicContext(
      new Clef(clefG), new TraditionalKey(0),
      new Pitch[]{pi(1, 1, 4)}, 5);
    //contextAccG4: key = C major, acc = G#4
    contextAccG4 = new MusicContext(
      new Clef(clefG), new TraditionalKey(0),
      new Pitch[]{pi(4, 1, 4)}, 5);
    //contextAccB4: key = C major, acc = B#4
    contextAccB4 = new MusicContext(
      new Clef(clefG), new TraditionalKey(0),
      new Pitch[]{pi(6, 1, 4)}, 5);
    //contextAccC5: key = C major, acc = C#5
    contextAccC5 = new MusicContext(
      new Clef(clefG), new TraditionalKey(0),
      new Pitch[]{pi(0, 1, 5)}, 5);
    //contextAccD5: key = C major, acc = D#5
    contextAccD5 = new MusicContext(
      new Clef(clefG), new TraditionalKey(0),
      new Pitch[]{pi(1, 1, 5)}, 5);
    //contextAccsD4B5: key = C major, acc = D#4 and B#4
    contextAccsD4B4 = new MusicContext(
      new Clef(clefG), new TraditionalKey(0),
      new Pitch[]{pi(1, 1, 4), pi(6, 1, 4)}, 5);
    //contextAccsG5A5: key = C major, acc = G#5 and A#5
    /*TODO contextAccsG5A5 = new MusicContext(
      new Clef(clefG, null), new TraditionalKey(0),
      new Pitch[]{pi4, 1, 5), pi5, 1, 5)});*/
    noteOffset = Constants.WIDTH_QUARTER;
  }
  
  
  /**
   * Tests some chords with no accidentals
   */
  @Test public void testNoAcc()
  {
    //C5
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
      pvec(pi(0, 0, 5)),
      new NoteAlignment[]{new NoteAlignment(5, 0)}, contextC);
    assertNull(caa);
    //C4, D4, G4
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(0, 0, 4), pi(1, 0, 4), pi(4, 0, 4)),
      new NoteAlignment[]{new NoteAlignment(-2, noteOffset),
        new NoteAlignment(-1, 0, susRight), new NoteAlignment(2, noteOffset)},
      contextC);
    assertNull(caa);
    //Eb4, Ab4, G##5 with contextEb
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(2, -1, 4), pi(5, -1, 4), pi(4, 2, 5)),
      new NoteAlignment[]{new NoteAlignment(0, 0),
        new NoteAlignment(3, 0), new NoteAlignment(9, 0)}, contextEb);
    assertNull(caa);
  }
  
  
  /**
   * Tests some chords with one accidental.
   */
  @Test public void test1Acc()
  {
    //C#5
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(0, 1, 5)),
      new NoteAlignment[]{new NoteAlignment(5, 0)}, contextC);
    assertEquals(1, caa.getAccidentals().size());
    assertEquals(widthSharp + widthGapAccToNote, caa.getWidth(), Delta.DELTA_FLOAT);
    //C##5
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(0, 2, 5)),
      new NoteAlignment[]{new NoteAlignment(5, 0)}, contextC);
    assertEquals(1, caa.getAccidentals().size());
    assertEquals(widthDoubleSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    //C4, D4, Gbb4
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(0, 0, 4), pi(1, 0, 4), pi(4, -2, 4)),
      new NoteAlignment[]{new NoteAlignment(-2, noteOffset),
        new NoteAlignment(-1, 0, susRight), new NoteAlignment(2, noteOffset)},
      contextC);
    assertEquals(1, caa.getAccidentals().size());
    assertEquals(widthFlatFlat + widthGapAccToNote, caa.getWidth(), Delta.DELTA_FLOAT);
    //Eb4, A4, G##5 with contextEb
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(2, -1, 4), pi(5, 0, 4), pi(4, 2, 5)),
      new NoteAlignment[]{new NoteAlignment(0, 0),
        new NoteAlignment(3, 0), new NoteAlignment(9, 0)}, contextEb);
    assertEquals(1, caa.getAccidentals().size());
    assertEquals(widthNatural + widthGapAccToNote, caa.getWidth(), Delta.DELTA_FLOAT);
  }
  
  
  /**
   * Tests some chords with two notes and two accidentals.
   * The examples are taken from "Ross: The Art of Music Engraving",
   * page 131 f.
   */
  @Test public void test2Acc2Notes()
  {
    //F#4, F#5 (p. 131, F# instead of F(nat))
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(3, 1, 4), pi(3, 1, 5)),
      new NoteAlignment[]{new NoteAlignment(1, 0), new NoteAlignment(8, 0)}, contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthSharp + widthGapAccToNote, caa.getWidth(), Delta.DELTA_FLOAT);
    //G#4, B#5 (p. 131)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(4, 1, 4), pi(6, 1, 5)),
      new NoteAlignment[]{new NoteAlignment(2, 0), new NoteAlignment(11, 0)}, contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthSharp + widthGapAccToNote, caa.getWidth(), Delta.DELTA_FLOAT);
    //Bb4, Ab5 (p. 131)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(6, -1, 4), pi(5, -1, 5)),
      new NoteAlignment[]{new NoteAlignment(4, 0), new NoteAlignment(10, 0)}, contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthFlat + widthGapAccToNote, caa.getWidth(), Delta.DELTA_FLOAT);
    //Ab4, Bb4 (p. 132)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(5, -1, 4), pi(6, -1, 4)),
      new NoteAlignment[]{
        new NoteAlignment(3, 0), new NoteAlignment(4, widthQuarter, susRight)},
      contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthFlat + widthGapAccToAcc + widthFlat + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f, caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(widthFlat + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    //A#4, F#5 (p. 132)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(5, 1, 4), pi(3, 1, 5)),
      new NoteAlignment[]{
        new NoteAlignment(3, 0), new NoteAlignment(8, widthQuarter)}, contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthSharp + widthGapAccToAcc + widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f, caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    //Db5, Eb5 (p. 132)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(1, -1, 5), pi(2, -1, 5)),
      new NoteAlignment[]{
        new NoteAlignment(6, 0, susLeft), new NoteAlignment(7, widthQuarter)},
      contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthFlat + widthGapAccToAcc + widthFlat + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f, caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(widthFlat + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
  }
  
  
  /**
   * Tests some chords with three notes and two accidentals.
   * The examples are taken from "Ross: The Art of Music Engraving",
   * page 132.
   */
  @Test public void test2Acc3Notes()
  {
    //E4, G#4, C#5 (no accidental at bottom note)
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(2, 0, 4), pi(4, 1, 4), pi(0, 1, 5)),
      new NoteAlignment[]{new NoteAlignment(0, 0),
        new NoteAlignment(2, 0), new NoteAlignment(5, 0)}, contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthSharp + widthGapAccToAcc + widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f, caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(2, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(5, caa.getAccidentals().get(1).getLinePosition());
    //Eb4, G(nat)4, C5 with contextAccG4 (no accidental at top note)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(2, -1, 4), pi(4, 0, 4), pi(0, 0, 5)),
      new NoteAlignment[]{new NoteAlignment(0, 0),
        new NoteAlignment(2, 0), new NoteAlignment(5, 0)}, contextAccG4);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthFlat + widthGapAccToAcc + widthNatural + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f, caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(0, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(widthFlat + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(2, caa.getAccidentals().get(1).getLinePosition());
    //Eb4, G4, C(nat)5 with contextAccC5 (no accidental at middle note)
    //TODO: Eb4-accidental can be placed nearer to the chord
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(2, -1, 4), pi(4, 0, 4), pi(0, 0, 5)),
      new NoteAlignment[]{new NoteAlignment(0, 0),
        new NoteAlignment(2, 0), new NoteAlignment(5, 0)}, contextAccC5);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthFlat + widthGapAccToAcc + widthNatural + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f, caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(0, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(widthFlat + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(5, caa.getAccidentals().get(1).getLinePosition());
    //F4, G#4, D(nat)5 with contextAccD5
    //(no accidental at bottom note, middle note suspended)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(3, 0, 4), pi(4, 1, 4), pi(1, 0, 5)),
      new NoteAlignment[]{new NoteAlignment(1, 0),
        new NoteAlignment(2, widthQuarter, susRight), new NoteAlignment(6, 0)},
      contextAccD5);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthNatural + widthGapAccToAcc + widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(widthNatural + widthGapAccToAcc,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(2, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(0f,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(6, caa.getAccidentals().get(1).getLinePosition());
    //F#4, C5, D#5
    //(no accidental at middle note, top note suspended)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(3, 1, 4), pi(0, 0, 5), pi(1, 1, 5)),
      new NoteAlignment[]{new NoteAlignment(1, 0),
        new NoteAlignment(5, 0), new NoteAlignment(6, widthQuarter, susRight)},
      contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthSharp + widthGapAccToAcc + widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(1, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(6, caa.getAccidentals().get(1).getLinePosition());
    //Ab4, Eb5, F5
    //(no accidental at top note, middle note suspended)
    //TODO: accidentals nearer to chord (Ab4-accidental
    //has enough room under Eb5)
    caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(5, -1, 4), pi(2, -1, 5), pi(3, 0, 5)),
      new NoteAlignment[]{new NoteAlignment(3, widthQuarter),
        new NoteAlignment(7, 0, susLeft), new NoteAlignment(8, widthQuarter)},
      contextC);
    assertEquals(2, caa.getAccidentals().size());
    assertEquals(widthFlat + widthGapAccToAcc + widthFlat + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(widthFlat + widthGapAccToAcc,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(3, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(0f,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(7, caa.getAccidentals().get(1).getLinePosition());
  }
  
  
  /**
   * Test a chord with three accidentals.
   * The example is taken from "Ross: The Art of Music Engraving",
   * page 132/133, rule 1.
   */
  @Test public void test3AccRule1()
  {
    //D#4, F#4, C#5
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(1, 1, 4), pi(3, 1, 4), pi(0, 1, 5)),
      new NoteAlignment[]{new NoteAlignment(-1, 0),
        new NoteAlignment(1, 0), new NoteAlignment(5, 0)}, contextC);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(widthSharp + widthGapAccToAcc + widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(widthSharp + widthGapAccToAcc, caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(-1, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(0f,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(1, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(5, caa.getAccidentals().get(2).getLinePosition());
  }
  
  
  /**
   * Test a chord with three accidentals.
   * The example is taken from "Ross: The Art of Music Engraving",
   * page 132/133, rule 2.
   */
  @Test public void test3AccRule2()
  {
    //D#4, F#4, B4 with contextAccB4
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(1, 1, 4), pi(3, 1, 4), pi(6, 0, 4)),
      new NoteAlignment[]{new NoteAlignment(-1, 0),
        new NoteAlignment(1, 0), new NoteAlignment(4, 0)}, contextAccB4);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(2 * (widthSharp + widthGapAccToAcc) +
      widthNatural + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(-1, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(0f,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(1, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(2 * (widthSharp + widthGapAccToAcc),
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(4, caa.getAccidentals().get(2).getLinePosition());
  }
  
  
  /**
   * Test a chord with three accidentals.
   * The example is taken from "Ross: The Art of Music Engraving",
   * page 132/133, rule 3.
   */
  @Test public void test3AccRule3()
  {
    //D#4, E#4, C#5
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(1, 1, 4), pi(2, 1, 4), pi(0, 1, 5)),
      new NoteAlignment[]{new NoteAlignment(-1, 0),
        new NoteAlignment(0, widthQuarter, susRight), new NoteAlignment(5, 0)},
      contextC);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(2 * (widthSharp + widthGapAccToAcc) +
      widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(-1, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(2 * (widthSharp + widthGapAccToAcc),
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(0, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(5, caa.getAccidentals().get(2).getLinePosition());
  }
  
  
  /**
   * Test a chord with three accidentals.
   * The example is taken from "Ross: The Art of Music Engraving",
   * page 133, rule 4.
   */
  @Test public void test3AccRule4()
  {
    //D#4, C#5, D#5
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(1, 1, 4), pi(0, 1, 5), pi(1, 1, 5)),
      new NoteAlignment[]{new NoteAlignment(-1, 0),
        new NoteAlignment(5, 0), new NoteAlignment(6, widthQuarter, susRight)},
      contextC);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(widthSharp + widthGapAccToAcc +
      widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(-1, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(0f,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(5, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(6, caa.getAccidentals().get(2).getLinePosition());
  }
  
  
  /**
   * Test a chord with three accidentals.
   * The example is taken from "Ross: The Art of Music Engraving",
   * page 133, rule 5.
   */
  @Test public void test3AccRule5()
  {
    //D4, Ab4, Bb4 with contextAccD4
    //TODO: natural can be indented nearer to the chord
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(1, 0, 4), pi(5, -1, 4), pi(6, -1, 4)),
      new NoteAlignment[]{new NoteAlignment(-1, 0),
        new NoteAlignment(3, 0), new NoteAlignment(4, widthQuarter, susRight)},
      contextAccD4);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(widthFlat + widthGapAccToAcc +
      widthNatural + widthGapAccToAcc +
      widthFlat + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(widthFlat + widthGapAccToAcc,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(-1, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(0f,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(3, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(widthFlat + widthGapAccToAcc +
      widthNatural + widthGapAccToAcc,
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(4, caa.getAccidentals().get(2).getLinePosition());
  }
  
  
  /**
   * Test a chord with three accidentals.
   * The example is taken from "Ross: The Art of Music Engraving",
   * page 133, rule 6.
   */
  @Test public void test3AccRule6()
  {
    //D4, E#4, B4 with contextAccsD4B4
    //TODO: natural can be indented nearer to the chord
    AccidentalsAlignment caa = strategy.computeAccidentalsAlignment(
    	pvec(pi(1, 0, 4), pi(2, 1, 4), pi(6, 0, 4)),
      new NoteAlignment[]{new NoteAlignment(-1, 0),
        new NoteAlignment(0, widthQuarter, susRight), new NoteAlignment(4, 0)},
      contextAccsD4B4);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(widthNatural + widthGapAccToAcc +
      widthNatural + widthGapAccToAcc +
      widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(-1, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(2 * (widthNatural + widthGapAccToAcc),
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(0, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(widthNatural + widthGapAccToAcc,
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(4, caa.getAccidentals().get(2).getLinePosition());
  }
  
  
  /**
   * Test some chords with three accidentals.
   * These examples are taken from "Ross: The Art of Music Engraving",
   * page 133.
   */
  @Test public void test3AccSuspendedLeft()
  {
    /* TODO: can not be solved with the current algorithm
    //Db5, Eb5, A#5
    ChordAccidentalsAlignment caa = new ChordAccidentalsAlignment(
      new Pitch[]{
        pi1, -1, 5), pi2, -1, 5), pi6, 1, 5)},
      new NoteAlignment[]{new NoteAlignment(6, 0, susLeft),
        new NoteAlignment(7, widthQuarter),
        new NoteAlignment(10, widthQuarter)}, contextC);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(2 * (widthFlat + widthGapAccToAcc) +
      widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(6, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(widthFlat + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(7, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(2 * (widthFlat + widthGapAccToAcc),
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(10, caa.getAccidentals().get(2).getLinePosition());
    //A#4, B#4, A#5
    caa = new ChordAccidentalsAlignment(
      new Pitch[]{
        pi5, 1, 4), pi6, 1, 4), pi6, 1, 5)},
      new NoteAlignment[]{new NoteAlignment(3, 0, susLeft),
        new NoteAlignment(4, widthQuarter),
        new NoteAlignment(10, widthQuarter)}, contextC);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(2 * (widthSharp + widthGapAccToAcc) +
      widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(0f,
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(3, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(widthSharp + widthGapAccToAcc,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(4, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(2 * (widthSharp + widthGapAccToAcc),
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(10, caa.getAccidentals().get(2).getLinePosition());
    //C#5, G5, A5 with contextAccsG5A5
    caa = new ChordAccidentalsAlignment(
      new Pitch[]{
        pi0, 1, 5), pi4, 0, 5), pi5, 0, 5)},
      new NoteAlignment[]{new NoteAlignment(5, widthQuarter),
        new NoteAlignment(9, 0, susLeft),
        new NoteAlignment(10, widthQuarter)}, contextAccsG5A5);
    assertEquals(3, caa.getAccidentals().size());
    assertEquals(2 * (widthNatural + widthGapAccToAcc) +
      widthSharp + widthGapAccToNote,
      caa.getWidth(), Delta.DELTA_FLOAT);
    assertEquals(2 * (widthNatural + widthGapAccToAcc),
      caa.getAccidentals().get(0).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(5, caa.getAccidentals().get(0).getLinePosition());
    assertEquals(0f,
      caa.getAccidentals().get(1).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(9, caa.getAccidentals().get(1).getLinePosition());
    assertEquals(widthNatural + widthGapAccToAcc,
      caa.getAccidentals().get(2).getOffset(), Delta.DELTA_FLOAT);
    assertEquals(10, caa.getAccidentals().get(2).getLinePosition()); */
  }
  

}
