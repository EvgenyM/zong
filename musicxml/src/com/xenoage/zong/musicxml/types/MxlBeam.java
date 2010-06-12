package com.xenoage.zong.musicxml.types;

import static com.xenoage.util.NullTools.notNull;
import static com.xenoage.util.xml.XMLWriter.addAttribute;
import static com.xenoage.util.xml.XMLWriter.addElement;
import static com.xenoage.zong.musicxml.util.Parse.parseAttrIntNull;

import org.w3c.dom.Element;

import com.xenoage.util.annotations.NeverNull;
import com.xenoage.zong.musicxml.types.enums.MxlBeamValue;
import com.xenoage.zong.musicxml.util.IncompleteMusicXML;


/**
 * MusicXML beam.
 * 
 * @author Andreas Wenger
 */
@IncompleteMusicXML(missing="repeater,fan,color")
public final class MxlBeam
{
	
	public static final String ELEM_NAME = "beam";
	
	@NeverNull private final MxlBeamValue value;
	private final int number;
	
	private static final int defaultNumber = 1;
	
	
	public MxlBeam(MxlBeamValue value, int number)
	{
		this.value = value;
		this.number = number;
	}


	@NeverNull public MxlBeamValue getValue()
	{
		return value;
	}
	

	public int getNumber()
	{
		return number;
	}
	
	
	@NeverNull public static MxlBeam read(Element e)
	{
		return new MxlBeam(MxlBeamValue.read(e),
			notNull(parseAttrIntNull(e, "number"), defaultNumber));
	}
	
	
	public void write(Element parent)
	{
		Element e = addElement(ELEM_NAME, parent);
		value.write(e);
		addAttribute(e, "number", number);
	}
	
	

}
