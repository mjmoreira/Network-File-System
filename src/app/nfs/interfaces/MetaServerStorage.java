/**
 * Methods for interaction with the Meta-data server by the storage server.
 */

package nfs.interfaces;

import nfs.shared.StorageInformation;
import nfs.shared.ReturnStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MetaServerStorage extends Remote {
	ReturnStatus addStorageServer(StorageInformation sl) throws RemoteException;
	ReturnStatus deleteStorageServer(StorageInformation sl) throws RemoteException;
	String getNewStorageId() throws RemoteException;

	ReturnStatus addFile(String[] path, long size) throws RemoteException;
	ReturnStatus addDirectory(String[] path) throws RemoteException;
	ReturnStatus removeFile(String[] path) throws RemoteException;
	ReturnStatus removeDirectory(String[] path) throws RemoteException;
}
