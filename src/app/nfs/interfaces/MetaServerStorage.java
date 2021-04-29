/**
 * Methods for interaction with the Meta-data server by the storage server.
 */

package nfs.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MetaServerStorage extends Remote {
	boolean addStorageServer(String path) throws RemoteException;
	boolean deleteStorageServer(String path) throws RemoteException;

	boolean addFile(String path) throws RemoteException;
	boolean addDirectory(String path) throws RemoteException;
	boolean removeFile(String path) throws RemoteException;
	boolean removeDirectory(String path) throws RemoteException;
}
