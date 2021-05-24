package nfs.filesystem;

import nfs.shared.NFSPath;
import nfs.shared.LsInfo;
import nfs.shared.ReturnStatus;
import static nfs.shared.ReturnStatus.*;

public final class Filesystem {
	private Directory root;


	public Filesystem() {
		root = Directory.createRootDirectory();
	}


	public ReturnStatus createFile(String[] path, long size) {
		if (!NFSPath.isValidPath(path)) {
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

	public ReturnStatus createStorageDirectory(String[] path, String storageId) {
		if (!NFSPath.isValidPath(path)) {
			return FAILURE_INVALID_PATH;
		}
		if (path.length > 2) { // storage root directory must be a child of root.
			return FAILURE_STORAGE_MOUNT_TOO_DEEP;
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

	public ReturnStatus createDirectory(String[] path) {
		if (!NFSPath.isValidPath(path)) {
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
		if (!NFSPath.isValidPath(path)) {
			return null;
		}
		
		if (path.length == 1) {
			if (path[0] == "") {
				return root.generateLsInfo();
			}
			else {
				return null;
			}
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

	public boolean exists(String[] path) {
		if (!NFSPath.isValidPath(path)) {
			return false;
		}

		// path is just root
		if (path.length == 1) {
			if (path[0] == "") {
				return true;
			}
			else {
				return false;
			}
		}

		// Traverse the path until we reach the parent directory of
		// path[path.length -1], path[path.length - 2]
		Directory parent = Directory.navigatePath(root, path, path.length - 1);
		if (parent == null) {
			// Path does not exist.
			return false;
		}

		// Check if the path leads to a directory.
		Directory dir = parent.getChildDirectory(path[path.length - 1]);
		if (dir != null) {
			return true;
		}
		else {
			if (parent.getChildFile(path[path.length - 1]) == null) {
				// It is neither a directory nor a file
				return false;
			}
			else {
				return true;
			}
		}
	}
}
