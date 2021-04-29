package nfs.filesystem;

import nfs.shared.Path;
import nfs.shared.LsInfo;
import static nfs.shared.Constants.ReturnStatus.*;

public final class Filesystem {
	private Directory root;


	public Filesystem() {
		root = Directory.createRootDirectory();
	}


	public int createFile(String[] path, long size) {
		if (!Path.validPathString(path)) {
			return FAILURE_INVALID_PATH;
		}
		
		Directory parent = Directory.navigatePath(root, path, path.length - 1);
		if (parent == null) {
			return FAILURE_PATH_DOES_NOT_EXIST;
		}
		if (parent.createChildFile(path[path.length - 1], size) == null) {
			return FAILURE_FILE_ALREADY_EXISTS;
		}
		return SUCCESS;
	}

	public int createDirectory(String[] path) {
		if (!Path.validPathString(path)) {
			return FAILURE_INVALID_PATH;
		}

		Directory parent = Directory.navigatePath(root, path, path.length - 1);
		if (parent == null) {
			return FAILURE_PATH_DOES_NOT_EXIST;
		}
		if (parent.createChildDirectory(path[path.length - 1]) == null) {
			return FAILURE_DIRECTORY_ALREADY_EXISTS;
		}
		return SUCCESS;
	}

	public LsInfo listDirectory(String[] path) {
		if (!Path.validPathString(path)) {
			return null;
		}
		
		Directory parent = Directory.navigatePath(root, path, path.length - 1);
		// Path does not exist.
		if (parent == null) {
			return null;
		}
		// Check if the path leads to a directory
		Directory dir = parent.getChildDirectory(path[path.length - 1]);
		// If the path leads to a file, the parent directory is listed.
		if (dir == null) {
			// It is neither a directory nor a file
			if (parent.getChildFile(path[path.length - 1]) == null) {
				return null;
			}
			// Path leads to a file
			dir = parent;
		}
		
		return dir.generateLsInfo();
	}
}
