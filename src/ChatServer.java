import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;


public class ChatServer extends UnicastRemoteObject implements IChatServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<IChatClient> clients;
	
	protected ChatServer() throws RemoteException {
		super();
		clients = new ArrayList<IChatClient>();
		
		try {
			Naming.rebind("CHAT-ROOM", this);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	public void Remove(IChatClient client) throws RemoteException {
		int index = clients.indexOf(client);
		clients.remove(client);
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).UpdateVector(false, index);
			clients.get(i).PrintMessage(client.getName() + " leave the room", null, i, true);
		}
	}
	public void Add(IChatClient client) throws RemoteException {
		for (int i = 0; i < clients.size(); i++)
			client.setVector(clients.get(i).getVector().get(i), -1);
		clients.add(client);
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).UpdateVector(true, 0);
			clients.get(i).PrintMessage(client.getName() + " enter the room", null, i, true);
		}
	}
	public static void main(String[] args) {
		try {
			new ChatServer();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public void SendMessage(IChatClient client, String message, String des) throws RemoteException {		
		int source = clients.indexOf(client);
		int temp = client.getVector().get(source);
		client.setVector(temp + 1, source);
		ArrayList<Integer> v = client.getVector();
		
		for (int i = 0; i< clients.size(); i++) {
			if (clients.get(i).getName().equals(des)){
				Random ran = new Random();
				try {
					Thread.sleep((Math.abs(ran.nextInt())%8 + 3) * 1000);
					clients.get(i).PrintMessage(client.getName() + ":" + message, v, source, true);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if (!clients.get(i).getName().equals(client.getName()))
				clients.get(i).PrintMessage(client.getName() + ":" + message, v, source, false);
		}
	}
	@Override
	public boolean clientExist(String name) throws RemoteException {
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).getName().equals(name))
				return true;
		return false;
	}
}
