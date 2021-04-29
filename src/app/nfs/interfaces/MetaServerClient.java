/**
 * Methods for interaction with the Meta-data server by the client.
 */

package nfs.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import nfs.shared.LsInfo;

public interface MetaServerClient extends Remote {
	LsInfo listDir(String[] path) throws RemoteException;
}
