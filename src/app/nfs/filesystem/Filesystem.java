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
		if (!Path.isValidPath(path)) {
			return FAILURE_INVALID_PATH;
		}
		if (path.length < 3) { // No files in root.
			return FAILURE_PATH_NOT_OWNED_BY_STORAGE;
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

	public int createStorageDirectory(String[] path, String storageId) {
		if (!Path.isValidPath(path)) {
			return FAILURE_INVALID_PATH;
		}
		if (path.length > 2) { // storage root directory must be a child of root.
			return FAILURE_PATH_TOO_DEEP;
		}
		Directory parent = Directory.navigatePath(root, path, path.length - 1);
		if (parent == null) {
			return FAILURE_PATH_DOES_NOT_EXIST;
		}
		if (parent.createChildDirectory(path[path.length-1], storageId)==null) {
			return FAILURE_DIRECTORY_ALREADY_EXISTS;
		}
		return SUCCESS;
	}

	public int createDirectory(String[] path) {
		if (!Path.isValidPath(path)) {
			return FAILURE_INVALID_PATH;
		}

		Directory parent = Directory.navigatePath(root, path, path.length - 1);
		if (parent == null) {
			return FAILURE_PATH_DOES_NOT_EXIST;
		}
		if (parent.getStorageId() == null) {
			return FAILURE_PATH_NOT_OWNED_BY_STORAGE;
		}
		if (parent.createChildDirectory(path[path.length - 1]) == null) {
			return FAILURE_DIRECTORY_ALREADY_EXISTS;
		}
		return SUCCESS;
	}

	public LsInfo listDirectory(String[] path) {
		if (!Path.isValidPath(path)) {
			return null;
		}
		
		// Traverse the path until we reach the parent directory of
		// path[path.length -1].
		// The objective is to provide a listing of the parent directory if the
		// path "path" leads to a file, because the information of the file
		// is contained in the listing of his parent directory.

		// "parent" contains the directory with name path[path.length - 2].
		Directory parent = Directory.navigatePath(root, path, path.length - 1);
		// Path does not exist.
		if (parent == null) {
			return null;
		}
		// Special case: "parent" is the actual directory to be listed.
		if (parent == root && path.length == 1) {
			return parent.generateLsInfo();
		}
		// Check if the path leads to a directory.
		Directory dir = parent.getChildDirectory(path[path.length - 1]);
		// If the path leads to a file, the parent directory is listed.
		if (dir == null) {
			// It is neither a directory nor a file
			if (parent.getChildFile(path[path.length - 1]) == null) {
				return null;
			}
			// Path leads to a file.
			dir = parent;
		}

		return dir.generateLsInfo();
	}
}
