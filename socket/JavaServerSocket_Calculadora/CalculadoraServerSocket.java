import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class CalculadoraServerSocket {
    public static void main(String[] args) {
        Calculadora calc = new Calculadora();

        try (ServerSocket welcomeSocket = new ServerSocket(9090)) {
            System.out.println("Servidor iniciado na porta 9090");

            while (true) {
                try (Socket connectionSocket = welcomeSocket.accept()) {
                    System.out.println("cliente conectado");

                    BufferedReader entrada = new BufferedReader(
                            new InputStreamReader(connectionSocket.getInputStream()));

                    DataOutputStream saida = new DataOutputStream(connectionSocket.getOutputStream());

                    String expressao = entrada.readLine();
                    System.out.println("Expressão recebida: " + expressao);

                    try {
                        double resultado = calc.calcular(expressao);
                        
                        saida.writeBytes(resultado + "\n");
                        System.out.println("Resultado: " + resultado);

                    } catch (Exception e) {
                        saida.writeBytes("ERRO: " + e.getMessage() + "\n");
                    }

                    saida.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Calculadora {
    public double calcular(final String str) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < str.length()) ? str.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Caractere inesperado: " + (char)ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); 
                    else if (eat('-')) x -= parseTerm(); 
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); 
                    else if (eat('/')) x /= parseFactor(); 
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { 
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Caractere inesperado: " + (char)ch);
                }
                return x;
            }
        }.parse();
    }
}