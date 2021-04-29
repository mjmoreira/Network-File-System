package nfs.metaserver;

import nfs.interfaces.MetaServerClient;
import nfs.interfaces.MetaServerStorage;
import nfs.shared.Constants;
import nfs.shared.LsInfo;
import nfs.shared.LsDirectory;
import nfs.shared.LsFile;
import nfs.filesystem.Directory;
import nfs.filesystem.File;

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

	private Directory root;

	private MetaDataServer() {
		root = Directory.createRootDirectory();
	}


	// Client methods

	// Pode receber String[] com o path. Fica mais fácil.
	// Retorna String[] com path + Directory do último nó do path.
	public LsInfo listDir(String[] path) throws RemoteException {
		if (path.length == 0) {
			return null;
		}
		System.out.println("Client -> Meta:: listDir: " + Arrays.toString(path));

				
		LsDirectory[] dirs = new LsDirectory[3];
		dirs[0] = new LsDirectory("dir1", "storage1");
		dirs[1] = new LsDirectory("dir2", "storage1");
		dirs[2] = new LsDirectory("dir3", "storage1");
		
		LsFile[] files = new LsFile[2];
		files[0] = new LsFile("file1", 111L);
		files[1] = new LsFile("file2", 222L);
		
		LsInfo info =
			new LsInfo(path, path[path.length - 1], "storage1", dirs, files);
		return info;
	}

	// ---------------------------

	// Storage methods

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
