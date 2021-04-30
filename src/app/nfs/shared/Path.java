package nfs.shared;

import nfs.filesystem.File;

/**
 * All paths must be canonical. The root is represented as "".
 */
public class Path {
	private static final String SEPARATOR = "/";

	/**
	 * Validate canonical path.
	 * @param path
	 * @return
	 */
	public static boolean validPathString(String[] path) {
		if (path.length == 0 || path[0].length() != 0) {
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
	 * Validate canonical path.
	 * @param path
	 * @return
	 */
	public static boolean validPathString(String path) {
		return validPathString(path.split(SEPARATOR));
	}

	/**
	 * Validate canonical path and split it in its components.
	 * @param path
	 * @return String[] with path if valid, null if invalid
	 */
	public static String[] parsePathString(String path) {
		String[] parts = path.split(SEPARATOR);
		if (!validPathString(parts)) {
			return null;
		}
		return parts;
	}

	public static String pathString(String[] path) {
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

	public static String pathString(String[] path, String name) {
		return pathString(path) + SEPARATOR + name;
	}
}
