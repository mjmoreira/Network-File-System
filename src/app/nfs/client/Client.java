package nfs.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashMap;

import nfs.interfaces.MetaServerClient;
import nfs.interfaces.StorageClient;
import nfs.shared.LsFile;
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
			throw new NullPointerException("metadata is null.");
		}
		storages = new HashMap<>();
	}

	LsInfo list(String[] path) {
		LsInfo info = null;
		try {
			info = meta.listDir(path);
		} catch (RemoteException e) {}
		return info;
	}

	boolean createFile(String[] path, byte[] contents) {
		String fileName = path[path.length - 1];
		LsInfo info = list(Arrays.copyOf(path, path.length - 1));
		if (info == null) {
			System.err.println("path does not exist return code");
			return false;
		}
		if (info.hasDirectory(fileName) || info.hasFile(fileName)) {
			System.err.println("file or directory with same name already exists return code");
			return false;
		}

		StorageClient storage = getStorageStub(info.storageId);
		if (storage == null) {
			System.err.println("Client.createFile: Couldn't get stub for storage: "
			                   + info.storageId);
			return false;
		}
		ReturnStatus status = FAILURE_NOT_IMPLEMENTED;
		try {
			status = storage.createFile(path, contents);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return status == SUCCESS;
	}

	byte[] getFile(String[] path) {
		LsInfo info = list(path);
		if (!fileExists(path[path.length - 1], info.files)) {
			return null;
		}
		StorageClient storage = getStorageStub(info.storageId);
		if (storage == null) {
			System.err.println("Client.getFile: Couldn't get stub for storage: "
			                   + info.storageId);
			return null;
		}
		byte[] contents = null;
		try {
			contents = storage.getFile(path);
		} catch (RemoteException e) {
			System.err.println("Client.getFile: Couldn't get file from storage: "
			                   + NFSPath.convertPath(path));
			e.printStackTrace();
		}
		return contents;
	}

	private boolean fileExists(String name, LsFile[] files) {
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].name.equals(name)) {
					return true;
				}
			}
		}
		return false;
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
