import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface ClientInterface extends Remote {
    public void inicia(int id) throws RemoteException;
    public void finaliza() throws RemoteException;
    public void cutuca() throws RemoteException;
}
