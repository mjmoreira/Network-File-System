package nfs.shared;

import java.io.Serializable;
import java.util.Arrays;

// Retornado pelo servidor de metadados como resposta ao listdir()
public class LsInfo implements Serializable {
	private static final long serialVersionUID = 20210428001L;

	public final String[] path; // path to directory, path[.length - 1] = name
	public final String name; // directory name
	public final String storageId;
	public final LsDirectory[] directories;
	public final LsFile[] files;

	public LsInfo(String[] path, String name, String storageId,
			LsDirectory[] directories, LsFile[] files) {
		this.path = path;
		this.name = name;
		this.storageId = storageId;
		this.directories = directories;
		this.files = files;
		if (this.directories != null) {
			for (LsDirectory d: this.directories) {
				d.setLsInfo(this);
			}
		}
		if (this.files != null) {
			for (LsFile f: this.files) {
				f.setLsInfo(this);
			}
		}
	}

	public boolean hasFile(String fileName) {
		if (fileName == null || files == null) {
			return false;
		}
		for (LsFile f: files) {
			if (fileName.equals(f.name)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasDirectory(String directoryName) {
		if (directoryName == null || directories == null) {
			return false;
		}
		for (LsDirectory d: directories) {
			if (directories.equals(d.name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "[Path: " + NFSPath.convertPath(path)
		        + ", storage: " + storageId
		        + ", directories: " + Arrays.toString(directories)
		        + ", files: " + Arrays.toString(files)
		        + "]";
	}
}
