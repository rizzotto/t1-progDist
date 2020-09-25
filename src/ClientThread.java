
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientThread extends Thread {
    protected String[] thread_args;
    private static Random random = new Random();
    public static boolean finalizouClient;
    public static boolean iniciaClient;



    public ClientThread(String[] args) {
        thread_args = args;
    }

    public void updateFinalizou(boolean f){
        finalizouClient = f;
    }
    public void updateInicia(boolean i){
        iniciaClient = i;
    }

    public void run() {
        int id = -1;

        try {
            String client = "rmi://" + thread_args[1] + ":" + thread_args[2] + "/server2";
            Naming.rebind(client, new Client());
            System.out.println("Addition Server is ready.");
        } catch (Exception e) {
            System.out.println("Addition Serverfailed: " + e);
        }

        String remoteHostName = thread_args[0];
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
                    id = server.registra(Integer.parseInt(thread_args[2]));

                }
                if(iniciaClient){
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

}
