package practice2;



import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;


public class Client {
    public static final String UNIQUE_BINDING_NAME = "equation.solver";


    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(4321);
        Solver equationSolver = (Solver) registry.lookup(UNIQUE_BINDING_NAME);


        while (true) {
            Scanner scanner = new Scanner(System.in);
            String equation = scanner.nextLine();
            System.out.println(equationSolver.getRootsOfTheQuadraticEquation(equation));
        }
    }
}
