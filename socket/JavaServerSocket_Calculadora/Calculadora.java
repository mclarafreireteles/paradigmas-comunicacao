public class Calculadora {
    public double calcular(String expressao) throws Exception {
        String[] partes = expressao.split(" ");
        
        if (partes.length != 3) {
            throw new Exception("Formato inválido. Envie no formato: NUMERO OPERADOR NUMERO (ex: 5 + 3)");
        }

        double oper1 = Double.parseDouble(partes[0]);
        String operador = partes[1];
        double oper2 = Double.parseDouble(partes[2]);

        switch (operador) {
            case "+": return soma(oper1, oper2);
            case "-": return sub(oper1, oper2);
            case "*": return multi(oper1, oper2);
            case "/": 
                if (oper2 == 0) throw new Exception("Divisão por zero");
                return div(oper1, oper2);
            default:
                throw new Exception("Operador inválido. Use +, -, * ou /");
        }
    }

    public String sayHello(String nome, String sobrenome) {
        return "Fala "+ nome + " " + sobrenome;
    }
    public double soma(double oper1, double oper2) {
        return oper1 + oper2;
    }
    public double sub(double oper1, double oper2) {
        return oper1 - oper2;
    }
    public double multi(double oper1, double oper2) {
        return oper1 * oper2;
    }
    public double div(double oper1, double oper2) {
        return oper1 / oper2;
    }
}