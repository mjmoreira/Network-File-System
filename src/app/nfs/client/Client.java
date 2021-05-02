package nfs.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Scanner;

import nfs.interfaces.MetaServerClient;
import nfs.interfaces.StorageClient;
import nfs.shared.Constants;
import nfs.shared.LsInfo;
import nfs.shared.Path;
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

			Scanner in = new Scanner(System.in);
			System.out.print("Path: ");
			while (in.hasNext()) {
				String s = in.nextLine().trim();
				String[] path = Path.convertPath(s);
				if (path == null) {
					System.out.println("Bad path: " + s);
				}
				else {
					LsInfo info = meta.listDir(path);
					System.out.println(info);
					if (info == null) {
						System.out.println("Did not find " + s + ".");
					}
					else {
						printLsInfo(path, info);
					}
				}
				System.out.print("Path: ");
			}
			in.close();
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

	public static void printLsInfo(String[] path, LsInfo lsInfo) {
		if (path == null || lsInfo == null || path.length == 0) {
			System.out.println("printLsInfo: bad parameter data");
			return;
		}
		
		// file path used in the ls request
		if (path.length > lsInfo.path.length) {
			String fileName = path[path.length - 1];
			for (LsFile f: lsInfo.files) {
				if (f.name.equals(fileName)) {
					System.out.println(Path.joinPath(lsInfo.path, fileName));
					printLsFile(f);
					break;
				}
			}
		}
		else { // Directory
			System.out.println(Path.convertPath(lsInfo.path));
			for (LsDirectory d: lsInfo.directories) {
				printLsDirectory(d);
			}
			for (LsFile f: lsInfo.files) {
				printLsFile(f);
			}
		}
	}

	private static void printLsFile(LsFile file) {
		System.out.println("- " + file.name + " \t" + file.size);
	}

	private static void printLsDirectory(LsDirectory directory) {
		System.out.println("d " + directory.name + " \t@ " + directory.storageId);
	}
}
