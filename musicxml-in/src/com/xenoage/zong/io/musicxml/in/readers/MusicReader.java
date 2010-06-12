package com.xenoage.zong.io.musicxml.in.readers;

import static com.xenoage.pdlib.PVector.pvec;
import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.iterators.It.it;
import static com.xenoage.util.math.Fraction._0;
import static com.xenoage.util.math.Fraction.fr;
import static com.xenoage.zong.core.music.MP.atMeasure;
import static com.xenoage.zong.core.music.MP.atStaff;
import static com.xenoage.zong.core.music.MP.mp;
import static com.xenoage.zong.core.music.barline.Barline.createBackwardRepeatBarline;
import static com.xenoage.zong.core.music.barline.Barline.createBarline;
import static com.xenoage.zong.core.music.barline.Barline.createForwardRepeatBarline;
import static com.xenoage.zong.io.musicxml.in.readers.ChordReader.readChord;
import static com.xenoage.zong.io.musicxml.in.readers.FontInfoReader.readFontInfo;
import static com.xenoage.zong.io.musicxml.in.readers.OtherReader.readPosition;
import static com.xenoage.zong.io.musicxml.in.readers.StaffLayoutReader.readStaffLayout;

import com.xenoage.pdlib.PVector;
import com.xenoage.util.error.ErrorProcessing;
import com.xenoage.util.font.FontInfo;
import com.xenoage.util.iterators.It;
import com.xenoage.util.lang.Tuple2;
import com.xenoage.util.math.Fraction;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.core.format.Break;
import com.xenoage.zong.core.format.SystemLayout;
import com.xenoage.zong.core.header.ScoreHeader;
import com.xenoage.zong.core.music.Measure;
import com.xenoage.zong.core.music.Staff;
import com.xenoage.zong.core.music.Voice;
import com.xenoage.zong.core.music.barline.BarlineStyle;
import com.xenoage.zong.core.music.clef.Clef;
import com.xenoage.zong.core.music.clef.ClefType;
import com.xenoage.zong.core.music.direction.Crescendo;
import com.xenoage.zong.core.music.direction.Diminuendo;
import com.xenoage.zong.core.music.direction.Dynamics;
import com.xenoage.zong.core.music.direction.DynamicsType;
import com.xenoage.zong.core.music.direction.Pedal;
import com.xenoage.zong.core.music.direction.Tempo;
import com.xenoage.zong.core.music.direction.Wedge;
import com.xenoage.zong.core.music.direction.Words;
import com.xenoage.zong.core.music.direction.Pedal.Type;
import com.xenoage.zong.core.music.format.Position;
import com.xenoage.zong.core.music.key.Key;
import com.xenoage.zong.core.music.key.TraditionalKey;
import com.xenoage.zong.core.music.layout.PageBreak;
import com.xenoage.zong.core.music.layout.SystemBreak;
import com.xenoage.zong.core.music.rest.Rest;
import com.xenoage.zong.core.music.time.NormalTime;
import com.xenoage.zong.core.music.time.SenzaMisura;
import com.xenoage.zong.core.music.time.Time;
import com.xenoage.zong.io.musicxml.in.util.MusicReaderException;
import com.xenoage.zong.io.score.ScoreController;
import com.xenoage.zong.musicxml.types.MxlAttributes;
import com.xenoage.zong.musicxml.types.MxlBackup;
import com.xenoage.zong.musicxml.types.MxlBarline;
import com.xenoage.zong.musicxml.types.MxlClef;
import com.xenoage.zong.musicxml.types.MxlDirection;
import com.xenoage.zong.musicxml.types.MxlDirectionType;
import com.xenoage.zong.musicxml.types.MxlDynamics;
import com.xenoage.zong.musicxml.types.MxlFormattedText;
import com.xenoage.zong.musicxml.types.MxlForward;
import com.xenoage.zong.musicxml.types.MxlKey;
import com.xenoage.zong.musicxml.types.MxlNormalNote;
import com.xenoage.zong.musicxml.types.MxlNormalTime;
import com.xenoage.zong.musicxml.types.MxlNote;
import com.xenoage.zong.musicxml.types.MxlPedal;
import com.xenoage.zong.musicxml.types.MxlPrint;
import com.xenoage.zong.musicxml.types.MxlScorePartwise;
import com.xenoage.zong.musicxml.types.MxlSound;
import com.xenoage.zong.musicxml.types.MxlStaffLayout;
import com.xenoage.zong.musicxml.types.MxlSystemLayout;
import com.xenoage.zong.musicxml.types.MxlTime;
import com.xenoage.zong.musicxml.types.MxlWedge;
import com.xenoage.zong.musicxml.types.MxlWords;
import com.xenoage.zong.musicxml.types.attributes.MxlPrintAttributes;
import com.xenoage.zong.musicxml.types.attributes.MxlRepeat;
import com.xenoage.zong.musicxml.types.choice.MxlDirectionTypeContent;
import com.xenoage.zong.musicxml.types.choice.MxlMusicDataContent;
import com.xenoage.zong.musicxml.types.choice.MxlDirectionTypeContent.MxlDirectionTypeContentType;
import com.xenoage.zong.musicxml.types.choice.MxlMusicDataContent.MxlMusicDataContentType;
import com.xenoage.zong.musicxml.types.choice.MxlNoteContent.MxlNoteContentType;
import com.xenoage.zong.musicxml.types.choice.MxlTimeContent.MxlTimeContentType;
import com.xenoage.zong.musicxml.types.enums.MxlBackwardForward;
import com.xenoage.zong.musicxml.types.enums.MxlRightLeftMiddle;
import com.xenoage.zong.musicxml.types.partwise.MxlMeasure;
import com.xenoage.zong.musicxml.types.partwise.MxlPart;


