import json
from http.server import BaseHTTPRequestHandler, HTTPServer

class CalculatorService:
    def somar(self, a, b): return a + b
    def subtrair(self, a, b): return a - b
    def multiplicar(self, a, b): return a * b
    def dividir(self, a, b):
        if b == 0: raise ValueError("Divisão por zero")
        return a / b
        
    def calcular(self, expressao):
        allowed_chars = "0123456789+-*/(). "
        if not all(c in allowed_chars for c in expressao):
            raise ValueError("Caracteres inválidos na expressão")
        return eval(expressao, {"__builtins__": None}, {})

class JSONRPCRequestHandler(BaseHTTPRequestHandler):
    service = CalculatorService()

    def do_POST(self):
        content_length = int(self.headers['Content-Length'])
        
        post_data = self.rfile.read(content_length).decode('utf-8')
        
        try:
            req = json.loads(post_data)
            
            method = req.get('method')
            params = req.get('params', [])
            req_id = req.get('id', 1)

            if method == "somar": resultado = self.service.somar(float(params[0]), float(params[1]))
            elif method == "subtrair": resultado = self.service.subtrair(float(params[0]), float(params[1]))
            elif method == "multiplicar": resultado = self.service.multiplicar(float(params[0]), float(params[1]))
            elif method == "dividir": resultado = self.service.dividir(float(params[0]), float(params[1]))
            elif method == "calcular": resultado = self.service.calcular(params[0])
            else: raise ValueError(f"Método '{method}' desconhecido")
            
            response = {"jsonrpc": "2.0", "result": resultado, "id": req_id}
            
        except Exception as e:
            response = {"jsonrpc": "2.0", "error": {"message": str(e)}, "id": req_id}

        self.send_response(200)
        
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        
        self.wfile.write(json.dumps(response).encode('utf-8'))

if __name__ == '__main__':
    server = HTTPServer(('localhost', 8080), JSONRPCRequestHandler)
    print("Servidor JSON-RPC a correr em http://localhost:8080")
    server.serve_forever() 