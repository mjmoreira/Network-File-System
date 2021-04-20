package nfs.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import nfs.shared.MetaServerClient;
import nfs.shared.StorageClient;
import nfs.shared.Constants;

public class Client {
	public static void main(String args[]) {
		/* requires argument with hostname of the rmiregistry */
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			Registry registry = LocateRegistry.getRegistry(args[0]);
			MetaServerClient meta = (MetaServerClient)
				registry.lookup(Constants.REGISTRY_ID_METADATA);
			System.out.println(meta.listDir("/asdf"));
			StorageClient storage = (StorageClient)
				registry.lookup(Constants.REGISTRY_ID_STORAGE);
			System.out.println(storage.createFile("/a", "1234"));
			System.out.println(storage.createDirectory("/b"));
			System.out.println(storage.removeFile("/c"));
			System.out.println(storage.removeDirectory("/d"));
		} catch (Exception e) {
			System.err.println("Client exception:");
			e.printStackTrace();
		}
	}
}
