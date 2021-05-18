package nfs.storage;

import nfs.shared.Constants;

public class StorageServer {

	// Should receive as arguments the name of the file with the address of the
	// metadata server.
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Storage storage =
				Storage.createStorage("localhost",
				                      Constants.REGISTRY_PORT,
				                      Constants.METADATA_REGISTRY_ID,
				                      "storage1");
			
			storage.createDirectory(new String[] {"", "storage1", "dir1"});
			storage.createDirectory(new String[] {"", "storage1", "dir2"});
			storage.createFile(new String[] {"", "storage1", "f1"}, "contents1");
			storage.createFile(new String[] {"", "storage1", "f2"}, "cont2");
			storage.createFile(new String[] {"", "storage1", "dir1", "f3"}, "");
			
			System.out.println("StorageServer created.");
		} catch (Exception e) {
			System.err.println("StorageServer exception:");
			e.printStackTrace();
		}
	}
}
