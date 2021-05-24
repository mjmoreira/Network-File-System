package nfs.client;

import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import nfs.shared.Constants;
import nfs.shared.LsInfo;
import nfs.shared.NFSPath;
import nfs.shared.LsDirectory;
import nfs.shared.LsFile;

class CommandLine {
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

		Client client = new Client(args[0], Constants.REGISTRY_PORT,
		                           Constants.METADATA_REGISTRY_ID);

		Scanner in = new Scanner(System.in);
		System.out.print("cmd$: ");
		while (in.hasNext()) {
			Scanner line = new Scanner(in.nextLine().trim());
			String command = line.next();
			String pathString = line.next();

			String[] path = NFSPath.validateAndConvertPath(pathString);
			if (path == null) {
				System.out.println("Bad path: " + pathString);
			}
			else {
				if (command.equals("ls")) {
					LsInfo info = client.list(path);
					System.out.println(info);
					if (info == null) {
						System.out.println("Did not find " + pathString + ".");
					}
					else {
						printLsInfo(path, info);
					}
				} else if (command.equals("create")) {
					String contents = line.next();
					System.out.println(client.createFile(path, contents.getBytes()));
				} else if (command.equals("getFile")) {
					System.out.println(new String(client.getFile(path)));
				} else {
					System.out.println("unknown command: " + command);
				}
			}
			System.out.print("cmd$: ");
		}
		in.close();
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
					System.out.println(NFSPath.joinPath(lsInfo.path, fileName));
					printLsFile(f);
					break;
				}
			}
		}
		else { // Directory
			System.out.println(NFSPath.convertPath(lsInfo.path));
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
