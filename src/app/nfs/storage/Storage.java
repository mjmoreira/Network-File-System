package nfs.storage;

import nfs.interfaces.StorageClient;
import nfs.interfaces.MetaServerStorage;
import nfs.filesystem.*;
import nfs.shared.NFSPath;
import nfs.shared.ReturnStatus;
import nfs.shared.StorageInformation;
import static nfs.shared.ReturnStatus.*;

import java.util.Arrays;
import java.util.Random;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
// import java.nio.file.DirectoryStream;

// This class avoids creating more than one registry by JVM, that is, all the
// Storage objects in a JVM use the same registry.
// Each storage object can be associated with a different metadata server.
public class Storage implements StorageClient {
	private static Registry localRegistry = null;
	private static int localRegistryPort = 0;

	public static Storage createStorage(String metaHostName,
	                                    int metaRegistryPort,
	                                    String metaRegistryName,
	                                    String storageMountName) {
		Storage storage = new Storage();
		try {
			/* Create local filesystem tree. */
			storage.nfsMountPath = new String[] {"", storageMountName};
			storage.fs = createFilesystem(storage.nfsMountPath);

			/* Create local RMI registry. */
			createLocalRegistry();

			/* Get metadata server's stub from its RMI registry. */
			storage.metadataServer = (MetaServerStorage) LocateRegistry
			                         .getRegistry(metaHostName, metaRegistryPort)
			                         .lookup(metaRegistryName);

			/* Get unique identifier, to identify this storage server in the
			   filesystem tree. */
			storage.storageId = storage.metadataServer.getNewStorageId();

			/* Register the storage object in the local RMI server. */
			localRegistry.rebind(storage.storageId,
			                     UnicastRemoteObject.exportObject(storage, 0));

			/* Create this storage root directory at the metadata server. */
			StorageInformation sl =
				new StorageInformation(storage.storageId,
				                       InetAddress.getLocalHost().getCanonicalHostName(),
				                       localRegistryPort,
				                       storageMountName);
			if (!storage.metadataServer.addStorageServer(sl).ok) {
				System.err.println("Failed to add storage server: "
				                   + storageMountName);
				return null;
			}

			storage.storagePath =
				Files.createTempDirectory("nfs-storage-" + storageMountName + "-");
			// storage.storagePath.toFile().deleteOnExit();
			// TODO: just deletes the directory if it is empty.
			// Find a way to remove everything from directory before exiting?
			// https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/Runtime.html#addShutdownHook(java.lang.Thread)

		} catch (Exception e) {
			e.printStackTrace();
			storage = null;
		}
		return storage;
	}

	private static void createLocalRegistry() {
		Random r = new Random();

		Registry registry = null;
		int port = 0;

		boolean done = false;
		while (!done) {
			port = 2000 + r.nextInt(0xffff - 2000);
			try {
				registry = LocateRegistry.createRegistry(port);
				done = true;
			} catch (RemoteException e) { ; } // Failed to create registry
		}
		localRegistry = registry;
		localRegistryPort = port;
	}

	/**
	 * 
	 * @param mount name of the directory that is the start of the area of control
	 * of the storage server.
	 * @return
	 */
	private static Filesystem createFilesystem(String[] mountPath) {
		// Can be extended to support mount points not just on the root of the
		// tree.
		Filesystem fs = new Filesystem();
		fs.createStorageDirectory(mountPath, "not_relevant");
		return fs;
	}

	private static void populateFilesystemWithLocalDirectoryContents(
				Path localDirectory,
				Filesystem fs)
	{
		// Tem que contactar o servidor de metadados para criar a entrada.
		// Tem que manter o registo do dir base para poder usar relativize
		
		// try (DirectoryStream<Path> s = Files.newDirectoryStream(localDirectory)) {
		// 	for (Path item: s) {
		// 		System.out.println(item.getFileName());
		// 	}
		// } catch (IOException | DirectoryIteratorException x) {
		// 	// IOException can never be thrown by the iteration.
		// 	// In this snippet, it can only be thrown by newDirectoryStream.
		// 	System.err.println(x);
		// }
	}

	// ----------------------

	private MetaServerStorage metadataServer;
	private Filesystem fs;
	private String storageId;
	private Path storagePath;
	private String[] nfsMountPath;

	// Create a storage instance calling the static method createStorage()
	private Storage() {
		metadataServer = null;
		fs = null;
		storageId = null;
	}

	private boolean underManagement(String[] path) {
		return fs.exists(path) && path.length >= nfsMountPath.length;
	}

	private Path transformToLocalPath(String[] nfsPath) {
		return Paths.get(storagePath.toString(),
		                 Arrays.copyOfRange(nfsPath, nfsMountPath.length,
		                                    nfsPath.length));
	}

	public ReturnStatus createFile(String[] path, byte[] contents) throws RemoteException {
		System.out.println("Client -> Storage:: createFile: "
		                   + NFSPath.convertPath(path) + " " + contents.length);
		ReturnStatus r;
		r = fs.createFile(path, contents.length);
		if (!r.ok) {
			System.out.println(r.message);
			return r;
		}
		Path f = null;
		try {
			f = Files.write(transformToLocalPath(path), contents);
		} catch (Exception e) {
			System.err.println("Failed to create file.");
			e.printStackTrace();
		}
		if (f == null) {
			// TODO: delete entry in filesystem object
			return FAILURE_UNABLE_TO_CREATE_FILE;
		}
		System.out.println("Storage -> Meta:: addFile: "
		                   + NFSPath.convertPath(path));
		r = metadataServer.addFile(path, contents.length);
		if (!r.ok) {
			System.out.println(r.message);
		}
		return r;
	}

	public ReturnStatus createDirectory(String[] path) throws RemoteException {
		System.out.println("Client -> Storage:: createDirectory: "
		                   + NFSPath.convertPath(path));
		ReturnStatus r;
		r = fs.createDirectory(path);
		if (!r.ok) {
			System.out.println(r.message);
			return r;
		}
		Path f = null;
		try {
			f = Files.createDirectory(transformToLocalPath(path));
		} catch (Exception e) {
			System.err.println("Failed to create directory.");
			e.printStackTrace();
		}
		if (f == null) {
			// TODO: delete entry in filesystem object
			return FAILURE_UNABLE_TO_CREATE_DIRECTORY;
		}
		System.out.println("Storage -> Meta:: addDirectory: "
		                   + NFSPath.convertPath(path));
		r = metadataServer.addDirectory(path);
		if (!r.ok) {
			System.out.println(r.message);
		}
		return r;
	}

	public ReturnStatus removeFile(String[] path) throws RemoteException {
		System.out.println("Client -> Storage:: removeFile: " + path);
		System.out.println("Storage -> Meta:: removeFile: "
		                   + metadataServer.removeFile(path));
		return FAILURE_NOT_IMPLEMENTED;
	}

	public ReturnStatus removeDirectory(String[] path) throws RemoteException {
		System.out.println("Client -> Storage:: removeDirectory: " + path);
		System.out.println("Storage -> Meta:: removeDirectory: "
		                   + metadataServer.removeDirectory(path));
		return FAILURE_NOT_IMPLEMENTED;
	}

	public byte[] getFile(String[] path) throws RemoteException {
		if (!underManagement(path)) {
			return null;
		}
		Path p = transformToLocalPath(path);
		byte[] file = null;
		try {
			file = Files.readAllBytes(p);
		} catch (IOException e) {
			System.err.println("Could not read: " + p);
			e.printStackTrace();
		}
		return file;
	}
}
