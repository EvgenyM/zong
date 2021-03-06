package com.xenoage.zong.musiclayout.stampings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.xenoage.util.Delta;
import com.xenoage.util.math.Point2f;
import com.xenoage.util.math.Shape;
import com.xenoage.zong.core.format.ScoreFormat;


/**
 * Test cases for a {@link StaffStamping}.
 *
 * @author Andreas Wenger
 */
public class StaffStampingTest
{
  
  private ScoreFormat scoreFormat = ScoreFormat.defaultValue;
  

  @Test public void createStaffLayoutElement()
  {
    //create default staff layout element.
    //drawing element must have 5 lines and the default interline space.
    StaffStamping staff1 = new StaffStamping(new Point2f(40, 80), 160, 5, 1.6f, null);
    assertEquals(staff1.getLinesCount(), 5);
    assertEquals(staff1.getInterlineSpace(),
      scoreFormat.getInterlineSpace(), Delta.DELTA_FLOAT);
    assertEquals(staff1.getPosition().x, 40, Delta.DELTA_FLOAT);
    assertEquals(staff1.getPosition().y, 80, Delta.DELTA_FLOAT);
    assertEquals(staff1.getLength(), 160, Delta.DELTA_FLOAT);
    //create another staff layout element
    //with 3 lines and 4 mm interline space.
    StaffStamping staff2 = new StaffStamping(new Point2f(40, 160), 160, 3, 4, null);
    assertEquals(staff2.getLinesCount(), 3);
    assertEquals(staff2.getInterlineSpace(), 4, Delta.DELTA_FLOAT);
  }
  
  
  @Test public void containsPoint()
  {
  	StaffStamping staff = new StaffStamping(new Point2f(40, 80), 160, 5, 1, null);
  	Shape shape = staff.getBoundingShape();
    //don't hit it
    assertFalse(shape.contains(new Point2f(39, 81)));
    assertFalse(shape.contains(new Point2f(42, 79)));
    assertFalse(shape.contains(new Point2f(201, 81)));
    assertFalse(shape.contains(new Point2f(100, 85)));
    //hit it
    assertTrue(shape.contains(new Point2f(40, 81)));
    assertTrue(shape.contains(new Point2f(41, 80)));
    assertTrue(shape.contains(new Point2f(200, 81)));
    assertTrue(shape.contains(new Point2f(100, 84)));
  }
  
}
