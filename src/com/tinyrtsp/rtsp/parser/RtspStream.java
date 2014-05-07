package com.tinyrtsp.rtsp.parser;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tinyrtsp.rtsp.message.RtspMessage;

public class RtspStream implements Closeable {
	private InputStream in;
	private OutputStream out;
	
	public RtspStream(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}
	
	public void write(RtspMessage msg) throws IOException {
		System.out.println("--------------->");
		System.out.println(msg.toWireString());
		out.write(msg.toWire());
		out.flush();
	}
	
	public RtspMessage read() throws IOException, RtspParseException {
		StringBuilder message = new StringBuilder();
		int state = 0;
		RtspMessage msg = null;
		
		for (;;) {
			int ch = in.read();
			if (ch == -1) {
				return null;
			}
			char c = (char) ch;
			
			// CRLFCRLF ends the packet
			if (state == 0 || state == 2) {
				if (c == '\r') {
					state++;
				}
				else {
					state = 0;
				}
			}
			else {
				if (c == '\n') {
					state++;
				}
				else {
					state = 0;
				}
			}
			
			message.append(c);
			
			// End of RTSP header
			if (state == 4) {
				msg = RtspParser.parseMessageNoPayload(message.toString());
				
				// Read payload if it exists
				String lenStr = msg.getOption("Content-Length");
				if (lenStr != null) {
					int len;
					try {
						len = Integer.parseInt(lenStr);
					} catch (NumberFormatException e) {
						throw new RtspParseException("Invalid Content-Length in header");
					}
					
					byte[] payload = new byte[len];
					int offset = 0;
					do {
						offset += in.read(payload, offset, len - offset);
					} while (offset < len);
					
					msg.setPayload(new String(payload, "IBM437"));
				}
				
				System.out.println("<---------------");
				System.out.println(msg.toWireString());
				return msg;
			}
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
		out.close();
	}
}
