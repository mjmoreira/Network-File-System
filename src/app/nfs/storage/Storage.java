package nfs.storage;

import nfs.interfaces.StorageClient;
import nfs.interfaces.MetaServerStorage;
import nfs.filesystem.*;
import nfs.shared.Path;
import nfs.shared.ReturnStatus;
import nfs.shared.StorageLocation;
import static nfs.shared.ReturnStatus.*;

import java.util.Random;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;

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
			// Create local filesystem tree
			storage.fs = createFilesystem(storageMountName);

			// Create local registry
			createLocalRegistry();

			storage.metadataServer = (MetaServerStorage) LocateRegistry
			                         .getRegistry(metaHostName, metaRegistryPort)
			                         .lookup(metaRegistryName);

			// Get unique identifier from metadata server
			storage.storageId = storage.metadataServer.getNewStorageId();

			// Register the storage object in the local rmi server.
			localRegistry.rebind(storage.storageId,
			                     UnicastRemoteObject.exportObject(storage, 0));

			// Create storage directory in the metadata server
			StorageLocation sl = new StorageLocation(storage.storageId,
			                    InetAddress.getLocalHost().getCanonicalHostName(),
			                    localRegistryPort,
			                    storageMountName);
			if (!storage.metadataServer.addStorageServer(sl).ok) {
				System.err.println("Failed to add storage server: "
				                   + storageMountName);
				return null;
			}

			// Populate storage directory tree in the metadata server
			// - percorrer a árvore do filesystem em preorder: current dir, dirs,
			// files e depois entro nos dir por ordem?
			// - ler o filesystem e posso criar uma queue de diretórios, e depois
			// faço chamadas para criar todos
			// - depois posso criar todos os ficheiros que encontrei ao listar
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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

	private static Filesystem createFilesystem(String mount) {
		// must receive as an argument the local directory where the filesystem
		// will be stored
		// return createTestFilesystem();
		Filesystem fs = new Filesystem();
		fs.createStorageDirectory(new String[] {"", mount}, "not_relevant");
		return fs;
	}

	private static Filesystem createTestFilesystem() {
		Filesystem fs = new Filesystem();
		fs.createDirectory(new String[] {"", "dir1"});
		fs.createDirectory(new String[] {"", "dir2"});
		fs.createDirectory(new String[] {"", "dir3"});
		fs.createDirectory(new String[] {"", "dir3", "dir4"});
		fs.createDirectory(new String[] {"", "dir3", "dir5"});
		fs.createFile(new String[] {"", "f1"}, 11);
		fs.createFile(new String[] {"", "dir1", "f2"}, 22);
		fs.createFile(new String[] {"", "dir1", "f3"}, 33);
		fs.createFile(new String[] {"", "dir3", "dir5", "f4"}, 44);
		fs.createFile(new String[] {"", "dir3", "dir5", "f5"}, 55);
		return fs;
	}

	// ----------------------

	private MetaServerStorage metadataServer;
	private Filesystem fs;
	private String storageId;

	// Create a storage instance calling the static method createStorage()
	private Storage() {
		metadataServer = null;
		fs = null;
		storageId = null;
	}

	public ReturnStatus createFile(String[] path, String contents) throws RemoteException {
		System.out.println("Client -> Storage:: createFile: "
		                   + Path.convertPath(path) + " " + contents.length());
		ReturnStatus r;
		r = fs.createFile(path, contents.length());
		if (!r.ok) {
			System.out.println(r.message);
			return r;
		}
		System.out.println("Storage -> Meta:: addFile: "
		                   + Path.convertPath(path));
		r = metadataServer.addFile(path, contents.length());
		if (!r.ok) {
			System.out.println(r.message);
		}
		return r;
	}

	public ReturnStatus createDirectory(String[] path) throws RemoteException {
		System.out.println("Client -> Storage:: createDirectory: "
		                   + Path.convertPath(path));
		ReturnStatus r;
		r = fs.createDirectory(path);
		if (!r.ok) {
			System.out.println(r.message);
			return r;
		}
		System.out.println("Storage -> Meta:: addDirectory: "
		                   + Path.convertPath(path));
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

	public String getFile(String[] path) throws RemoteException {
		// TODO: Find the proper way to transmit a file.
		// null if the file does not exist.
		// empty if the file is empty.
		return "File contents: " + path;
	}
}
