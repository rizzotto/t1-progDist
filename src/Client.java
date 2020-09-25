
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInterface {
    public static ClientThread thread = null;
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

//        thread = new ClientThread(args);
//        thread.start();
//        new ClientThread(args).start();

        int id = -1;

        try {
            String client = "rmi://" + args[1] + ":" + args[2] + "/server2";
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
            System.out.println ("Client failed: ");
            e.printStackTrace();
        }

        while (true) {

            try {
                int i = 0;
                if(id == -1){
                    id = server.registra(Integer.parseInt(args[2]));

                }
                if(inicia){
                    while ( i < 20){
                        if(!finalizouClient){
                            server.joga(id);
                            Thread.sleep(500);
                            i++;
                        }
                    }
                    server.encerra(id);
                    return;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
        }

    }

    public void inicia(int id) {
        System.out.println("Jogador: " + id + " iniciado");
//        thread.updateInicia(true);
        inicia = true;
    }
    public void finaliza() {
        System.out.println("Servidor encerrou 1% de chance");
//        thread.updateFinalizou(true);
        finalizouClient = true;
    }
    public void cutuca() {
        System.out.println("Jogador cutucado");
    }
}
