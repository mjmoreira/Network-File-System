package nfs.storage;

import nfs.shared.Constants;

public class StorageServer {

	// Should receive as arguments the name of the file with the address of the
	// metadata server.
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		Storage storage =
			Storage.createStorage("localhost",
			                      Constants.REGISTRY_PORT,
			                      Constants.METADATA_REGISTRY_ID,
			                      "storage1");
		
		if (storage == null) {
			System.out.println("Failed to create StorageServer.");
			System.exit(1);
		}
		System.out.println("StorageServer created.");
	}
}
