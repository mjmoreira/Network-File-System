package nfs.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import nfs.interfaces.MetaServerClient;
import nfs.interfaces.StorageClient;
import nfs.shared.Constants;
import nfs.shared.LsInfo;
import nfs.shared.LsDirectory;
import nfs.shared.LsFile;

public class Client {

	/**
	 * Run as:
	 *  nfs.client.Client (address of metadata) (file format config) \
	 *    [local temporary storage dir]
	 * @param args
	 */
	public static void main(String args[]) {
		// if (System.getSecurityManager() == null) {
		// 	System.setSecurityManager(new SecurityManager());
		// }
		try {
			Registry registry = LocateRegistry.getRegistry(args[0]);
			MetaServerClient meta = (MetaServerClient)
				registry.lookup(Constants.REGISTRY_ID_METADATA);

			String[] path = {"", "asdf"};
			LsInfo info = meta.listDir(path);
			
			System.out.println(info);
			// StorageClient storage = (StorageClient)
			// registry.lookup(Constants.REGISTRY_ID_STORAGE + "-" + args[1]);
			// System.out.println(storage.createFile("/a", "1234"));
			// System.out.println(storage.createDirectory("/b"));
			// System.out.println(storage.removeFile("/c"));
			// System.out.println(storage.removeDirectory("/d"));
		} catch (Exception e) {
			System.err.println("Client exception:");
			e.printStackTrace();
		}
	}
}
