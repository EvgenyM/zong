package com.xenoage.zong.musiclayout.layouter.beamednotation.alignment;

import static com.xenoage.util.Delta.DELTA_FLOAT_2;
import static com.xenoage.zong.data.music.StemDirection.Down;
import static com.xenoage.zong.data.music.StemDirection.Up;
import static com.xenoage.zong.musiclayout.layouter.beamednotation.alignment.SingleMeasureSingleStaffStrategy.isBeamOutsideStaff;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import com.xenoage.util.Delta;
import com.xenoage.zong.data.music.StemDirection;
import com.xenoage.zong.musiclayout.layouter.beamednotation.design.SingleBeamDesign;
import com.xenoage.zong.musiclayout.notations.chord.NotesAlignment;
import com.xenoage.zong.musiclayout.notations.chord.StemAlignment;
import com.xenoage.zong.musiclayout.notations.chord.NoteAlignment;


/**
 * Test class for {@link SingleMeasureSingleStaffStrategy}.
 * 
 * @author Uli Teschemacher
 * @author Andreas Wenger
 */
public class SingleMeasureSingleStaffStrategyTest
{
	
	SingleMeasureSingleStaffStrategy strategy = new SingleMeasureSingleStaffStrategy();
	
	
	/**
	 * Attention! This test will only work correct if the values of Ted Ross are selected in the {@link SingleBeamDesign}.
	 * TODO: fix, when Teddy is back
	 */
	@Test public void computeStemAlignments1StaffTest()
	{
		int linesCount = 5;
		float[] positionX;
		float[] positionX6 = {2,3,4,5,6,8};
		float[] positionX4 = {2,4,6,8};
		StemDirection dir;
		StemAlignment[] alignment;
		NotesAlignment[] cna;
		int singlebeam = 1;
		int doublebeam = 2;
		
		positionX = new float[2];
		positionX[0] = 2;
		positionX[1] = 8;
		dir = StemDirection.Down;
 
		int[][] notes1 = {{6},{7}};
		cna = generateChordNotesAlignment(notes1, dir);
		
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir).getStemAlignments();

		assertEquals(-0.5, alignment[0].getEndLinePosition(), Delta.DELTA_FLOAT_2);
		assertEquals(0, alignment[1].getEndLinePosition(), Delta.DELTA_FLOAT_2);

