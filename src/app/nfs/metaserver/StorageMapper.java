package nfs.metaserver;

import nfs.shared.StorageInformation;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

/**
 * StorageMapper permits obtaining a storage server's StorageInformation from
 * its storage server identifier. This allows a client to obtain the information
 * necessary to contact the storage server who owns a particular directory.
 */
class StorageMapper {
	Map<String, StorageInformation> storageIdMap;
	// A storageId is only here while the registration is not complete.
	Set<String> validStorageIds;
	

	// Can also be used to map a cookie (only known to the storage server) to the
	// StorageInformation, as a way of authenticating the caller of the storage
	// remote methods, in order to prevent a storage server from altering other
	// storage's tree at the metadata server.

	StorageMapper() {
		storageIdMap = new HashMap<>();
		validStorageIds = new HashSet<>();
	}

	boolean addStorageServer(StorageInformation storageInfo) {
		if (storageInfo == null || !validStorageId(storageInfo.storageId)) {
			return false;
		}
		validStorageIds.remove(storageInfo.storageId);
		storageIdMap.put(storageInfo.storageId, storageInfo);
		return true;
	}

	StorageInformation getStorageServer(String storageId) {
		return storageIdMap.get(storageId);
	}

	String getNewStorageId() {
		String id = "Storage-" + System.nanoTime();
		validStorageIds.add(id);
		return id;
	}

	boolean validStorageId(String storageId) {
		return validStorageIds.contains(storageId);
	}
}