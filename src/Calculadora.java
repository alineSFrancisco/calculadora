import java.util.Locale;

/** Guarda os valores e executa as operações da calculadora. */
public class Calculadora {
    private String entrada = "0";
    private double valorGuardado;
    private char operacaoPendente;
    private boolean iniciarNovaEntrada = true;

    public String getVisor() {
        return entrada;
    }

    public void digitar(String digito) {
        if (entrada.equals("Erro")) limpar();
        if (iniciarNovaEntrada || entrada.equals("0")) {
            entrada = digito;
            iniciarNovaEntrada = false;
        } else if (entrada.length() < 14) {
            entrada += digito;
        }
    }

    public void digitarPonto() {
        if (entrada.equals("Erro")) limpar();
        if (iniciarNovaEntrada) {
            entrada = "0.";
            iniciarNovaEntrada = false;
        } else if (!entrada.contains(".")) {
            entrada += ".";
        }
    }

    public void escolherOperacao(char operacao) {
        if (entrada.equals("Erro")) return;
        double atual = lerEntrada();
        if (operacaoPendente != '\0' && !iniciarNovaEntrada) {
            atual = executar(valorGuardado, atual, operacaoPendente);
            if (Double.isNaN(atual)) {
                mostrarErro();
                return;
            }
            entrada = formatar(atual);
        }
        valorGuardado = atual;
        operacaoPendente = operacao;
        iniciarNovaEntrada = true;
    }

    public void calcularResultado() {
        if (entrada.equals("Erro") || operacaoPendente == '\0' || iniciarNovaEntrada) return;
        double resultado = executar(valorGuardado, lerEntrada(), operacaoPendente);
        if (Double.isNaN(resultado)) {
            mostrarErro();
            return;
        }
        entrada = formatar(resultado);
        operacaoPendente = '\0';
        iniciarNovaEntrada = true;
    }

    public void limpar() {
        entrada = "0";
        valorGuardado = 0;
        operacaoPendente = '\0';
        iniciarNovaEntrada = true;
    }

    public void apagar() {
        if (entrada.equals("Erro")) {
            limpar();
            return;
        }
        if (iniciarNovaEntrada && operacaoPendente != '\0') return;
        iniciarNovaEntrada = false;
        entrada = entrada.length() > 1 ? entrada.substring(0, entrada.length() - 1) : "0";
        if (entrada.equals("-")) entrada = "0";
    }

    public void inverterSinal() {
        if (!entrada.equals("Erro") && !entrada.equals("0")) {
            entrada = entrada.startsWith("-") ? entrada.substring(1) : "-" + entrada;
        }
    }

    public void porcentagem() {
        if (entrada.equals("Erro")) return;
        entrada = formatar(lerEntrada() / 100);
        iniciarNovaEntrada = true;
    }

    private double lerEntrada() {
        return Double.parseDouble(entrada);
    }

    private double executar(double primeiro, double segundo, char operacao) {
        switch (operacao) {
            case '+': return primeiro + segundo;
            case '-': return primeiro - segundo;
            case '*': return primeiro * segundo;
            case '/': return segundo == 0 ? Double.NaN : primeiro / segundo;
            default: return segundo;
        }
    }

    private void mostrarErro() {
        entrada = "Erro";
        operacaoPendente = '\0';
        iniciarNovaEntrada = true;
    }

    private String formatar(double valor) {
        if (valor == Math.rint(valor) && Math.abs(valor) < 1e15) {
            return String.format(Locale.ROOT, "%.0f", valor);
        }
        return String.format(Locale.ROOT, "%.8f", valor)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }
}
