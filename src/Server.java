
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private static volatile int registred;
    private static volatile String remoteHostName;
    private static volatile HashMap<Integer, String> hashConnections = new HashMap<>();

    public Server() throws RemoteException {
    }

    public static void main(String[] args) throws RemoteException {
        if (args.length != 2) {
            System.out.println("Usage: java Server <server ip> <number of players>");
            System.exit(1);
        }

        try {
            System.setProperty("java.rmi.server.hostname", args[0]);
            LocateRegistry.createRegistry(52369);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        try {
            String server = "rmi://" + args[0] + ":52369/server";
            Naming.rebind(server, new Server());
            System.out.println("Server is ready.");
        } catch (Exception e) {
            System.out.println("Serverfailed: " + e);
        }

        while (true) {
            if (registred == Integer.parseInt(args[1])) {
                hashConnections.forEach((i, j) -> {
                    try {
                        ClientInterface server = (ClientInterface) Naming.lookup(j);
                        server.inicia(i);
                    } catch (Exception e) {
                        System.out.println("Player inicialization failed");
                        e.printStackTrace();
                    }

                });
            }
            try {
                hashConnections.forEach((i, j) -> {
                    try {
                        ClientInterface server = (ClientInterface) Naming.lookup(j);
                        server.cutuca();
                    } catch (Exception e) {
                        System.out.println("Connection check failed");
                        e.printStackTrace();
                    }

                });

                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int registra(int port) {
        try {
            remoteHostName = getClientHost();
            hashConnections.put(registred, "rmi://" + remoteHostName + ":" + port + "/client");
        } catch (Exception e) {
            System.out.println("Registration failed");
            e.printStackTrace();
        }
        registred++;
        return registred - 1;
    }

    public int joga(int id) {
        try {
            System.out.println("Gamer " + id + " has played");

            var r = Math.random();

            if (r < 0.01) {
                ClientInterface server = (ClientInterface) Naming.lookup(hashConnections.remove(id));
                server.finaliza();
            }
        } catch (Exception e) {
            System.out.println("Play failed");
            e.printStackTrace();
        }

        return 1;
    }

    public int encerra(int id) {
        hashConnections.remove(id);
        System.out.println("Gamer " + id + " removed");

        return 2;
    }

}
