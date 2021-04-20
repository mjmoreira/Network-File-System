/**
 * Methods for interaction with the Meta-data server by the client.
 */

package nfs.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MetaServerClient extends Remote {
	// list dir
	String listDir(String path) throws RemoteException;
}
