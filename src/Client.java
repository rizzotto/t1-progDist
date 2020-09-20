
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements JogadorInterface {
    public static ClientThread thread = null;
    public static boolean inicia = false;
    public Client() throws RemoteException {
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Usage: java Client <server ip> <client ip>");
            System.exit(1);
        }

        try {
            System.setProperty("java.rmi.server.hostname", args[0]);
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        thread = new ClientThread(args);
        thread.start();
//        new ClientThread(args).start();

    }

    public void inicia(int id) {
        System.out.println("Jogador: " + id + " iniciado");
        thread.updateInicia(true);
    }
    public void finaliza() {
        System.out.println("Servidor encerrou 1% de chance");
        thread.updateFinalizou(true);

    }
    public void cutuca() {
        System.out.println("Jogador cutucado");
    }
}
