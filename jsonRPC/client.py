import json
import urllib.request
import urllib.error
import time

def invoke_remote(method, params):
    url = "http://localhost:8080"
    
    # Monta o payload (corpo da mensagem) definindo a operação no padrão JSON-RPC 2.0.
    payload = {
        "jsonrpc": "2.0",
        "method": method,
        "params": params,
        "id": 1
    }
    
    # Serializa o dicionário Python para uma string JSON e codifica-a em bytes para transmissão.
    data = json.dumps(payload).encode('utf-8')
    
    # Constrói o objeto de requisição HTTP POST, injetando o cabeçalho 'Content-Type'.
    req = urllib.request.Request(url, data=data, headers={'Content-Type': 'application/json'})

    max_retries = 3
    attempt = 0
    
    # Implementação do Retry Pattern (Wait Ts and Retry)
    while attempt < max_retries:
        try:
            # Abre a conexão de rede e envia a requisição HTTP com um timeout de 5 segundos.
            with urllib.request.urlopen(req, timeout=5) as response:
                
                # Lê os bytes recebidos da rede e descodifica-os de volta para string.
                resp_data = response.read().decode('utf-8')
                
                # Faz o parse da string JSON de resposta para um dicionário Python.
                resp_json = json.loads(resp_data)
                
                if "error" in resp_json:
                    raise Exception(resp_json["error"]["message"])
                return resp_json.get("result")
                
        except (urllib.error.URLError, ConnectionResetError) as e:
            attempt += 1
            print(f"[Aviso] Falha na rede. Tentativa {attempt} de {max_retries}. Aguardando 2s...")
            time.sleep(2) # Pausa a execução por 2 segundos (Wait Ts) antes da próxima tentativa.
            
    raise Exception("Falha crítica de ligação ao servidor.")

def main():
    print("=== CLIENTE JSON-RPC ===")
    print("Digite +, -, *, / para operação passo a passo")
    print("Ou digite a expressão matemática diretamente (ex: 1+1 ou (10+15)*4)")
    
    while True:
        op = input("\nEntrada ou 'sair': ").strip()
        if op.lower() == 'sair':
            break
            
        try:
            # Se for um operador isolado, faz o passo a passo
            if op in ["+", "-", "*", "/"]:
                a = input("Primeiro número: ")
                b = input("Segundo número: ")
                method = {"+": "somar", "-": "subtrair", "*": "multiplicar", "/": "dividir"}[op]
                resultado = invoke_remote(method, [a, b])
                
            # Se o usuário digitou qualquer outra coisa (como "1+1"), assume que é uma expressão
            else:
                # Remove espaços em branco por segurança antes de enviar
                expressao = op.replace(" ", "")
                resultado = invoke_remote("calcular", [expressao])
                
            print(f"-> Resposta remota do servidor: {resultado}")
            
        except Exception as e:
            print(f"-> Falha RPC: {e}")

if __name__ == '__main__':
    main()