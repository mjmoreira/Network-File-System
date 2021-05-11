package nfs.shared;

import java.io.Serializable;

public class StorageLocation implements Serializable {
	private static final long serialVersionUID = 20210511001L;

	public final String storageId;
	public final String address;
	public final int registryPort;
	public final String mountName;

	public StorageLocation(String storageId, String address, int registryPort,
	                       String mountName) {
		this.storageId = storageId;
		this.address = address;
		this.registryPort = registryPort;
		this.mountName = mountName;
	}
}
