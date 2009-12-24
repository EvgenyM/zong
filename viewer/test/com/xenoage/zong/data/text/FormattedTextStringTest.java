package com.xenoage.zong.data.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.xenoage.util.Units;
import com.xenoage.util.font.FontInfo;


/**
 * Test cases for a {@link FormattedTextElement}
 * 
 * @author Andreas Wenger
 */
public class FormattedTextStringTest
{
	
	
	@Test public void testAscentAndDescent()
  {
		FormattedTextStyle style = new FormattedTextStyle(new FontInfo("Arial", 72f, null));
		float ascent = Units.pxToMm(66, 1);
		float descent = Units.pxToMm(16, 1);
		float delta = 1f; //TODO: why so rough?
		//any letters
		FormattedTextElement text = new FormattedTextString("AbcdefÄß*^^°}yqg", style);
		assertEquals(ascent, text.getAscent(), delta);
		assertEquals(descent, text.getDescent(), delta);
		//only "small" letters - must have same result
    text = new FormattedTextString("acemnorsuvwxz", style);
    assertEquals(ascent, text.getAscent(), delta);
		assertEquals(descent, text.getDescent(), delta);
  }
	

}
