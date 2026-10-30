
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class CalculadoraClientSocket {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("===== CALCULADORA CLIENTE-SERVIDOR =====");
        System.out.println("Digite uma expressão matemática.");
        System.out.println("Exemplo: (10 + 5) * 2");
        System.out.println("Digite 'sair' para encerrar.");

        while (true) {

            System.out.print("\nExpressão: ");
            String expressao = scanner.nextLine();

            if (expressao.equalsIgnoreCase("sair")) {
                System.out.println("Calculadora encerrada.");
                break;
            }

            try (Socket clientSocket = new Socket("localhost", 9090)) {

                DataOutputStream saida = new DataOutputStream(
                        clientSocket.getOutputStream());

                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));

                saida.writeBytes(expressao + "\n");
                saida.flush();

                String resultado = entrada.readLine();

                System.out.println("Resultado: " + resultado);

            } catch (IOException e) {

                System.out.println("Erro ao conectar com o servidor.");
                System.out.println("Verifique se o servidor está iniciado.");
            }
        }

        scanner.close();
    }
}
