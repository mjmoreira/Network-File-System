package nfs.storage;

import nfs.interfaces.StorageClient;
import nfs.interfaces.MetaServerStorage;
import nfs.shared.Constants;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class StorageServer implements StorageClient {
	private Registry registry;
	private MetaServerStorage metadataServer;
	private String storageName;

	public StorageServer(String name, String host, int port)
			throws RemoteException, NotBoundException {
		storageName = Constants.REGISTRY_ID_STORAGE + "-" + name;

		registry = LocateRegistry.getRegistry(host, port);
		metadataServer =
			(MetaServerStorage) registry.lookup(Constants.REGISTRY_ID_METADATA);
		registry.rebind(storageName,
		                UnicastRemoteObject.exportObject((Remote) this, 0));
	}


	public boolean createFile(String path, String contents) throws RemoteException {
		System.out.println("Client -> Storage:: createFile: " + path + " "
		                   + contents.length());
		System.out.println("Storage -> Meta:: addFile: "
		                   + metadataServer.addFile(path));
		return false;
	}

	public boolean createDirectory(String path) throws RemoteException {
		System.out.println("Client -> Storage:: createDirectory: " + path);
		System.out.println("Storage -> Meta:: addDirectory: "
		                   + metadataServer.addDirectory(path));
		return false;
	}

	public boolean removeFile(String path) throws RemoteException {
		System.out.println("Client -> Storage:: removeFile: " + path);
		System.out.println("Storage -> Meta:: removeFile: "
		                   + metadataServer.removeFile(path));
		return false;
	}

	public boolean removeDirectory(String path) throws RemoteException {
		System.out.println("Client -> Storage:: removeDirectory: " + path);
		System.out.println("Storage -> Meta:: removeDirectory: "
		                   + metadataServer.removeDirectory(path));
		return false;
	}


	public static void main(String[] args) {
		/* requires argument with hostname of the rmiregistry */
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			StorageServer storage = new StorageServer(args[0], args[1], Constants.REGISTRY_PORT);

			System.out.println("StorageServer created.");
		} catch (Exception e) {
			System.err.println("StorageServer exception:");
			e.printStackTrace();
		}
	}
}
