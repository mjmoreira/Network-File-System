package nfs.filesystem;

import java.util.LinkedList;
import java.util.regex.Pattern;

import nfs.shared.LsFile;

public final class File {

	/**
	 * Must have size >= 1.
	 * Must not begin or end with a space.
	 * Alphanumeric . and _ characters only.
	 */
	private static final Pattern namePattern =
		Pattern.compile("[a-zA-Z0-9_.]+([a-zA-Z0-9_ .]*[a-zA-Z0-9_.]+)*");

	public static boolean validName(String name) {
		return !name.equals(".")
		       && !name.equals("..")
		       && namePattern.matcher(name).matches();
	}


	private String name;
	private long size;
	private Directory parent;

	public File(String name, long size, Directory parent)
		throws InvalidNameException, InvalidSizeException {
		if (!validName(name)) {
			throw new InvalidNameException(name);
		}
		if (size < 0) {
			throw new InvalidSizeException("File size must be >= 0.");
		}
		this.name = name;
		this.size = size;
		this.parent = parent;
	}

	public String[] getCanonicalPath() {
		LinkedList<String> path = new LinkedList<>();
		path.add(name);
		Directory dir = parent;
		while (dir != null) {
			path.addFirst(dir.getName());
			dir = dir.getParent();
		}
		String[] r = new String[path.size()];
		return path.toArray(r);
	}

	public String getCanonicalPathString() {
		String[] array = getCanonicalPath();
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < array.length - 1; i++) {
			b.append(array[i]);
			b.append('/');
		}
		b.append(array[array.length - 1]);
		return b.toString();
	}

	@Override
	public String toString() {
		return "[File: " + getCanonicalPathString() + "]";
	}

	public LsFile getLsFile() {
		return new LsFile(name, size);
	}
}
