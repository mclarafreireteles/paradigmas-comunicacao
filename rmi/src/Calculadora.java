import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Calculadora implements ICalculadora {

    private static final long serialVersionUID = 1L;
    private static int chamadas = 0;

    public double soma(double a, double b) throws RemoteException {
        System.out.println("Método soma chamado " + ++chamadas);
        return a + b;
    }
    public double sub(double a, double b) throws RemoteException { return a - b; }
    public double multi(double a, double b) throws RemoteException { return a * b; }
    public double div(double a, double b) throws RemoteException {
        if (b == 0) throw new RemoteException("Divisão por zero");
        return a / b; 
    }

    public double calcular(final String str) throws RemoteException {
        System.out.println("Método calcular chamado " + ++chamadas + " | Expressão: " + str);
        try {
            return new Object() {
                int pos = -1, ch;
                void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) { nextChar(); return true; }
                    return false;
                }
                double parse() {
                    nextChar(); double x = parseExpression();
                    if (pos < str.length()) throw new RuntimeException("Erro sintático");
                    return x;
                }
                double parseExpression() {
                    double x = parseTerm();
                    for (;;) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }
                double parseTerm() {
                    double x = parseFactor();
                    for (;;) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }
                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();
                    double x; int startPos = this.pos;
                    if (eat('(')) { x = parseExpression(); eat(')'); } 
                    else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(str.substring(startPos, this.pos));
                    } else { throw new RuntimeException("Caractere inválido"); }
                    return x;
                }
            }.parse();
        } catch (Exception e) {
            throw new RemoteException(e.getMessage());
        }
    }

    public static void main(String[] args) throws AccessException, RemoteException, AlreadyBoundException  {
        Calculadora calculadora = new Calculadora();        
        Registry reg = null;
        
        ICalculadora stub = (ICalculadora) UnicastRemoteObject.exportObject(calculadora, 1100);
        
        try {
            System.out.println("Criando registro RMI...");
            reg = LocateRegistry.createRegistry(1099);
        } catch (Exception e) {
            try {
                reg = LocateRegistry.getRegistry(1099);
            } catch (Exception e1) {
                System.exit(0);
            }
        }
        
        
        reg.rebind("calculadora", stub);
        System.out.println("Servidor RMI inicializado e aguardando chamadas.");
    }
}