package nfs.metaserver;

import nfs.interfaces.MetaServerClient;
import nfs.interfaces.MetaServerStorage;
import nfs.shared.Constants;
import nfs.shared.LsInfo;
import nfs.shared.Path;
import nfs.shared.StorageLocation;
import nfs.filesystem.Filesystem;
import nfs.shared.ReturnStatus;
import static nfs.shared.ReturnStatus.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class MetaDataServer implements MetaServerClient, MetaServerStorage {
	private static MetaDataServer server = null;
	
	public static MetaDataServer create() {
		server = new MetaDataServer();
		return server;
	}

	private Filesystem filesystem;
	private Set<String> validStorageIds;

	private MetaDataServer() {
		filesystem = new Filesystem();
		validStorageIds = new HashSet<>();
	}


	public void createTestFilesystemTree() {
		Filesystem f = filesystem;
		f.createStorageDirectory(new String[] {"", "s1"}, "storageId-1");
		f.createDirectory(new String[] {"", "s1", "d1"});
		f.createDirectory(new String[] {"", "s1", "d1", "d2"});
		f.createDirectory(new String[] {"", "s1", "d1", "d2", "d3"});
		f.createFile(new String[] {"", "s1", "f1"}, 11L);
		f.createFile(new String[] {"", "s1", "f2"}, 22L);
		f.createFile(new String[] {"", "s1", "d1", "d2", "d3", "f3"}, 33L);
		f.createFile(new String[] {"", "s1", "d1", "d2", "d3", "f4"}, 44L);
		f.createFile(new String[] {"", "s1", "d1", "f5"}, 55L);
		f.createStorageDirectory(new String[] {"", "s2"}, "storageId-2");
		f.createDirectory(new String[] {"", "s2", "d1"});
	}

	// Client methods

	public LsInfo listDir(String[] path) throws RemoteException {
		System.out.println("Client -> Meta:: listDir: " + Arrays.toString(path));

		return filesystem.listDirectory(path);
	}

	// ---------------------------

	// Storage methods

	// TODO: tenho que impedir a criação de duas storages com o mesmo path
	// ou com o mesmo ID. (O ID não deve ser problema porque é gerado pelo
	// servidor de metadados).
	public ReturnStatus addStorageServer(StorageLocation sl) throws RemoteException {
		System.out.println("Storage -> Meta:: addStorageServer: " + sl.mountName);
		if (!validStorageIds.contains(sl.storageId)) {
			return FAILURE_INVALID_STORAGE_ID;
		}
		return filesystem.createStorageDirectory(new String[] {"", sl.mountName},
		                                         sl.storageId);
	}

	public ReturnStatus deleteStorageServer(StorageLocation sl) throws RemoteException {
		System.out.println("Storage -> Meta:: deleteStorageServer: " + sl.mountName);
		return FAILURE_NOT_IMPLEMENTED;
	}

	// Can return an array of 2 Strings: the storageId and the cookie.
	// Alternatively, the cookie can be returned when the storage is officially
	// created.
	public String getNewStorageId() throws RemoteException {
		String id = "Storage-" + System.nanoTime();
		validStorageIds.add(id);
		return id;
	}

	public ReturnStatus addFile(String[] path, long size) throws RemoteException {
		System.out.println("Storage -> Meta:: addFile: " + Path.convertPath(path));
		return filesystem.createFile(path, size);
	}
	public ReturnStatus addDirectory(String[] path) throws RemoteException {
		System.out.println("Storage -> Meta:: addDirectory: " + Path.convertPath(path));
		return filesystem.createDirectory(path);
	}
	public ReturnStatus removeFile(String[] path) throws RemoteException {
		System.out.println("Storage -> Meta:: removeFile: " + Path.convertPath(path));
		return FAILURE_NOT_IMPLEMENTED;
	}
	public ReturnStatus removeDirectory(String[] path) throws RemoteException {
		System.out.println("Storage -> Meta:: removeDirectory: " + Path.convertPath(path));
		return FAILURE_NOT_IMPLEMENTED;
	}

	// ---------------------------

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			MetaDataServer server = new MetaDataServer();
			server.createTestFilesystemTree();
			Remote stub = UnicastRemoteObject.exportObject((Remote) server, 0);
			Registry registry = LocateRegistry.createRegistry(Constants.REGISTRY_PORT);
			registry.rebind(Constants.METADATA_REGISTRY_ID, stub);
			System.out.println("MetaDataServer created.");
		} catch (Exception e) {
			System.err.println("MetaDataServer exception:");
			e.printStackTrace();
		}
	}

}
