
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class Server extends UnicastRemoteObject implements JogoInterface {
    private static volatile int registrou;
    private static volatile boolean changed;
    private static volatile boolean jogadaChamada;
    private static volatile boolean jogadorFinalizado;
    private static volatile String remoteHostName;
    private static Random random = new Random();
    private static volatile List<Integer> listaUsuarios = new ArrayList<>();


    public Server() throws RemoteException {
    }

    public static void main(String[] args) throws RemoteException {
        if (args.length != 2) {
            System.out.println("Usage: java Server <server ip> <N>");
            System.exit(1);
        }

        try {
            System.setProperty("java.rmi.server.hostname", args[0]);
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        try {
            String server = "//" + args[0] + "/server";
            Naming.rebind(server, new Server());
            System.out.println("Server is ready.");
        } catch (Exception e) {
            System.out.println("Serverfailed: " + e);
        }

        while (true) {
            if (changed || jogadaChamada) {

                String connectLocation = "//" + remoteHostName + "/server2";

                JogadorInterface server2 = null;
                try {
                    System.out.println("Calling client back at : " + connectLocation);
                    server2 = (JogadorInterface) Naming.lookup(connectLocation);
                } catch (Exception e) {
                    System.out.println ("Callback failed: ");
                    e.printStackTrace();
                }

                try {
                    int jogadores = Integer.parseInt(args[1]);
                    if(registrou == jogadores){
                        if(listaUsuarios.size() == jogadores && changed ){
                            for(int i=0; i < jogadores; i++){
                                server2.inicia(listaUsuarios.get(i));

                            }
                        }
                    }

                    if(jogadaChamada){
                        jogadaChamada = false;
                        var r = Math.random();
                        if(r < 0.01){
                            server2.finaliza();

                            return;
                        }
                    }

                    //cutucada
//                    JogadorInterface finalServer = server2;
//                    Thread t = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                finalServer.cutuca();
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    try {
//                        t.sleep(3000);
//                        t.start();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                changed = false;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
        }
    }



    public int registra() {
        int numero = random.nextInt();
        listaUsuarios.add(numero);
        registrou++;
        try {
            remoteHostName = getClientHost();
        } catch (Exception e) {
            System.out.println ("Failed to get client IP");
            e.printStackTrace();
        }
        changed = true;
        return numero;
    }

    public int joga(int id) {
        System.out.println("Jogador: " + id + " acabou de realizar uma jogada");
        jogadaChamada = true;

        return 1;
    }

    public int encerra(int id) {

//        for (int i = 0; i < listaUsuarios.size(); i++) {
//            if (id == listaUsuarios.get(i))
//                listaUsuarios.remove(id);
                System.out.println("Jogador: " + id + " removido");
//        }

        return 2;
    }

}
