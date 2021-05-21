package com.ibm.au.optim.suro.core.composer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * A class which holds any number of objects in a grouping together.
 *
 * @author brendanhaesler
 */
public class NTuple {

	/**
	 * The elements of the tuple
	 */
	private Object[] elements;

	/**
	 * Logger to log events within this class
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(NTuple.class);

	private static int DEFAULT_TUPLE_MAX_LENGTH = 8192;

	public static boolean hasNextNTuple(Reader reader) throws IOException {
		return hasNextNTuple(reader, DEFAULT_TUPLE_MAX_LENGTH);
	}

	public static boolean hasNextNTuple(Reader reader, int tupleMaxLength) throws IOException {
		// check if marking is permitted
		if (!reader.markSupported()) {
			throw new IOException("Reader does not support marking, unable to maintain reader integrity.");
		}

		// mark the position in the reader so we can reset it if there isn't a tuple
		reader.mark(tupleMaxLength);

		// note whether we've found the start of a tuple yet
		boolean tupleStarted = false;
		// count how many unmatched left brackets there are
		int unmatchedBrackets = 0;
		// count how many characters we've read
		int charsRead = 0;

		// while the tuple hasn't started, or while there are unmatched brackets
		while (!tupleStarted || unmatchedBrackets != 0) {
			// ensure we don't pass the mark/reset limit
			if (charsRead == tupleMaxLength) {
				reader.reset();
				throw new IOException("Potential tuple was longer length than supplied limit of " + tupleMaxLength);
			}

			// read a character
			char c;

			try {
				c = (char) reader.read();
			} catch (IOException e) {
				// whatever caused the exception indicates there is not a valid next tuple
				LOGGER.error("Stream exception while looking for tuple.", e);
				reader.reset();
				return false;
			}

			// increment characters read
			++charsRead;

			// if the character is not whitespace
			if (!Character.isWhitespace(c)) {
				// if it's an open bracket
				if (c == '<') {
					// increase the bracket count and set the tuple as started
					++unmatchedBrackets;
					tupleStarted = true;
				} else if (tupleStarted) {
					// if the tuple is started, and if the character is a close bracket
					if (c == '>') {
						--unmatchedBrackets;
					}
				} else {
					// it wasn't whitespace, or an open bracket, and the tuple isn't yet started, so the tuple is invalid
					reader.reset();
					return false;
				}
			}
		}

		// if we exited the while loop naturally, it's because the angle brackets matched up, so we have a valid tuple
		reader.reset();
		return true;
	}

	/**
	 * Static method to read a tuple using a given reader.
	 * @param reader The reader to read the tuple from.
	 * @return The NTuple read.
	 * @throws IOException If the stream under the reader does not contain a properly formatted tuple, or if the reader
	 * throws any exceptions.
	 */
	public static NTuple readTuple(Reader reader) throws IOException {
		return readTuple(reader, false);
	}

	/**
	 * Static method to read a tuple using a given reader.
	 * @param reader The reader to read the tuple from.
	 * @param tupleStarted Whether or not the opening '<' has already been read.
	 * @return The NTuple read.
	 * @throws IOException If the stream under the reader does not contain a properly formatted tuple, or if the reader
	 * throws any exceptions.
	 */
	private static NTuple readTuple(Reader reader, boolean tupleStarted) throws IOException {
		// List to hold elements read
		ArrayList<Object> elements = new ArrayList<>();

		// string to hold each token
		StringBuilder token = new StringBuilder();

		// boolean to mark if we are reading a string
		boolean inString = false;

		// loop infinitely. Loop is ended by return or exception
		while (true) {
			// read the next character
			char c = (char) reader.read();

			// if the tuple hasn't started
			if (!tupleStarted) {
				// check if the tuple is starting
				if (c == '<') {
					tupleStarted = true;
					continue;
				} else {
					// consume whitespace
					if (Character.isWhitespace(c)) {
						continue;
					} else {
						// the tuple isn't starting and it's not whitespace, so throw an exception
						throw new IOException("Badly formatted tuple.");
					}
				}
			}

			// if the character is a double-quote, flip the state of inString
			if (c == '"') {
				// add the double quote to the token. This is needed to properly fix object types later
				token.append(c);
				inString = !inString;
				continue;
			}

			// if we're in a string, just append the character to the token and move on
			if (inString) {
				token.append(c);
				continue;
			}

			// consume whitespace
			if (Character.isWhitespace(c)) {
				// if there is a non-empty token, add it
				if (token.length() > 0) {
					elements.add(parseToken(token.toString()));
					token = new StringBuilder();
				}

				continue;
			}

			// deal with nested tuples
			if (c == '<') {
				elements.add(readTuple(reader, true));
				continue;
			}

			// deal with finishing the tuple
			if (c == '>') {
				// add the last token if non-empty
				if (token.length() > 0) {
					elements.add(parseToken(token.toString()));
				}

				return new NTuple(elements.toArray());
			}

			// no special cases, append the character to the current token
			token.append(c);
		}
	}

	/**
	 * Helper method to parse tokens read as more accurate types
	 * @param token The token to parse
	 * @return A more specific representation of the token, or the original token if no more specific type was found
	 */
	private static Object parseToken(String token) {
		Object result = token;

		// check for quotes (indicates a semantic string, as opposed to a number that is currently stored as a string)
		if (token.charAt(0) == '"') {
			// store it back sans quotes
			result = token.substring(1, token.length() - 1);
		} else {
			// try parsing it as a number
			result = parseNumber(token);
		}

		return result == null ? token : result;
	}

	private static Number parseNumber(String token) {
		Number number = null;
		try {
			number = Byte.parseByte(token);
		} catch (NumberFormatException e1) {
			try {
				number = Short.parseShort(token);
			} catch (NumberFormatException e2) {
				try {
					number = Integer.parseInt(token);
				} catch (NumberFormatException e3) {
					try {
						number = Long.parseLong(token);
					} catch (NumberFormatException e4) {
						try {
							number = Float.parseFloat(token);
						} catch (NumberFormatException e5) {
							try {
								number = Double.parseDouble(token);
							} catch (NumberFormatException e6) { /* do nothing */ }
						}
					}
				}
			}
		}

		return number;
	}

	/**
	 * Constructs a new instance of the NTuple class.
	 * @param elements The data items for this tuple.
	 */
	public NTuple(Object... elements) {
		this.elements = elements;
	}

	public Object getElement(int index) {
		return elements[index];
	}

	/**
	 * Gets the elements of the tuple.
	 * @return An Object array of elements.
	 */
	public Object[] getElements() {
		return elements;
	}

	/**
	 * Sets the elements of the tuple.
	 * @param elements An array containing the elements
	 */
	public void setElements(Object... elements) {
		this.elements = elements;
	}

	/**
	 * Returns a String representation of the tuple.
	 * @return The elements of the tuple inside angle brackets, delimited by spaces
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("<");

		for (int i = 0; i < elements.length; ++i) {
			if (elements[i] instanceof String) {
				result.append("\"")
								.append(elements[i])
								.append("\"");
			} else {
				result.append(elements[i]);
			}

			if (i + 1 < elements.length) {
				result.append(" ");
			}
		}

		result.append(">");
		return result.toString();
	}
}