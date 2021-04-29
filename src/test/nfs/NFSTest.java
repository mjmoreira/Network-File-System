package nfs;

import nfs.shared.MetaServerStorage;
import nfs.shared.MetaServerClient;
import nfs.shared.Constants;
import nfs.metaserver.MetaDataServer;
import nfs.client.Client;
import nfs.storage.StorageServer;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.Remote;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;

public class NFSTest {
	private static MetaDataServer metaserver;
	private static Remote metaserverStub;
	private static Registry registry;
	
	@BeforeAll
	static void initialize() throws RemoteException {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		registry = LocateRegistry.createRegistry(Constants.REGISTRY_PORT);
		//Registry registry = LocateRegistry.getRegistry();
		metaserver = new MetaDataServer();
		metaserverStub = UnicastRemoteObject.exportObject(metaserver, 0);
		registry.rebind(Constants.REGISTRY_ID_METADATA, metaserverStub);
		
		//assertNotNull(server);
	// 	try {
	// 		MetaServerClient server = new MetaDataServer();
	// 		MetaServerClient clientStub =
	// 			(MetaServerClient) UnicastRemoteObject.exportObject(server, 0);
	// 		// MetaServerStorage storageStub =
	// 		// 	(MetaServerStorage) UnicastRemoteObject.exportObject(server, 0);
	// 		Registry registry = LocateRegistry.getRegistry();
	// 		registry.rebind(RegistryNames.META_CLIENT_NAME, clientStub);
	// 		//registry.rebind(RegistryNames.META_STORAGE_NAME, storageStub);
	// 		System.out.println("MetaDataServer created.");
	// 	} catch (Exception e) {
	// 		System.err.println("MetaDataServer exception:");
	// 		e.printStackTrace();
	// 	}
	}

	// @Test
	// void createMetaDataSever() throws RemoteException, NotBoundException {
	// 	Registry registry = LocateRegistry.getRegistry();
	// 	MetaDataServer server = new MetaDataServer();
	// 	assertNotNull(server);
	// }


	@Test
	void createStorage() throws RemoteException, NotBoundException {
		StorageServer server = new StorageServer("1", "localhost", Constants.REGISTRY_PORT);
		StorageServer server2 = new StorageServer("2", "localhost", Constants.REGISTRY_PORT);
		assertNotNull(server);
	}

	// @Test
	// void createClient() throws RemoteException {
	// 	// no such thing at the moment
	// 	Client client = new Client(
	// 		(MetaServerClient) registry.lookup(Constants.REGISTRY_ID_METADATA));
	// 	assertNotNull(client);
	// }
}