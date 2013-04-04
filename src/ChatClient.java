import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;


public class ChatClient extends UnicastRemoteObject implements IChatClient {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;//client name
	private IChatServer server;//remote server
	private ArrayList<node> queue;//buffer the message
	private ArrayList<Integer> vector;//the message
	
	protected ChatClient(String[] args) throws RemoteException {
		super();
		name = args[0];
		queue = new ArrayList<node>();
		vector = new ArrayList<Integer>();
		
		System.setSecurityManager(new RMISecurityManager());
		if (args.length != 2){
			System.out.println("the number of arguments is not correct!");
			System.exit(0);
		}
		
		String url = "//" + args[1] + "/CHAT-ROOM";
		try {
			server = (IChatServer) Naming.lookup(url);
			server.Add(this);
			
			String text;
			Scanner scan = new Scanner(System.in);
			do {
				text = scan.nextLine();
				final String[] split = text.split(" ");
				if (split.length == 1 && !text.equalsIgnoreCase("exit")) {
					System.out.println("The name of the buddy or the message is not specfied");
					continue;
				}
				else if (split[0].equals(name)) {
					System.out.println("you cannot send message to youself");
					continue;
				}
				int index = text.indexOf(" ");
				final String msg = text.substring(index + 1);
				if (!text.equalsIgnoreCase("exit")) {
					new Thread(new Runnable(){
						public void run() {
							try {
								server.SendMessage(ChatClient.this, msg, split[0]);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}while (!text.equalsIgnoreCase("exit"));
			server.Remove(this);
			System.exit(0);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	public boolean showable(ArrayList<Integer> vector, int index){
		boolean print = true;
		if (this.vector.get(index) != vector.get(index) - 1)
			print = false;
		for (int  i = 0; i < vector.size(); i++)
			if ( i != index && vector.get(i) > this.vector.get(i))
				print = false;
		return print;
	}
	public void PrintMessage(String message, ArrayList<Integer> vector, int index, boolean print) throws RemoteException {
		//System.out.println("the message is | " + message + " |print " + print + " from : " + index);
		//if(this.vector != null) System.out.println("self: " + this.vector.toString());
		//if(vector != null) System.out.println("from: " + vector.toString());
		if (vector == null)//the message from the server
			System.out.println(message);
		else {
			if (showable(vector, index)) {//able to print
				if (print)
					System.out.println(message);
				this.vector.set(index, vector.get(index));
				int i = 0;
				while (i < queue.size() && !queue.isEmpty()) {
					if (showable(queue.get(i).vector, queue.get(i).source)) {
						if (queue.get(i).print)
							System.out.println(queue.get(i).message);
						this.vector.set(queue.get(i).source, queue.get(i).vector.get(queue.get(i).source));
						queue.remove(i);
						i = 0;
						continue;
					}
					i++;
				}
			}
			else {//unable to print put into the buffer queue
				node nd = new node();
				nd.message = message;
				nd.vector = vector;
				nd.source = index;
				nd.print = print;
				queue.add(nd);
			}
		}
	}
	public void UpdateVector(boolean add, int index) throws RemoteException {
		if (add) 
			vector.add(0);
		else
			vector.remove(index);
	}
	
	public String getName() throws RemoteException {
		return name;
	}
	public static void main(String[] args) {
		try {
			new ChatClient(args);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<Integer> getVector() throws RemoteException {
		return vector;
	}
	public void setVector(int j, int pos) throws RemoteException {
		if (pos == -1)
			vector.add(j);
		else
			vector.set(pos, j);
	}
}
class node {
	public String message;//the message
	public ArrayList<Integer> vector;//the vector
	public int source;//the source of the message
	public boolean print;//whether to print the message
}