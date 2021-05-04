package nfs.shared;

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

	private static final String SEPARATOR = "/";

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
	 * Check if canonical path is valid.
	 * @param path
	 * @return
	 */
	public static boolean isValidPath(String path) {
		// FIXME: //, ///, ////, ... are considered valid.
		String[] parts = convertPath(path);
		return isValidPath(parts);
	}

	/**
	 * Validate canonical path and split it in its components.
	 * @param path
	 * @return String[] with path if valid, null if invalid
	 */
	public static String[] convertPath(String path) {
		if (path.length() == 0) {
			return null;
		}
		String[] parts = path.split(SEPARATOR);
		// If the path is only "/", split returns empty array
		if (parts.length == 0) {
			return new String[] {""};
		}
		if (parts[0].length() == 0 // root is ""
			&& isValidPath(parts)) {
			return parts;
		}
		return null;
	}

	public static String convertPath(String[] path) {
		StringBuilder b = new StringBuilder();
		for (String s: path) {
			if (s.length() == 0) {
				b.append(SEPARATOR);
			}
			else {
				b.append(s);
				b.append(SEPARATOR);
			}
		}
		return b.toString();
	}

	public static String joinPath(String[] path, String name) {
		return convertPath(path) + name;
	}
}
