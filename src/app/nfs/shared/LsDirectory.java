package nfs.shared;

import java.io.Serializable;

// retornado pelo servidor de metadados como resposta ao list dir
public class LsDirectory implements Serializable {
	private static final long serialVersionUID = 20210428001L;

	public final String name;
	public final String server;
	public LsInfo parent;

	public LsDirectory(String name, String server) {
		this.name = name;
		this.server = server;
		this.parent = null;
	}

	void setLsInfo(LsInfo parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "<" + name + "@" + server + ">";
	}
}
