
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class Server extends UnicastRemoteObject implements ServerInterface {
    private static volatile int registrou;
    private static volatile boolean changed;
    private static volatile String remoteHostName;
    private static Random random = new Random();
    private static volatile HashMap<Integer, String> hashConnections = new HashMap<>();


    public Server() throws RemoteException {
    }

    public static void main(String[] args) throws RemoteException {
        if (args.length != 2) {
            System.out.println("Usage: java Server <server ip> <N>");
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
            if (changed) {

                int jogadores = Integer.parseInt(args[1]);
                if (registrou == jogadores && changed) {
                        hashConnections.forEach((i,j) -> {
                            ClientInterface server = null;
                            try {
                                server = (ClientInterface) Naming.lookup(j);
                                server.inicia(i);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                }

                changed = false;
            }
            try {
                hashConnections.forEach((i,j) -> {
                    ClientInterface server = null;
                    try {
                        server = (ClientInterface) Naming.lookup(j);
                        server.cutuca();
                    } catch (Exception e) {
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
        int numero = random.nextInt();
        registrou++;
        try {
            remoteHostName = getClientHost();
            hashConnections.put(numero, "rmi://" + remoteHostName + ":" + port + "/server2");
        } catch (Exception e) {
            System.out.println("Failed to get client IP");
            e.printStackTrace();
        }
        changed = true;
        return numero;
    }

    public int joga(int id) {
        try{
            System.out.println("Jogador: " + id + " acabou de realizar uma jogada");

            var r = Math.random();

            if (r < 0.01) {
                ClientInterface server = (ClientInterface) Naming.lookup(hashConnections.remove(id));
                server.finaliza();
            }
        }catch (Exception e){
            System.out.println(e);
        }

        return 1;
        }

        public int encerra ( int id){
            hashConnections.remove(id);
            System.out.println("Jogador: " + id + " removido");

            return 2;
        }

    }
