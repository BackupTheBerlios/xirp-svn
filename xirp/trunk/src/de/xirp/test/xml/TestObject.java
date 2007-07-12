package de.xirp.test.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * @author Matthias Gernand
 *
 */
@XmlRootElement(name="main") //$NON-NLS-1$
public class TestObject {
	
	@XmlAttribute(name="homo") //$NON-NLS-1$
	String mainAtt = "moho"; //$NON-NLS-1$
	
	@XmlElement(name="dases") //$NON-NLS-1$
	List<Das> dases = new ArrayList<Das>();
	
	public TestObject() {
		dases.add(new Das());
		dases.add(new Das());
	}
	
	public static void main(String args[]) {
		try {
			JAXBContext jc = JAXBContext.newInstance(TestObject.class);
			Marshaller m = jc.createMarshaller( );
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(new TestObject(), System.out);
		}
		catch (JAXBException e) {
			e.printStackTrace( );
		}
	}
}

@XmlType(name="das") //$NON-NLS-1$
class Das {
	
	@XmlAttribute(name="der") //$NON-NLS-1$
	String der = "der"; //$NON-NLS-1$
	
	@XmlValue
	String die = "die"; //$NON-NLS-1$
	
	@XmlAttribute(name="sowassoundso") //$NON-NLS-1$
	private Enum e = Enum.WARUM;
	
	@XmlEnum(String.class)
	enum Enum {
		DER,
		DIE,
		DAS,
		WER,
		WIE,
		WAS,
		WIESO,
		WEßHALB,
		WARUM
	}
}
