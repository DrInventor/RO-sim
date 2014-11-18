package es.oeg.om.util;

import org.xml.sax.SAXException;

public class BreakParsingException extends SAXException {

	private static final long serialVersionUID = 1L;

	public BreakParsingException() {
		super();
	}

	public BreakParsingException(Exception e) {
		super(e);

	}

	public BreakParsingException(String message, Exception e) {
		super(message, e);

	}

	public BreakParsingException(String message) {
		super(message);

	}

}
