import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatServer extends Remote {
	public boolean clientExist(String name) throws RemoteException;
	public void SendMessage(IChatClient client, String message, String des) throws RemoteException;
	public void Remove(IChatClient client) throws RemoteException;
	public void Add(IChatClient client) throws RemoteException;	
	public void ShowUser(IChatClient client) throws RemoteException;
}
