package com.tinyrtsp.rtsp.parser;

import java.io.IOException;

public class RtspParseException extends IOException {
	private static final long serialVersionUID = 1L;
	
	public RtspParseException(String message) {
		super(message);
	}
}
