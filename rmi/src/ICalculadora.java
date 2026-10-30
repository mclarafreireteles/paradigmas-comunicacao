import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICalculadora extends Remote {
    
    public double soma(double a, double b) throws RemoteException;
    public double sub(double a, double b) throws RemoteException;
    public double multi(double a, double b) throws RemoteException;
    public double div(double a, double b) throws RemoteException;
    
    public double calcular(String expressao) throws RemoteException;
}