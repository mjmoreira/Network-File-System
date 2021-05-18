package nfs.shared;

import java.util.LinkedList;
import java.util.regex.Pattern;

import nfs.filesystem.File;


/**
 * All paths must be canonical.
 */
public class Path {
	// Examples:
	//  ___________________________________________________
	// |       String       |           String[]           |
	// |--------------------|------------------------------|
	// | "/"                | [""]                         |
	// | "/dir1"            | ["", "dir1"]                 |
	// | "/dir1/dir2/file"  | ["", "dir1", "dir2", "file"] |
	// |____________________|______________________________|

	private static final char SEPARATOR = '/';

	/**
	 * Check if canonical path is valid.
	 * @param path
	 * @return
	 */
	public static boolean isValidPath(String[] path) {
		if (path == null || path.length == 0 || path[0].length() != 0) {
			return false;
		}
		for (int i = 1; i < path.length; i++) {
			if (!File.validName(path[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the canonical path is valid.
	 * Less efficient than using isValidPath(String[] path) if there is the
	 * intention of using String[] path later.
	 * @param path
	 * @return
	 */
	public static boolean isValidPath(String path) {
		String[] parts = splitPath(path);
		return isValidPath(parts);
	}

	/**
	 * Validate canonical path and split it in its components.
	 * @param path
	 * @return String[] with path if it is valid, null if it is invalid.
	 */
	public static String[] validateAndConvertPath(String path) {
		String[] parts = splitPath(path);
		if (isValidPath(parts)) {
			return parts;
		}
		return null;
	}

	/**
	 * Split argument string at the SEPARATOR, '/'.
	 * Examples (input -> output):
	 *   "" -> []
	 *   "/" -> [""]
	 *   "asdf" -> ["asdf"]
	 *   "//" -> ["", ""]
	 *   "/asdf" -> ["", "asdf"]
	 *   "/asdf/" -> ["", "asdf"]
	 *   "//asdf" -> ["", "", "asdf"]
	 * 
	 * The objective is to obtain an array that shows where there are '/', if
	 * there are consecutive '/' without other characters in the middle, and
	 * if the string is composed only of '/'.
	 * 
	 * String.split() has almost the desired behaviour, but does not handle
	 * strings of only '/', like "///", as desired, because the output is [].
	 * 
	 * @param path
	 * @return
	 */
	private static String[] splitPath(String path) {
		if (path == null) {
			return null;
		}

		LinkedList<String> l = new LinkedList<>();
		char[] p = path.toCharArray();

		//    /   /  aasdfawe /  asdfasdf7 /  asdfsdf (/)
		// ["", "", "aasdfawe", "asdfasdf7", "asdfsdf"]

		// "//aasdfawe/asdfasdf7/asdfsdf"
		//  ^start/last -> ""
		// "-/aasdfawe/asdfasdf7/asdfsdf"
		//   ^start/last -> ""
		// "--aasdfawe/asdfasdf7/asdfsdf"
		//    ^start  ^last -> aasdfawe
		//  ...
		// "---------------------asdfsdf"
		//                       ^start ^ last -> asdfsdf
		int start = 0, last = 0;
		StringBuilder b;
		while (start < p.length) {
			b = new StringBuilder();
			while (last < p.length && p[last] != SEPARATOR) {
				b.append(p[last]);
				last++;
			}
			l.addLast(b.toString());
			start = last + 1;
			last = start;
		}
		return l.toArray(new String[0]);
	}

	/**
	 * Convert path to String[] representation. Does not perform any validation.
	 * For example:
	 *   / -> [""]
	 *   /dir1/dir2/file -> ["", "dir1", "dir2", "file"]
	 * 
	 * @param path
	 * @return String[] with path split in its components; null if path is null.
	 */
	public static String[] convertPath(String path) {
		return splitPath(path);
	}

	public static String convertPath(String[] path) {
		StringBuilder b = new StringBuilder();
		for (String s: path) {
			b.append(s);
			b.append(SEPARATOR);
		}
		return b.toString();
	}

	public static String joinPath(String[] path, String name) {
		return convertPath(path) + name;
	}
}
