
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class Client extends UnicastRemoteObject implements ClientInterface {
    public static boolean inicia = false;
    public static boolean finalizouClient = false;

    public Client() throws RemoteException {
    }

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Usage: java Client <server ip> <client ip> <client port>");
            System.exit(1);
        }

        try {
            System.setProperty("java.rmi.server.hostname", args[0]);
            LocateRegistry.createRegistry(Integer.parseInt(args[2]));
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        int id = -1;

        try {
            String client = "rmi://" + args[1] + ":" + args[2] + "/client";
            Naming.rebind(client, new Client());
            System.out.println("Addition Server is ready.");
        } catch (Exception e) {
            System.out.println("Addition Serverfailed: " + e);
        }

        String remoteHostName = args[0];
        String connectLocation = "rmi://" + remoteHostName + ":52369/server";

        ServerInterface server = null;
        try {
            System.out.println("Connecting to server at : " + connectLocation);
            server = (ServerInterface) Naming.lookup(connectLocation);
        } catch (Exception e) {
            System.out.println("Client failed: ");
            e.printStackTrace();
        }

        while (true) {
            try {
                int i = 0;
                if (id == -1) {
                    id = server.registra(Integer.parseInt(args[2]));

                }
                if (inicia) {
                    while (i < 20) {
                        if (!finalizouClient) {
                            server.joga(id);
                            Thread.sleep(randomTime());
                            i++;
                        }
                    }
                    server.encerra(id);
                    System.out.println("Game Over");
                    return;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }

    }

    public void inicia(int id) {
        System.out.println("Gamer " + id + " inicialized");
        inicia = true;
    }

    public void finaliza() {
        System.out.println("Server stopped (1% chance)");
        finalizouClient = true;
    }

    public void cutuca() {
        System.out.println("Connection check");
    }

    public static int randomTime() {
        Random r = new Random();
        int low = 500;
        int high = 1500;
        return r.nextInt(high - low) + low;
    }
}
