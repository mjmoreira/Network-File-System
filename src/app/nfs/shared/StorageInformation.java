package nfs.shared;

import java.io.Serializable;

/**
 * The StorageInformation contains all the information the Storage server needs
 * to provide to the Metadata server, to successfully creating a new directory
 * managed by a Storage server.
 */
public class StorageInformation implements Serializable {
	private static final long serialVersionUID = 20210511001L;

	public final String storageId;
	public final String address;
	public final int registryPort;
	public final String mountName;

	/**
	 * 
	 * @param storageId the unique identifier that will identify the storage server.
	 * @param address the address of the storage server.
	 * @param registryPort the port of the RMI registry where the server is registered.
	 * @param mountName the name of the directory which will serve as the root
	 * for the storage server's management area, that is, files and directories
	 * stored at the root of the storage server (/) will be located at
	 * /path/to/mountName/ in the metadata server tree.
	 */
	public StorageInformation(String storageId, String address, int registryPort,
	                          String mountName) {
		this.storageId = storageId;
		this.address = address;
		this.registryPort = registryPort;
		this.mountName = mountName;
	}
}
