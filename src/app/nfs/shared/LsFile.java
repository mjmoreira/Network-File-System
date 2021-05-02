package nfs.shared;

import java.io.Serializable;

public class LsFile implements Serializable, Comparable<LsFile> {
	private static final long serialVersionUID = 20210428001L;

	public final String name;
	public final long size;
	public LsInfo parent;

	public LsFile(String name, long size) {
		this.name = name;
		this.size = size;
		this.parent = null;
	}

	void setLsInfo(LsInfo parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "«" + name + " #" + size + "»";
	}

	public int compareTo(LsFile o) {
		return this.name.compareTo(o.name);
	}
}
