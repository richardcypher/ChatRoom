import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IChatClient extends Remote {
	public void PrintMessage(String message, ArrayList<Integer> vector, int index, boolean print) throws RemoteException;
	public void UpdateVector(boolean add, int index) throws RemoteException;
	public ArrayList<Integer> getVector() throws RemoteException;
	public void setVector(int j, int pos) throws RemoteException;
	public String getName() throws RemoteException;
}
