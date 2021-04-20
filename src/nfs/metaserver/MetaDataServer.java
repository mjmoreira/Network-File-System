package nfs.metaserver;

import nfs.shared.MetaServerClient;
import nfs.shared.MetaServerStorage;
import nfs.shared.Constants;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class MetaDataServer implements MetaServerClient, MetaServerStorage {
	public MetaDataServer() {
		super();
	}

	// Client methods
	public String listDir(String path) throws RemoteException {
		System.out.println("Client -> Meta:: listDir: " + path);
		return "Meta -> Client: listDir";
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
