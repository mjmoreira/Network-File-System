package nfs.client;

import java.util.Scanner;
import java.io.PrintStream;
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

		Client client = null;
		try {
			client = new Client(args[0], Constants.REGISTRY_PORT,
		                        Constants.METADATA_REGISTRY_ID);
		} catch (Exception e) {}
		if (client == null) {
			System.out.println("Unable to establish connection with metadata server.");
			return;
		}

		printCommands();
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
					// ls /absolute/nfs/path
					LsInfo info = client.list(path);
					System.out.println(info);
					if (info == null) {
						System.out.println("Did not find " + pathString + ".");
					}
					else {
						printLsInfo(path, info);
					}
				} else if (command.equals("getFile")) {
					// retrieves file and prints as string in stdout.
					// getFile /absolute/nfs/path/to/file
					byte[] file = client.getFile(path);
					if (file != null) {
						System.out.println(new String(file));
					} else {
						System.out.println("null");
					}
				} else if (command.equals("mkdir")) {
					// mkdir /absolute/nfs/path/to/new_dir
					System.out.println(client.createDirectory(path));
				} else if (command.equals("create")) {
					// create file
					// create /absolute/nfs/path/to/new_file /absolute/path/to/local/fs/file
					String pathSource = line.next();
					create(client, path, pathSource);
				} else {
					System.out.println("CLI: Unknown command: " + command);
				}
			}
			System.out.print("cmd$: ");
		}
		in.close();
	}

	private static void printCommands() {
		PrintStream o = System.out;
		o.println("Commands available:");
		o.println("  List:");
		o.println("    ls /absolute/nfs/path");
		o.println("  Create directory:");
		o.println("    mkdir /absolute/nfs/path/to/new_dir");
		o.println("  Copy file from the local filesystem to the NFS:");
		o.println("    create /absolute/nfs/path/to/new_file /absolute/path/to/local/fs/file");
		o.println("  Retrieve a file from the NFS and print it in the console:");
		o.println("    getFile /absolute/nfs/path/to/file");
	}

	private static void create(Client client, String[] path, String pathSource) {
		Path source = null;
		try {
			source = Paths.get(pathSource).normalize().toAbsolutePath();
		} catch (Exception e) {
			System.out.println("CLI: Invalid path: " + pathSource);
			e.printStackTrace();
		}
		if (source == null) {
			return;
		}
		byte[] contents = null;
		try {
			contents = Files.readAllBytes(source);
		} catch (Exception e) {
			System.out.println("CLI: Unable to read source file.");
			e.printStackTrace();
		}
		if (contents != null) {
			System.out.println(client.createFile(path, contents));
		} else {
			System.out.println("CLI: File contents is null.");
		}
	}

	private static void printLsInfo(String[] path, LsInfo lsInfo) {
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
