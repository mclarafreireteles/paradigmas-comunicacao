import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CalculadoraCliente {
    
    public static void main(String[] args) {
        Registry reg = null;
        ICalculadora calc;      
        
        try {
            reg = LocateRegistry.getRegistry("localhost", 1099);
            
            calc = (ICalculadora) reg.lookup("calculadora");
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("===== CLIENTE RMI =====");
            System.out.println("Exemplo de operação direta no servidor (RMI):");
            System.out.println("3 + 2 = " + calc.soma(3.0, 2.0));
            
            System.out.println("\nDigite uma expressão (ex: (10 + 15) * 4) ou 'sair':");
            
            while (true) {
                System.out.print("\nExpressão: ");
                String expressao = scanner.nextLine();
                
                if (expressao.equalsIgnoreCase("sair")) break;
                
                try {
                    // Invoca o método remoto para processar a expressão complexa
                    double resultado = calc.calcular(expressao);
                    System.out.println("Resultado RMI: " + resultado);
                } catch (RemoteException e) {
                    System.out.println("Erro retornado pelo servidor: " + e.getMessage());
                }
            }
            scanner.close();

        } catch (RemoteException | NotBoundException e) {
            System.out.println("Falha na comunicação RMI: " + e.getMessage());
            System.exit(0);
        }
    }       
}