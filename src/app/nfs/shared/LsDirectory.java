package nfs.shared;

import java.io.Serializable;

/**
 * Used in the response (LsInfo) of the requests of the client to list a
 * directory.
 */
public class LsDirectory implements Serializable, Comparable<LsDirectory> {
	private static final long serialVersionUID = 20210428001L;

	public final String name;
	public final String storageId;
	public LsInfo parent;

	public LsDirectory(String name, String server) {
		this.name = name;
		this.storageId = server;
		this.parent = null;
	}

	void setLsInfo(LsInfo parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "<" + name + " @" + storageId + ">";
	}

	public int compareTo(LsDirectory o) {
		return this.name.compareTo(o.name);
	}
}
