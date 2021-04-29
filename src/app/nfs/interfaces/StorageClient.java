/**
 * Methods for interaction with the Storage server by the client.
 */

package nfs.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StorageClient extends Remote {
	boolean createFile(String path, String contents) throws RemoteException;
	boolean createDirectory(String path) throws RemoteException;
	boolean removeFile(String path) throws RemoteException;
	boolean removeDirectory(String path) throws RemoteException;
}
