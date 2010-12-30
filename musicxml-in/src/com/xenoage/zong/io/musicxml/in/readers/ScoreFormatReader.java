package com.xenoage.zong.io.musicxml.in.readers;

import static com.xenoage.zong.io.musicxml.in.readers.StaffLayoutReader.readStaffLayout;

import java.awt.Font;

import com.xenoage.util.math.Size2f;
import com.xenoage.zong.core.format.LayoutFormat;
import com.xenoage.zong.core.format.PageFormat;
import com.xenoage.zong.core.format.PageMargins;
import com.xenoage.zong.core.format.ScoreFormat;
import com.xenoage.zong.musicxml.types.MxlDefaults;
import com.xenoage.zong.musicxml.types.MxlLyricFont;
import com.xenoage.zong.musicxml.types.MxlPageLayout;
import com.xenoage.zong.musicxml.types.MxlPageMargins;
import com.xenoage.zong.musicxml.types.MxlScaling;
import com.xenoage.zong.musicxml.types.MxlScoreHeader;
import com.xenoage.zong.musicxml.types.MxlScorePartwise;
import com.xenoage.zong.musicxml.types.MxlStaffLayout;
import com.xenoage.zong.musicxml.types.MxlSystemLayout;


/**
 * This class reads the default format of a score
 * from a MusicXML 2.0 document.
 *
 * @author Andreas Wenger
 */
public final class ScoreFormatReader
{
	
	public static final class Value
	{
		public final ScoreFormat scoreFormat;
		public final LayoutFormat layoutFormat;
		public final float tenthMm;
		
		public Value(ScoreFormat scoreFormat, LayoutFormat layoutFormat, float tenthMm)
		{
			this.scoreFormat = scoreFormat;
			this.layoutFormat = layoutFormat;
			this.tenthMm = tenthMm;
		}
	}
	
  
  /**
   * Reads the default format of a score
   * from the given {@link MxlScorePartwise}.
   */
  public static Value read(MxlScorePartwise mxlScore)
  {
  	MxlScoreHeader xmlHeader = mxlScore.getScoreHeader();
  	
  	LayoutFormat layoutFormat = LayoutFormat.defaultValue;
  	ScoreFormat scoreFormat = ScoreFormat.defaultValue;
  	float tenthMm = scoreFormat.getInterlineSpace() / 10;
    
    //defaults
    MxlDefaults mxlDefaults = xmlHeader.getDefaults();
    if (mxlDefaults != null)
    {
      Float interlineSpaceNull = readInterlineSpace(mxlDefaults);
      if (interlineSpaceNull != null)
      {
      	scoreFormat = scoreFormat.withInterlineSpace(interlineSpaceNull);
      	tenthMm = interlineSpaceNull / 10;
      }
      
      //page layout
      layoutFormat = readPageLayout(mxlDefaults, tenthMm);
      
      //system layout
      MxlSystemLayout mxlSystemLayout = mxlDefaults.getLayout().getSystemLayout();
      if (mxlSystemLayout != null)
      {
      	SystemLayoutReader.Value v = SystemLayoutReader.read(mxlSystemLayout, tenthMm);
      	scoreFormat = scoreFormat.withSystemLayout(v.systemLayout); 
      	if (v.topSystemDistance != null)
      	{
      		scoreFormat = scoreFormat.withTopSystemDistance(v.topSystemDistance);
      	}
      }
      
      //staff layouts
      for (MxlStaffLayout mxlStaffLayout : mxlDefaults.getLayout().getStaffLayouts())
      {
      	StaffLayoutReader.Value v = readStaffLayout(mxlStaffLayout, tenthMm);
      	if (v.number == null)
      	{
      		scoreFormat = scoreFormat.withStaffLayoutOther(v.staffLayout);
      	}
      	else
      	{
      		scoreFormat = scoreFormat.withStaffLayout(v.number - 1, v.staffLayout);
      	}
      }
      
      //read default lyrics font
      //(only one lyric font is supported)
      MxlLyricFont mxlLyricFont = mxlDefaults.getLyricFont();
      if (mxlLyricFont != null)
      {
      	Font defaultLyricFont = FontInfoReader.readFontInfo(mxlLyricFont.getFont()).createFont();
      	scoreFormat = scoreFormat.withLyricFont(defaultLyricFont);
      }
    }
    
    return new Value(scoreFormat, layoutFormat, tenthMm);
  }


  /**
   * Reads the interline space from the scaling element.
   */
  private static Float readInterlineSpace(MxlDefaults mxlDefaults)
  {
    MxlScaling mxlScaling = mxlDefaults.getScaling();
    if (mxlScaling != null)
    {
      float millimeters = mxlScaling.getMillimeters();
      float tenths = mxlScaling.getTenths();
      return millimeters * 10 / tenths;
    }
    else
    {
    	return null;
    }
  }
  
  
  /**
   * Reads the page-layout-block.
   */
  private static LayoutFormat readPageLayout(MxlDefaults mxlDefaults, float tenthsMm)
  {
    MxlPageLayout mxlPageLayout = mxlDefaults.getLayout().getPageLayout();
    if (mxlPageLayout != null)
    {
      Size2f size = PageFormat.defaultValue.getSize();
      
      //page-width and page-height
      Float mxlPageWidth = mxlPageLayout.getPageWidth();
      Float mxlPageHeight = mxlPageLayout.getPageHeight();
      if (mxlPageWidth != null && mxlPageHeight != null)
      {
        size = new Size2f(tenthsMm * mxlPageWidth, tenthsMm * mxlPageHeight);
      }

      //page-margins
      PageMargins pageMarginsLeft = PageMargins.defaultValue;
      PageMargins pageMarginsRight = PageMargins.defaultValue;
      for (MxlPageMargins mxlMargins : mxlPageLayout.getPageMargins())
      {
        PageMargins pageMargins = new PageMargins(
        	tenthsMm * mxlMargins.getLeftMargin(),
        	tenthsMm * mxlMargins.getRightMargin(),
        	tenthsMm * mxlMargins.getTopMargin(),
        	tenthsMm * mxlMargins.getBottomMargin());
        //left, right page or both? default: both
        switch (mxlMargins.getType())
        {
        	case Both:
        		pageMarginsLeft = pageMargins;
          	pageMarginsRight = pageMargins;
          	break;
        	case Odd:
        		pageMarginsRight = pageMargins;
        		break;
        	case Even:
        		pageMarginsRight = pageMargins;
        		break;	
        }
      }
      
      return new LayoutFormat(new PageFormat(size, pageMarginsLeft),
      	new PageFormat(size, pageMarginsRight));
    }
    else
    {
    	return LayoutFormat.defaultValue;
    }
  }

}