/**
 * This class reads the actual musical contents of
 * the given partwise MusicXML 2.0 document into a {@link Score}.
 * 
 * If possible, this reader works with the voice-element
 * to separate voices. TODO: if not existent or
 * used unreliably within a measure, implement this algorithm: 
 * http://archive.mail-list.com/musicxml/msg01673.html
 * 
 * TODO: Connect chords over staves, if they have the same
 * voice element but different staff element.
 *
 * @author Andreas Wenger
 */
public final class MusicReader
{

  
  /**
   * Reads the given MusicXML document and returns the score.
   */
  public static Score read(MxlScorePartwise doc, Score baseScore, ErrorProcessing err)
  {
  	MusicReaderContext context = new MusicReaderContext(baseScore, err); 
    
    //read the parts
    int staffIndexOffset = 0;
    It<MxlPart> mxlParts = it(doc.getParts());
    for (MxlPart mxlPart : mxlParts)
    {
      //clear part-dependent context values
      int stavesCount = baseScore.getStavesList().getParts().get(
      	mxlParts.getIndex()).getStavesCount();
      context = context.beginNewPart(mxlParts.getIndex());
      staffIndexOffset += stavesCount;
      //read the measures
      It<MxlMeasure> mxlMeasures = it(mxlPart.getMeasures());
      for (MxlMeasure mxlMeasure : mxlMeasures)
      {
        try
        {
        	context = readMeasure(context, mxlMeasure, mxlMeasures.getIndex());
        }
        catch (MusicReaderException ex)
        {
        	throw new RuntimeException("Error at " + ex.getContext().toString(), ex);
        }
        catch (Exception ex)
        {
          throw new RuntimeException("Error (roughly) around " + context.toString(), ex);
        }
    	}
    }
    
    //go through the whole score, and fill empty measures (that means, measures where
    //voice 0 has no single VoiceElement) with rests
    Fraction measureDuration = fr(1, 4);
    for (int iStaff = 0; iStaff < context.getScore().getStavesCount(); iStaff++)
    {
    	Staff staff = context.getScore().getStaff(atStaff(iStaff));
    	for (int iMeasure = 0; iMeasure < staff.getMeasures().size(); iMeasure++)
    	{
    		Measure measure = staff.getMeasures().get(iMeasure);
    		Time newTime = context.getScore().getScoreHeader().getColumnHeader(iMeasure).getTime();
    		if (newTime != null)
    		{
    			//time signature has changed
    			if (newTime instanceof NormalTime)
    			{
    				measureDuration = ((NormalTime) newTime).getBeatsPerMeasure();
    			}
    			else
    			{
    				measureDuration = fr(1, 4); //default: 1/4
    			}
    		}
    		Voice voice0 = measure.getVoices().get(0);
    		if (voice0.isEmpty())
    		{
    			//TODO: "whole rests" or split. currently, also 3/4 rests are possible
    			context = context.withScore(ScoreController.writeVoiceElement(
    				context.getScore(), mp(iStaff, iMeasure, 0, _0), new Rest(measureDuration)));
    		}
    	}
    }
    
    return context.getScore();
  }
  
  
  /**
   * Reads the given measure element.
   */
  private static MusicReaderContext readMeasure(MusicReaderContext context,
  	MxlMeasure mxlMeasure, int measureIndex)
  {
    //begin a new measure
  	context = context.beginNewMeasure(measureIndex);
    //list all elements
  	PVector<MxlMusicDataContent> content = mxlMeasure.getMusicData().getContent();
    for (int i = 0; i < content.size(); i++)
    {
    	MxlMusicDataContent mxlMDC = content.get(i);
    	switch (mxlMDC.getMusicDataContentType())
    	{
    		case Note:
    		{
    			//collect all directly following notes with have a chord-element
    			PVector<MxlNote> mxlNotes = pvec((MxlNote) mxlMDC);
    			for (int i2 = i + 1; i2 < content.size(); i2++)
    			{
    				boolean found = false;
    				MxlMusicDataContent mxlMDC2 = content.get(i2);
    				if (mxlMDC2.getMusicDataContentType() == MxlMusicDataContentType.Note)
    				{
    					MxlNote mxlNote2 = (MxlNote) mxlMDC2;
    					if (mxlNote2.getContent().getNoteContentType() == MxlNoteContentType.Normal)
    					{
    						if (((MxlNormalNote) mxlNote2.getContent()).getFullNote().isChord())
    						{
    							mxlNotes = mxlNotes.plus(mxlNote2);
    							i++;
    							found = true;
    						}
    					}
    				}
    				if (!found)
    					break;
    			}
    			context = readChord(context, mxlNotes);
    			break;
    		}
    		case Attributes:
    			context = readAttributes(context, (MxlAttributes) mxlMDC);
          break;
    		case Backup:
    			context = readBackup(context, (MxlBackup) mxlMDC);
    			break;
    		case Forward:
    			context = readForward(context, (MxlForward) mxlMDC);
    			break;
    		case Print:
    			context = readPrint(context, (MxlPrint) mxlMDC);
    			break;
    		case Direction:
    			context = readDirection(context, (MxlDirection) mxlMDC);
    			break;
    		case Barline:
    			context = readBarline(context, (MxlBarline) mxlMDC);
    			break;
    	}
    }
    return context;
  }
  
  
  /**
   * Reads the given attributes element.
   */
  private static MusicReaderContext readAttributes(MusicReaderContext context,
  	MxlAttributes mxlAttributes)
  {
  	
  	//divisions
  	Integer divisions = mxlAttributes.getDivisions();
	  if (divisions != null)
	  {
	  	context = context.withDivisions(divisions);
	  }
	  
	  //key signature
	  MxlKey mxlKey = mxlAttributes.getKey();
	  if (mxlKey != null)
	  {
      //only the fifths element is supported
      int mxlFifths = mxlKey.getFifths();
      //write to column header (TODO: attribute "number" for single staves)
      Key key = new TraditionalKey(mxlFifths);
      context = context.writeColumnElement(key);
	  }
	  
	  //time signature
	  MxlTime mxlTime = mxlAttributes.getTime();
	  if (mxlTime != null)
	  {
	  	Time time = null;
	  	MxlTimeContentType type = mxlTime.getContent().getTimeContentType();
      if (type == MxlTimeContentType.SenzaMisura)
      {
        //senza misura
        time = new SenzaMisura();
      }
      else if (type == MxlTimeContentType.NormalTime)
      {
      	//at the moment we read only one beats/beat-type
      	//currently we accept only integers > 0
      	MxlNormalTime mxlNormalTime = (MxlNormalTime) mxlTime.getContent();
        time = new NormalTime(mxlNormalTime.getBeats(), mxlNormalTime.getBeatType());
      }
      //write to column header (TODO: attribute "number" for single staves)
      if (time != null)
      {
      	context = context.writeColumnElement(time);
      }
    }
	  
	  //clefs
	  MxlClef mxlClef = mxlAttributes.getClef();
	  if (mxlClef != null)
	  {
	  	Clef clef = null;
      switch (mxlClef.getSign())
      {
      	/* TODO case C:
      		clef = new Clef(ClefType.C); break; */
      	case F:
      		clef = new Clef(ClefType.F); break;
      	case G:
      		clef = new Clef(ClefType.G); break;
      	/* TODO case NONE:
      		clef = new Clef(ClefType.C); break;
      	case PERCUSSION:
      		clef = new Clef(ClefType.C); break;
      	case TAB:
      		clef = new Clef(ClefType.C); break; */
      }
      //staff (called "number" in MusicXML), first staff is default
      int staff = mxlClef.getNumber() - 1;
      //add to staff
      if (clef != null)
      {
      	context = context.writeMeasureElement(clef, staff);
      }
    }
	  
	  /* TODO: transposition changes ~= instrument changes
	  //transposition changes
	  MxlTranspose mxlTranspose = mxlAttributes.getTranspose();
	  if (mxlTranspose != null)
	  {
	  	int chromatic = mxlTranspose.getChromatic();
	  	Transpose transpose = new Transpose(chromatic);
	  	//write to all staves of this part
	  	for (int staff = 0; staff < context.getPartStavesIndices().getCount(); staff++)
	  	{
  			writeNoVoiceElement(transpose, staff);
      }
    }
    */
  
	  return context;
  }
  
  
  /**
   * Reads the given backup element.
   */
  private static MusicReaderContext readBackup(MusicReaderContext context, MxlBackup mxlBackup)
  {
    //duration
    Fraction duration = readDuration(context, mxlBackup.getDuration()).invert();
    //move cursor
    return context.moveCurrentBeat(duration);
  }
  
  
  /**
   * Reads the given forward element.
   */
  private static MusicReaderContext readForward(MusicReaderContext context, MxlForward mxlForward)
  {
    //duration
    Fraction duration = readDuration(context, mxlForward.getDuration());
    //move cursor
    return context.moveCurrentBeat(duration);
  }

  
  /**
   * Returns the duration as a {@link Fraction} from the given duration in divisions.
   */
  public static Fraction readDuration(MusicReaderContext context, int duration)
  {
    if (duration == 0)
    {
      throw new RuntimeException("Element has a duration of 0.");
    }
    return fr(duration, 4 * context.getDivisions());
  }
  
  
  /**
   * Reads the given print element.
   */
  private static MusicReaderContext readPrint(
  	MusicReaderContext context, MxlPrint mxlPrint)
  {
  	MxlPrintAttributes mxlPA = mxlPrint.getPrintAttributes();
  	
  	//system and page break
  	Boolean newSystem = mxlPA.getNewSystem();
  	SystemBreak systemBreak = (newSystem == null ? null :
  		(newSystem ? SystemBreak.NewSystem : SystemBreak.NoNewSystem));
  	Boolean newPage = mxlPA.getNewPage();
  	PageBreak pageBreak = (newPage == null ? null :
  		(newPage ? PageBreak.NewPage : PageBreak.NoNewPage));
  	context = context.withScore(ScoreController.writeColumnElement(context.getScore(),
  		atMeasure(context.getMP().getMeasure()), new Break(pageBreak, systemBreak)));
  	
  	//we assume that custom system layout information is just used in combination with
  	//forced system/page breaks. so we ignore system-layout elements which are not combined
  	//with system/page breaks.
  	//the first measure of a score is also ok.
  	if (context.getMP().getMeasure() == 0 || systemBreak == SystemBreak.NewSystem ||
  		pageBreak == PageBreak.NewPage)
  	{
  		
  		//first page or new page?
  		boolean isPageBreak = pageBreak == PageBreak.NewPage;
  		boolean isPageStarted = (context.getMP().getMeasure() == 0 || isPageBreak);
			if (isPageBreak)
			{
				//increment page index
				context = context.incPageIndex();
			}
  		
  		//first system or new system?
			boolean isSystemBreak = isPageBreak || systemBreak == SystemBreak.NewSystem;
  		if (isSystemBreak)
  		{
  			//increment system index 
  			context = context.incSystemIndex();
  		}	

  		//read system layout, if there
  		MxlSystemLayout mxlSystemLayout = mxlPrint.getLayout().getSystemLayout();
  		if (mxlSystemLayout != null)
  		{
  			SystemLayoutReader.Value sl = SystemLayoutReader.read(mxlSystemLayout, context.getTenthMm());
  			SystemLayout systemLayout = sl.systemLayout;
  			
  			//for first systems on a page, use top-system-distance
  			if (isPageStarted)
  			{
  				systemLayout = systemLayout.withSystemDistance(sl.topSystemDistance);
  			}
  			
  			//apply values
  			ScoreHeader scoreHeader =	context.getScore().getScoreHeader().withSystemLayout(
  				context.getSystemIndex(), systemLayout);
  			context = context.withScore(context.getScore().withScoreHeader(scoreHeader));
  		}
  		
  	}
  	
  	//staff layouts
		for (MxlStaffLayout mxlStaffLayout : mxlPrint.getLayout().getStaffLayouts())
		{
			int staffIndex = mxlStaffLayout.getNumberNotNull() - 1;
			context = context.withScore(ScoreController.withStaffLayout(context.getScore(),
				context.getSystemIndex(),
				context.getPartStavesIndices().getStart() + staffIndex,
				readStaffLayout(mxlStaffLayout, context.getTenthMm()).staffLayout));
		}
		
		return context;
  }
  
  
  /**
   * Reads the given barline element.
   * Currently only left and right barlines are supported.
   */
  private static MusicReaderContext readBarline(MusicReaderContext context, MxlBarline mxlBarline)
  {
  	MxlRightLeftMiddle location = mxlBarline.getLocation();
  	MxlRepeat repeat = mxlBarline.getRepeat();
  	int measureIndex = context.getMP().getMeasure();
  	BarlineStyle style = null;
  	if (mxlBarline.getBarStyle() != null)
  		style = BarlineStyleReader.read(mxlBarline.getBarStyle().getBarStyle());
  	if (repeat != null)
  	{
  		//repeat barline
	    if (location == MxlRightLeftMiddle.Left)
	    {
	    	//left barline
	    	if (repeat.getDirection() == MxlBackwardForward.Forward)
	    	{
	    		style = notNull(style, BarlineStyle.HeavyLight);
	    		context = context.withScore(ScoreController.writeColumnStartBarline(
	    			context.getScore(), measureIndex, createForwardRepeatBarline(style)));
	    	}
	    }
	    else if (location == MxlRightLeftMiddle.Right)
	    {
	    	//right barline
	    	if (repeat.getDirection() == MxlBackwardForward.Backward)
	    	{
	    		style = notNull(style, BarlineStyle.LightHeavy);
	    		int times = notNull(repeat.getTimes(), 1).intValue();
	    		context = context.withScore(ScoreController.writeColumnEndBarline(
	    			context.getScore(), measureIndex, createBackwardRepeatBarline(style, times)));
	    	}
	    }
  	}
  	else
  	{
  		//regular barline
  		style = notNull(style, BarlineStyle.Regular);
  		if (location == MxlRightLeftMiddle.Left)
	    {
  			//left barline
  			context = context.withScore(ScoreController.writeColumnStartBarline(
  				context.getScore(), measureIndex, createBarline(style)));
	    }
	    else if (location == MxlRightLeftMiddle.Right)
	    {
	    	//right barline
	    	context = context.withScore(ScoreController.writeColumnEndBarline(
	    		context.getScore(), measureIndex, createBarline(style)));
	    }
  	}
  	return context;
  }
  
  
  /**
	 * Reads the given direction element.
	 */
	private static MusicReaderContext readDirection(MusicReaderContext context,
		MxlDirection mxlDirection)
	{

		//staff
		int staff = notNull(mxlDirection.getStaff(), 1) - 1;

		//direction-types
		Words words = null;
		for (MxlDirectionType mxlType : mxlDirection.getDirectionTypes())
		{
			MxlDirectionTypeContent mxlDTC = mxlType.getContent();
			MxlDirectionTypeContentType mxlDTCType = mxlDTC.getDirectionTypeContentType();
			switch (mxlDTCType)
			{
				case Dynamics:
				{
					//dynamics
					DynamicsType type = ((MxlDynamics) mxlDTC).getElement();
					Dynamics dynamics = new Dynamics(type);
					context = context.writeMeasureElement(dynamics, staff);
					break;
				}
				case Pedal:
				{
					//pedal
					MxlPedal mxlPedal = (MxlPedal) mxlDTC;
					Pedal.Type type = null;
					switch (mxlPedal.getType())
					{
						case Start:
							type = Type.Start;
							break;
						case Stop:
							type = Type.Stop;
							break;
					}
					if (type != null)
					{
						Pedal pedal = new Pedal(type, readPosition(mxlPedal.getPrintStyle().getPosition(),
							context.getTenthMm(), context.getStaffLinesCount(staff)));
						context = context.writeMeasureElement(pedal, staff);
					}
					break;
				}
				case Wedge:
				{
					//wedge
					MxlWedge mxlWedge = (MxlWedge) mxlDTC;
					int number = mxlWedge.getNumber();
					Position pos = readPosition(mxlWedge.getPosition(),
						context.getTenthMm(), context.getStaffLinesCount(staff));
					switch (mxlWedge.getType())
					{
						case Crescendo:
							Wedge crescendo = new Crescendo(null, pos);
							context = context.writeMeasureElement(crescendo, staff);
							context = context.openWedge(number, crescendo);
							break;
						case Diminuendo:
							Wedge diminuendo = new Diminuendo(null, pos);
							context = context.writeMeasureElement(diminuendo, staff);
							context = context.openWedge(number, diminuendo);
							break;
						case Stop:
							Tuple2<MusicReaderContext, Wedge> t = context.closeWedge(number);
							context = t.get1();
							Wedge wedge = t.get2();
							if (wedge == null)
								throw new RuntimeException("Wedge " + (number + 1) + " is not open!");
							context = context.writeMeasureElement(wedge.getWedgeEnd(), staff);
							break;
					}
					break;
				}
				case Words:
				{
					//words (currently only one element is supported)
					if (words == null)
					{
						MxlWords mxlWords = (MxlWords) mxlDTC;
						MxlFormattedText mxlFormattedText = mxlWords.getFormattedText();
						FontInfo fontInfo = readFontInfo(mxlFormattedText.getPrintStyle().getFont());
						Position position = readPosition(mxlFormattedText.getPrintStyle().getPosition(),
							context.getTenthMm(), context.getStaffLinesCount(staff));
						words = new Words(mxlFormattedText.getValue(), fontInfo, position);
					}
					break;
				}
			}
		}
		
		//sound
		MxlSound mxlSound = mxlDirection.getSound();
		if (mxlSound != null)
		{
			//tempo
			if (mxlSound.getTempo() != null)
			{
				//always expressed in quarter notes per minute
				int quarterNotesPerMinute = mxlSound.getTempo().intValue();
				//if there were words found, use them for the tempo
				Tempo tempo;
				if (words != null)
				{
					tempo = new Tempo(fr(1, 4), quarterNotesPerMinute, words.getText(),
						words.getPosition());
					words = null; //words were used now
				}
				else
				{
					tempo = new Tempo(fr(1, 4), quarterNotesPerMinute, null, null);
				}
				//write to measure
				context = context.writeMeasureElement(tempo, staff);
			}
		}
		
		//if there are words that were not used for the tempo, write them now
		if (words != null)
		{
			context = context.writeMeasureElement(words, staff);
		}

		return context;
	}
  
  
}
