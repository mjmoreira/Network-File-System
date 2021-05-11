/**
 * Methods for interaction with the Storage server by the client.
 */

package nfs.interfaces;

import nfs.shared.ReturnStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StorageClient extends Remote {
	ReturnStatus createFile(String[] path, String contents) throws RemoteException;
	ReturnStatus createDirectory(String[] path) throws RemoteException;
	ReturnStatus removeFile(String[] path) throws RemoteException;
	ReturnStatus removeDirectory(String[] path) throws RemoteException;
	String getFile(String[] path) throws RemoteException;
}
