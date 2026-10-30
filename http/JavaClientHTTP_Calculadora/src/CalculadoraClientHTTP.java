import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection; // Trocado de Https para Http
import java.util.Scanner;

public class CalculadoraClientHTTP {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===== CALCULADORA HTTP (PYTHON SERVER) =====");
        System.out.print("Primeiro número: ");
        String oper1 = scanner.nextLine();
        
        System.out.print("Segundo número: ");
        String oper2 = scanner.nextLine();
        
        System.out.print("Operação (1-Somar, 2-Subtrair, 3-Multiplicar, 4-Dividir): ");
        String operacao = scanner.nextLine();

        try {
            // 1. Aponta para o servidor Python rodando na sua máquina
            URL url = URI.create("http://localhost:8080/").toURL();
            
            // 2. Abre a conexão HTTP simples (localhost não tem HTTPS)
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // 3. Monta e envia os parâmetros no formato de formulário que o Python espera
            String parametros = "oper1=" + oper1 + "&oper2=" + oper2 + "&operacao=" + operacao;
            
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(parametros);
            writer.flush();
            writer.close();
            os.close();

            // 4. Recebe o JSON formatado pelo servidor Python
            int responseCode = conn.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("\nJSON retornado pelo Python: " + response.toString());
            } else {
                System.out.println("\nErro: O servidor retornou o código HTTP " + responseCode);
            }
            
        } catch (IOException e) {
            System.out.println("\nFalha de comunicação. O servidor Python está rodando? Erro: " + e.getMessage());
        }
        
        scanner.close();
    }
}