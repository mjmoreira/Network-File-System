package nfs.filesystem;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.LinkedList;

import nfs.shared.LsDirectory;
import nfs.shared.LsInfo;
import nfs.shared.LsFile;

public final class Directory {
	/**
	 * Create a root Directory. The root is characterized by the name "", and
	 * a null parent Directory.
	 * @return a Directory to be used as root.
	 */
	public static Directory createRootDirectory() {
		return new Directory();
	}

	/**
	 * Traverse path from root until the directory named path[navigateLength - 1]
	 * and return it. Only checks Directory's.
	 * 
	 * for example:
	 *   path: { "", "1", "2", "3" },
	 *   navigateLength: 3,
	 *   return: Directory "2".
	 * @param path
	 * @param navigateLength Traverse path from root to the directory named
	 * path[navigateLength - 1]
	 * @return Directory if path exists, null otherwise.
	 */
	public static Directory navigatePath(Directory root, String[] path,
			int navigateLength) {
		// path must always start with root, "".
		if (path.length == 0 || navigateLength < 0 || path[0].length() != 0) {
			return null;
		}
		Directory current = root;
		for (int i = 1; i < path.length && i < navigateLength; i++) {
			current = current.directories.get(path[i]);
			if (current == null) {
				break;
			}
		}
		return current;
	}

	private String name;
	private String storageId; // name of the storage that holds this directory
	private Directory parent;
	private SortedMap<String,Directory> directories;
	private SortedMap<String,File> files;

	// to create non root directories
	// Não deve ser usado diretamente. Tem que se usar createChildDirectory()
	private Directory(String name, Directory parent, String serverName)
			throws InvalidNameException {
		if (!File.validName(name)) {
			throw new InvalidNameException(name);
		}
		if (parent == null) {
			throw new NullPointerException("Parent directory must be non null.");
		}
		this.name = name;
		this.storageId = serverName;
		this.parent = parent;
		this.directories = new TreeMap<>();
		this.files = new TreeMap<>();
	}

	// to create root
	private Directory() {
		this.name = "";
		this.storageId = null;
		this.parent = null;
		this.directories = new TreeMap<>();
		this.files = new TreeMap<>();
	}

	public String getName() {
		return name;
	}

	public Directory getParent() {
		return parent;
	}

	public String getStorageId() {
		return storageId;
	}

	public Directory getChildDirectory(String name) {
		return directories.get(name);
	}

	public File getChildFile(String name) {
		return files.get(name);
	}

	public Directory createChildDirectory(String name)
			throws InvalidNameException {
		if (directories.containsKey(name) || files.containsKey(name)) {
			return null;
		}
		Directory d = new Directory(name, this, this.storageId);
		directories.put(name, d);
		return d;
	}

	public Directory createChildDirectory(String name, String storageId)
			throws InvalidNameException {
		if (directories.containsKey(name) || files.containsKey(name)) {
			return null;
		}
		Directory d = new Directory(name, this, storageId);
		directories.put(name, d);
		return d;
	}

	public File createChildFile(String name, long fileSize)
			throws InvalidNameException, InvalidSizeException {
		if (directories.containsKey(name) || files.containsKey(name)) {
			return null;
		}

		File f = new File(name, fileSize, this);
		files.put(name, f);
		return f;
	}

	public String[] getCanonicalPath() {
		LinkedList<String> path = new LinkedList<>();
		path.add(name);
		Directory dir = parent;
		while (dir != null) {
			path.addFirst(dir.name);
			dir = dir.parent;
		}
		String[] r = new String[path.size()];
		return path.toArray(r);
	}

	public String getCanonicalPathString() {
		String[] array = getCanonicalPath();
		StringBuilder b = new StringBuilder();
		for (String dir: array) {
			b.append(dir);
			b.append('/');
		}
		return b.toString();
	}

	@Override
	public String toString() {
		return "[Directory: " + getCanonicalPathString() + "]";
	}

	public LsDirectory getLsDirectory() {
		return new LsDirectory(name, storageId);
	}

	/**
	 * If there are no files or directories, the array has size 0.
	 * @return
	 */
	public LsInfo generateLsInfo() {
		LsFile[] lsFiles = new LsFile[files.size()];
		int i = 0;
		for (File f: files.values()) {
			lsFiles[i] = f.getLsFile();
			i++;
		}

		LsDirectory[] lsDirectories = new LsDirectory[directories.size()];
		i = 0;
		for (Directory d: directories.values()) {
			lsDirectories[i] = d.getLsDirectory();
			i++;
		}

		return new LsInfo(this.getCanonicalPath(), this.name, this.storageId,
		                  lsDirectories, lsFiles);
	}
}
