import json
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import parse_qs

class CalculadoraPHPRequestHandler(BaseHTTPRequestHandler):
    
    def do_POST(self):
        content_length = int(self.headers.get('Content-Length', 0))
        post_data = self.rfile.read(content_length).decode('utf-8')
        
        _POST = parse_qs(post_data)
        
        def get_post_value(key, cast_func):
            if key in _POST:
                try:
                    return cast_func(_POST[key][0])
                except ValueError:
                    return None
            return None

        oper1 = get_post_value('oper1', float)
        oper2 = get_post_value('oper2', float)
        operacao = get_post_value('operacao', int)

        resultado = None
        erro = None

        if oper1 is None or oper2 is None or operacao is None:
            erro = "Parâmetros inválidos. Envie 'oper1', 'oper2' e 'operacao' via POST."
        else:
            if operacao == 1:
                resultado = oper1 + oper2
            elif operacao == 2:
                resultado = oper1 - oper2
            elif operacao == 3:
                resultado = oper1 * oper2
            elif operacao == 4:
                if oper2 == 0:
                    erro = "Erro: divisão por zero."
                else:
                    resultado = oper1 / oper2
            else:
                erro = "Operação inválida. Use 1 (soma), 2 (subtração), 3 (multiplicação) ou 4 (divisão)."

        if erro:
            resposta = {"erro": erro}
        else:
            resposta = {
                "oper1": oper1,
                "oper2": oper2,
                "operacao": operacao,
                "resultado": resultado
            }

        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        
        self.wfile.write(json.dumps(resposta).encode('utf-8'))

if __name__ == '__main__':
    server = HTTPServer(('localhost', 8080), CalculadoraPHPRequestHandler)
    print("Servidor rodando em http://localhost:8080")
    server.serve_forever()