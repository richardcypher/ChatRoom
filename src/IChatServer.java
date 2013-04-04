import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IChatServer extends Remote {
	public void SendMessage(IChatClient client, String message, String des) throws RemoteException;
	public void Remove(IChatClient client) throws RemoteException;
	public void Add(IChatClient client) throws RemoteException;
}
