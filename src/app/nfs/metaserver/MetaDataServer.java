package nfs.metaserver;

import nfs.interfaces.MetaServerClient;
import nfs.interfaces.MetaServerStorage;
import nfs.shared.Constants;
import nfs.shared.LsInfo;
import nfs.shared.LsDirectory;
import nfs.shared.LsFile;
import nfs.filesystem.Filesystem;

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

	private MetaDataServer() {
		filesystem = new Filesystem();
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
	public boolean addStorageServer(String path) throws RemoteException {
		System.out.println("Storage -> Meta:: addStorageServer: " + path);
		return false;
	}
	public boolean deleteStorageServer(String path) throws RemoteException {
		System.out.println("Storage -> Meta:: deleteStorageServer: " + path);
		return false;
	}

	public boolean addFile(String path) throws RemoteException {
		System.out.println("Storage -> Meta:: addFile: " + path);
		return false;
	}
	public boolean addDirectory(String path) throws RemoteException {
		System.out.println("Storage -> Meta:: addDirectory: " + path);
		return false;
	}
	public boolean removeFile(String path) throws RemoteException {
		System.out.println("Storage -> Meta:: removeFile: " + path);
		return false;
	}
	public boolean removeDirectory(String path) throws RemoteException {
		System.out.println("Storage -> Meta:: removeDirectory: " + path);
		return false;
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
			registry.rebind(Constants.REGISTRY_ID_METADATA, stub);
			System.out.println("MetaDataServer created.");
		} catch (Exception e) {
			System.err.println("MetaDataServer exception:");
			e.printStackTrace();
		}
	}

}
