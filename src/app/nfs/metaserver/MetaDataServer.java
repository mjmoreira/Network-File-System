package nfs.metaserver;

import nfs.interfaces.MetaServerClient;
import nfs.interfaces.MetaServerStorage;
import nfs.shared.Constants;
import nfs.shared.LsInfo;
import nfs.shared.NFSPath;
import nfs.shared.StorageInformation;
import nfs.filesystem.Filesystem;
import nfs.shared.ReturnStatus;
import static nfs.shared.ReturnStatus.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class MetaDataServer implements MetaServerClient, MetaServerStorage {
	private static MetaDataServer server = null;
	
	public static MetaDataServer create() {
		server = new MetaDataServer();
		return server;
	}

	private Filesystem filesystem;

	private StorageMapper storageMapper;

	private MetaDataServer() {
		filesystem = new Filesystem();
		storageMapper = new StorageMapper();
	}

	// Client methods

	public LsInfo listDir(String[] path) throws RemoteException {
		System.out.println("Client -> Meta:: listDir: " + Arrays.toString(path));
		return filesystem.listDirectory(path);
	}

	public StorageInformation getStorageInformation(String storageId)
			throws RemoteException {
		System.out.println("Client -> Meta:: getStorageInformation: " + storageId);
		return storageMapper.getStorageServer(storageId);
	}

	// ---------------------------

	// Storage methods

	public ReturnStatus addStorageServer(StorageInformation si)
			throws RemoteException {
		System.out.println("Storage -> Meta:: addStorageServer: " + si.mountName);
		if (!storageMapper.addStorageServer(si)) {
			return FAILURE_INVALID_STORAGE_ID;
		}
		return filesystem.createStorageDirectory(new String[] {"", si.mountName},
		                                         si.storageId);
	}

	public ReturnStatus deleteStorageServer(StorageInformation si)
			throws RemoteException {
		System.out.println("Storage -> Meta:: deleteStorageServer: " + si.mountName);
		return FAILURE_NOT_IMPLEMENTED;
	}

	// Can return an array of 2 Strings: the storageId and the cookie.
	// Alternatively, the cookie can be returned when the storage is officially
	// created.
	public String getNewStorageId() throws RemoteException {
		return storageMapper.getNewStorageId();
	}

	public ReturnStatus addFile(String[] path, long size) throws RemoteException {
		System.out.println("Storage -> Meta:: addFile: " + NFSPath.convertPath(path));
		return filesystem.createFile(path, size);
	}
	public ReturnStatus addDirectory(String[] path) throws RemoteException {
		System.out.println("Storage -> Meta:: addDirectory: " + NFSPath.convertPath(path));
		return filesystem.createDirectory(path);
	}
	public ReturnStatus removeFile(String[] path) throws RemoteException {
		System.out.println("Storage -> Meta:: removeFile: " + NFSPath.convertPath(path));
		return FAILURE_NOT_IMPLEMENTED;
	}
	public ReturnStatus removeDirectory(String[] path) throws RemoteException {
		System.out.println("Storage -> Meta:: removeDirectory: " + NFSPath.convertPath(path));
		return FAILURE_NOT_IMPLEMENTED;
	}

	// ---------------------------

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			MetaDataServer server = new MetaDataServer();
			Remote stub = UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.createRegistry(Constants.REGISTRY_PORT);
			registry.rebind(Constants.METADATA_REGISTRY_ID, stub);
			System.out.println("MetaDataServer created.");
		} catch (Exception e) {
			System.err.println("MetaDataServer exception:");
			e.printStackTrace();
		}
	}

}
