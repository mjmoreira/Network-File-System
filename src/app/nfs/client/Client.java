package nfs.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashMap;

import nfs.interfaces.MetaServerClient;
import nfs.interfaces.StorageClient;
import nfs.shared.LsInfo;
import nfs.shared.NFSPath;
import nfs.shared.StorageInformation;
import nfs.shared.ReturnStatus;
import static nfs.shared.ReturnStatus.*;

class Client {
	/**
	 * 
	 * @param address
	 * @param port
	 * @param id
	 * @return null if it was not possible to obtain the stub.
	 */
	private static Remote getRemoteStub(String address, int port, String id) {
		Remote r = null;
		try {
			Registry registry = LocateRegistry.getRegistry(address, port);
			r = registry.lookup(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	private MetaServerClient meta;
	// Assumes storages can't change address without changing the id.
	private HashMap<String, StorageClient> storages;

	Client(String address, int registryPort, String registryId)
			throws NullPointerException {
		meta = (MetaServerClient) getRemoteStub(address, registryPort, registryId);
		if (meta == null) {
			throw new NullPointerException("Unable to connect with metadata server.");
		}
		storages = new HashMap<>();
	}

	LsInfo list(String[] path) {
		LsInfo info = null;
		try {
			info = meta.listDir(path);
		} catch (RemoteException e) {
			System.out.println("Client.list: Could not get listing from metadata.");
			e.printStackTrace();
		}
		return info;
	}

	boolean createFile(String[] path, byte[] contents) {
		String fileName = path[path.length - 1];
		LsInfo info = list(Arrays.copyOf(path, path.length - 1));
		if (info == null) {
			System.err.println("Client.createFile: Path does not exist.");
			return false;
		}
		if (info.hasDirectory(fileName) || info.hasFile(fileName)) {
			System.out.println("Client.createFile: File or directory with same "
			                   + "name already exists.");
			return false;
		}
		if (info.storageId == null) {
			System.out.println("Client.createFile: Parent directory is not "
			                   + "owned by a storage server.");
			return false;
		}

		StorageClient storage = getStorageStub(info.storageId);
		if (storage == null) {
			System.out.println("Client.createFile: Could not get stub for storage: "
			                   + info.storageId);
			return false;
		}

		ReturnStatus status = FAILURE_NOT_IMPLEMENTED;
		try {
			status = storage.createFile(path, contents);
		} catch (RemoteException e) {
			System.out.println("Client.createFile: Cound not create file at "
			                   + "storage server.");
			e.printStackTrace();
		}

		if (!status.ok && status != FAILURE_NOT_IMPLEMENTED) {
			System.out.println("Client.createFile: " + status.message);
		}
		return status == SUCCESS;
	}

	byte[] getFile(String[] path) {
		LsInfo info = list(Arrays.copyOf(path, path.length - 1));
		if (info == null || !info.hasFile(path[path.length - 1])) {
			return null;
		}
		StorageClient storage = getStorageStub(info.storageId);
		if (storage == null) {
			System.out.println("Client.getFile: Couldn't get stub for storage: "
			                   + info.storageId);
			return null;
		}

		byte[] contents = null;
		try {
			contents = storage.getFile(path);
		} catch (RemoteException e) {
			System.out.println("Client.getFile: Couldn't get file from storage: "
			                   + NFSPath.convertPath(path));
			e.printStackTrace();
		}
		return contents;
	}

	boolean createDirectory(String[] path) {
		String dirName = path[path.length - 1];
		LsInfo info = list(Arrays.copyOf(path, path.length - 1));
		if (info == null) {
			System.out.println("Client.createDirectory: parent directory does "
			                   + "not exist.");
			return false;
		}
		if (info.hasDirectory(dirName) || info.hasFile(dirName)) {
			System.out.println("Client.createDirectory: "
			                   + path[path.length - 1] + " already exists.");
			return false;
		}
		if (info.storageId == null) {
			System.out.println("Client.createDirectory: Parent directory is "
			                   + "not owned by a storage server.");
			return false;
		}

		StorageClient storage = getStorageStub(info.storageId);
		if (storage == null) {
			System.out.println("Client.createDirectory: Couldn't get stub for "
			                   + "storage: " + info.storageId);
			return false;
		}
		
		ReturnStatus status = FAILURE_NOT_IMPLEMENTED;
		try {
			status = storage.createDirectory(path);
		} catch (RemoteException e) {
			System.out.println("Client.createDirectory: Unable to connect with "
			                   + "storage to create directory."); // ?
			e.printStackTrace();
		}

		if (!status.ok && status != FAILURE_NOT_IMPLEMENTED) {
			System.out.println("Client.createDirectory: " + status.message);
		}
		return status == SUCCESS; // alternatively: return status.ok;
	}

	/**
	 * 
	 * @param storageId
	 * @return null if it was not possible to obtain the stub.
	 */
	private StorageClient getStorageStub(String storageId) {
		StorageClient s = storages.get(storageId);
		if (s != null) {
			return s;
		}
		try {
			StorageInformation info = meta.getStorageInformation(storageId);
			s = (StorageClient)
				getRemoteStub(info.address, info.registryPort, info.storageId);
			if (s != null) {
				storages.put(storageId, s);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return s;
	}

}
