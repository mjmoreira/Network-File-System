package nfs.metaserver;

import nfs.shared.StorageInformation;

import java.util.Map;
import java.util.HashMap;

/**
 * StorageMapper permits obtaining a storage server's StorageInformation from
 * its storage server identifier. This allows a client to obtain the information
 * necessary to contact the storage server who owns a particular directory.
 */
class StorageMapper {
	Map<String, StorageInformation> storageIdMap;
	// Can also be used to map a cookie (only known to the storage server) to the
	// StorageInformation, as a way of authenticating the caller of the storage
	// remote methods, in order to prevent a storage server from altering other
	// storage's tree at the metadata server.

	StorageMapper() {
		storageIdMap = new HashMap<>();
	}

	void addStorageServer(StorageInformation storageInfo) {
		storageIdMap.put(storageInfo.storageId, storageInfo);
	}

	StorageInformation getStorageServer(String storageId) {
		return storageIdMap.get(storageId);
	}
}