		dir = StemDirection.Up;
		int[][] notes2 = {{-1,1},{-1,1}};
		cna = generateChordNotesAlignment(notes2, dir);
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(8, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(8, alignment[1].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Down;
		int[][] notes3 = {{6},{2},{3},{4},{5},{7}};
		cna = generateChordNotesAlignment(notes3, dir);
		alignment = strategy.computeStemAlignments(cna, positionX6, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(1F, alignment[5].getEndLinePosition() - alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(-4.5, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
    assertEquals(-3.5, alignment[5].getEndLinePosition(), DELTA_FLOAT_2);
     		
        
    //Attention! In this example, Ted Ross breaks his own rules!
		dir = StemDirection.Down;
		int[][] notes4 = {{7},{8},{5},{5}};
		cna = generateChordNotesAlignment(notes4, dir);
    alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, singlebeam, dir).getStemAlignments();
    assertEquals(-1.0, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(-2.0, alignment[3].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Up;
		int[][] notes5 = {{1},{-1}};
		cna = generateChordNotesAlignment(notes5, dir);
    alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(7, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(6, alignment[1].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Up;
		int[][] notes6 = {{0},{-1},{2},{5}};
		cna = generateChordNotesAlignment(notes6, dir);
    alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(8.5, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(11, alignment[3].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Down;
		int[][] notes7 = {{11},{15}};
		cna = generateChordNotesAlignment(notes7, dir);
    alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(3, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(4, alignment[1].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Down;
		int[][] notes8 = {{11},{12}};
		cna = generateChordNotesAlignment(notes8, dir);
    alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(3.5, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(4, alignment[1].getEndLinePosition(), DELTA_FLOAT_2);
		
		//closer Spacing
		positionX[1] = 4;
		dir = StemDirection.Down;
		int[][] notes9 = {{9},{5}};
		cna = generateChordNotesAlignment(notes9, dir);
		alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir).getStemAlignments();
    assertEquals(0, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(-1, alignment[1].getEndLinePosition(), DELTA_FLOAT_2);

		//normal Spacing again
		positionX[1] = 8;
		dir = StemDirection.Down;
		int[][] notes10 = {{9},{13}};
		cna = generateChordNotesAlignment(notes10, dir);
    alignment = strategy.computeStemAlignments(cna, positionX, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(3, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(4, alignment[1].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Down;
		int[][] notes11 = {{5},{6},{9},{8},{7},{8}};
		cna = generateChordNotesAlignment(notes11, dir);
    alignment = strategy.computeStemAlignments(cna, positionX6, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(-1, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(1, alignment[5].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Up;
		cna = generateChordNotesAlignment(notes11, dir);
    alignment = strategy.computeStemAlignments(cna, positionX6, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(0,alignment[5].getEndLinePosition() - alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Up;
		int[][] notes12 = {{-2},{-5},{-2},{-5}};
		cna = generateChordNotesAlignment(notes12, dir);
    alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(4, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(4, alignment[3].getEndLinePosition(), DELTA_FLOAT_2);

		dir = StemDirection.Up;
		cna = generateChordNotesAlignment(notes12, dir);
    alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, doublebeam, dir).getStemAlignments();
		assertEquals(4.5, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(4.5, alignment[3].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Down;
		int[][] notes13 = {{13},{11},{12},{11}};
		cna = generateChordNotesAlignment(notes13, dir);
    alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, singlebeam, dir).getStemAlignments();
		assertEquals(4, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(4, alignment[3].getEndLinePosition(), DELTA_FLOAT_2);
		
		dir = StemDirection.Down;
		cna = generateChordNotesAlignment(notes13, dir);
    alignment = strategy.computeStemAlignments(cna, positionX4, linesCount, doublebeam, dir).getStemAlignments();
		assertEquals(4, alignment[0].getEndLinePosition(), DELTA_FLOAT_2);
		assertEquals(4, alignment[3].getEndLinePosition(), DELTA_FLOAT_2);
		
	}

	private NotesAlignment[] generateChordNotesAlignment(int[][] notes,
		StemDirection dir)
	{
		NotesAlignment[] cna = new NotesAlignment[notes.length];
		int i = 0;
		for (int[] chord : notes)
		{
			NoteAlignment na[] = new NoteAlignment[chord.length];
			int a = 0;
			for (int note :chord){
				na[a++] = new NoteAlignment(note,1);
			}
			cna[i++] = new NotesAlignment(1,1,na,1,1,new int[]{1},0);
		}
		return cna;
	}	
	
	
	@Test public void testIsBeamOutsideStaff()
	{
		//stem up, above staff
		assertTrue(isBeamOutsideStaff(Up, 13, 13, 5, 2));
		assertTrue(isBeamOutsideStaff(Up, 12.1f, 13, 5, 2));
		assertTrue(isBeamOutsideStaff(Up, 13f, 12.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 11.9f, 11.9f, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 11.9f, 13, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 13f, 11.9f, 5, 2));
		//stem up, below staff
		assertTrue(isBeamOutsideStaff(Up, -1, -1, 5, 2));
		assertTrue(isBeamOutsideStaff(Up, -0.1f, -1, 5, 2));
		assertTrue(isBeamOutsideStaff(Up, -1, -0.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 0.1f, 0.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, 0.1f, -1, 5, 2));
		assertFalse(isBeamOutsideStaff(Up, -1, 0.1f, 5, 2));
		//stem down, above staff
		assertTrue(isBeamOutsideStaff(Down, 9, 9, 5, 2));
		assertTrue(isBeamOutsideStaff(Down, 8.1f, 9, 5, 2));
		assertTrue(isBeamOutsideStaff(Down, 9, 8.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, 7.9f, 7.9f, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, 7.9f, 9, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, 9, 7.9f, 5, 2));
		//stem down, below staff
		assertTrue(isBeamOutsideStaff(Down, -5, -5, 5, 2));
		assertTrue(isBeamOutsideStaff(Down, -4.1f, -5, 5, 2));
		assertTrue(isBeamOutsideStaff(Down, -5, -4.1f, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, -3.9f, -3.9f, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, -3.9f, -5, 5, 2));
		assertFalse(isBeamOutsideStaff(Down, -5, -3.9f, 5, 2));
	}
	
}
