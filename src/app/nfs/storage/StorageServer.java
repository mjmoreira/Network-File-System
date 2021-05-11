package nfs.storage;

import nfs.interfaces.StorageClient;
import nfs.interfaces.MetaServerStorage;
import nfs.shared.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



public class StorageServer {

	// Deve receber como argumento o nome de um ficheiro com o hostname, e o
	// endereço IP do servidor de metadados. Determina o seu hostname e o
	// endereço IP pelas funções de InetAddress
	// InetAddress.getLocalHost().getHostName()
	// Tem problemas, ver: https://stackoverflow.com/questions/7348711/recommended-way-to-get-hostname-in-java
	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			Storage storage =
				Storage.createStorage("localhost",
				                      Constants.REGISTRY_PORT,
				                      Constants.METADATA_REGISTRY_ID,
				                      "storage1");
			
			storage.createDirectory(new String[] {"", "storage1", "dir1"});
			storage.createDirectory(new String[] {"", "storage1", "dir2"});
			storage.createFile(new String[] {"", "storage1", "f1"}, "contents1");
			storage.createFile(new String[] {"", "storage1", "f2"}, "cont2");
			storage.createFile(new String[] {"", "storage1", "dir1", "f3"}, "");
			
			System.out.println("StorageServer created.");
		} catch (Exception e) {
			System.err.println("StorageServer exception:");
			e.printStackTrace();
		}
	}
}
