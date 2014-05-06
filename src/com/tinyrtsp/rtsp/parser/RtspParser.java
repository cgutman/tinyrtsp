package com.tinyrtsp.rtsp.parser;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.tinyrtsp.rtsp.message.RtspMessage;
import com.tinyrtsp.rtsp.message.RtspRequest;
import com.tinyrtsp.rtsp.message.RtspResponse;

public class RtspParser {
	
	private static String parseToEndOfLine(Scanner scan) throws RtspParseException {
		if (scan.hasNextLine()) {
			return scan.nextLine();
		}
		else {
			throw new RtspParseException("Unexpected end of packet");
		}
	}
	
	private static String parseNext(Scanner scan) throws RtspParseException {
		if (scan.hasNext()) {
			return scan.next();
		}
		else {
			throw new RtspParseException("Unexpected end of packet");
		}
	}
	
	private static Map.Entry<String, String> parseOptionNoPayload(Scanner scan) throws RtspParseException {
		String key = parseNext(scan);
		if (!key.endsWith(":")) {
			throw new RtspParseException("Invalid key");
		}
		else {
			key = key.substring(0, key.length()-1);
		}
		
		String value = parseToEndOfLine(scan).trim();
		
		return new AbstractMap.SimpleEntry<String, String>(key, value);
	}
	
	public static HashMap<String, String> parsePayload(Scanner scan) throws RtspParseException {
		HashMap<String, String> map = new HashMap<String, String>();
		
		while (scan.hasNext()) {
			Map.Entry<String, String> option = parseOptionNoPayload(scan);
			map.put(option.getKey(), option.getValue());
		}
		
		return map;
	}
	
	public static String generatePayload(HashMap<String, String> map) {
		StringBuilder builder = new StringBuilder();
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			builder.append(entry.getKey());
			builder.append(": ");
			builder.append(entry.getValue());
			builder.append("\r\n");
		}
		
		return builder.toString();
	}
	
	public static RtspMessage parseMessageNoPayload(String message) throws RtspParseException {
		Scanner scan = new Scanner(message);
		RtspMessage msg;
		
		// Determine whether this is a request or response
		String first = parseNext(scan);
		String protocol;
		if (first.startsWith("RTSP")) {
			protocol = first;
			
			int statusCode;
			try {
				statusCode = Integer.parseInt(parseNext(scan));
			} catch (NumberFormatException e) {
				throw new RtspParseException("Invalid status code");
			}
			String statusStr = parseToEndOfLine(scan);
			
			msg = new RtspResponse(protocol, statusCode, statusStr, 0, null, null);
		}
		else {
			String command = first;
			String target = parseNext(scan);
			protocol = parseNext(scan);
			
			msg = new RtspRequest(command, target, protocol, 0, null, null);
		}
		
		// Make sure protocol is valid
		if (!protocol.equals("RTSP/1.0")) {
			throw new RtspParseException("Invalid protocol: "+protocol);
		}
		
		// Parse remaining options
		while (scan.hasNext()) {
			Map.Entry<String, String> option = parseOptionNoPayload(scan);
			msg.setOption(option.getKey(), option.getValue());
		}
		
		// Grab the sequence number
		String seq = msg.getOption("CSeq");
		if (seq == null) {
			throw new RtspParseException("Missing CSeq option");
		}
		else {
			try {
				msg.setSequenceNumber(Integer.parseInt(seq));
			} catch (NumberFormatException e) {
				throw new RtspParseException("Invalid CSeq option");
			}
		}
		
		return msg;
	}
}
