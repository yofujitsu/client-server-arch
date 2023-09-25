package practice2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface Solver extends Remote {


    List<Double> getRootsOfTheQuadraticEquation(String equation) throws RemoteException;
}
