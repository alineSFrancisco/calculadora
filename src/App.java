import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class App {
    private static final Color FUNDO = new Color(198, 174, 224);
    private static final Color PAINEL = new Color(226, 211, 240);
    private static final Color TECLA = new Color(207, 185, 229);
    private static final Color DESTAQUE = new Color(119, 72, 163);
    private static final Color TEXTO = new Color(47, 31, 61);
    private static final Color SECUNDARIO = new Color(100, 78, 119);

    private static JTextField visor;
    private static String entrada = "0";
    private static double valorGuardado;
    private static char operacaoPendente;
    private static boolean iniciarNovaEntrada = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::criarJanela);
    }

    private static void criarJanela() {
        JFrame janela = new JFrame("Calculadora");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setSize(360, 520);
        janela.setMinimumSize(new Dimension(320, 470));
        janela.setLocationRelativeTo(null);

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        javax.swing.JLabel titulo = new javax.swing.JLabel("Calculadora");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(TEXTO);

        visor = new JTextField("0");
        visor.setEditable(false);
        visor.setFocusable(false);
        visor.setHorizontalAlignment(SwingConstants.RIGHT);
        visor.setFont(new Font("SansSerif", Font.BOLD, 36));
        visor.setForeground(TEXTO);
        visor.setBackground(PAINEL);
        visor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(177, 151, 204), 1, true),
                BorderFactory.createEmptyBorder(18, 14, 18, 14)));

        JPanel teclado = new JPanel(new GridLayout(5, 4, 10, 10));
        teclado.setBackground(FUNDO);
        adicionarTecla(teclado, "AC", App::limpar, false);
        adicionarTecla(teclado, "⌫", App::apagar, false);
        adicionarTecla(teclado, "%", App::porcentagem, false);
        adicionarTecla(teclado, "÷", () -> escolherOperacao('/'), true);

        adicionarTecla(teclado, "7", () -> digitar("7"), false);
        adicionarTecla(teclado, "8", () -> digitar("8"), false);
        adicionarTecla(teclado, "9", () -> digitar("9"), false);
        adicionarTecla(teclado, "×", () -> escolherOperacao('*'), true);

        adicionarTecla(teclado, "4", () -> digitar("4"), false);
        adicionarTecla(teclado, "5", () -> digitar("5"), false);
        adicionarTecla(teclado, "6", () -> digitar("6"), false);
        adicionarTecla(teclado, "−", () -> escolherOperacao('-'), true);

        adicionarTecla(teclado, "1", () -> digitar("1"), false);
        adicionarTecla(teclado, "2", () -> digitar("2"), false);
        adicionarTecla(teclado, "3", () -> digitar("3"), false);
        adicionarTecla(teclado, "+", () -> escolherOperacao('+'), true);

        adicionarTecla(teclado, "±", App::inverterSinal, false);
        adicionarTecla(teclado, "0", () -> digitar("0"), false);
        adicionarTecla(teclado, ".", App::digitarPonto, false);
        adicionarTecla(teclado, "=", App::calcularResultado, true);

        conteudo.add(titulo, BorderLayout.NORTH);
        conteudo.add(visor, BorderLayout.CENTER);
        conteudo.add(teclado, BorderLayout.SOUTH);
        janela.setContentPane(conteudo);
        janela.setVisible(true);
    }

    private static JButton adicionarTecla(JPanel painel, String texto, Runnable acao, boolean destaque) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 20));
        botao.setForeground(destaque ? Color.WHITE : TEXTO);
        botao.setBackground(destaque ? DESTAQUE : TECLA);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.addActionListener(e -> acao.run());
        painel.add(botao);
        return botao;
    }

    // Adiciona um dígito ao número que está sendo digitado.
    private static void digitar(String digito) {
        if (iniciarNovaEntrada || entrada.equals("0")) {
            entrada = digito;
            iniciarNovaEntrada = false;
        } else if (entrada.length() < 14) {
            entrada += digito;
        }
        atualizarVisor();
    }

    private static void digitarPonto() {
        if (iniciarNovaEntrada) {
            entrada = "0.";
            iniciarNovaEntrada = false;
        } else if (!entrada.contains(".")) {
            entrada += ".";
        }
        atualizarVisor();
    }

    private static void escolherOperacao(char operacao) {
        double atual = lerEntrada();
        if (operacaoPendente != '\0' && !iniciarNovaEntrada) {
            atual = executar(valorGuardado, atual, operacaoPendente);
            if (Double.isNaN(atual)) {
                mostrarErro();
                return;
            }
            entrada = formatar(atual);
            atualizarVisor();
        }
        valorGuardado = atual;
        operacaoPendente = operacao;
        iniciarNovaEntrada = true;
    }

    private static void calcularResultado() {
        if (operacaoPendente == '\0' || iniciarNovaEntrada) {
            return;
        }
        double resultado = executar(valorGuardado, lerEntrada(), operacaoPendente);
        if (Double.isNaN(resultado)) {
            mostrarErro();
            return;
        }
        entrada = formatar(resultado);
        operacaoPendente = '\0';
        iniciarNovaEntrada = true;
        atualizarVisor();
    }

    private static double executar(double primeiro, double segundo, char operacao) {
        switch (operacao) {
            case '+': return primeiro + segundo;
            case '-': return primeiro - segundo;
            case '*': return primeiro * segundo;
            case '/': return segundo == 0 ? Double.NaN : primeiro / segundo;
            default: return segundo;
        }
    }

    private static void limpar() {
        entrada = "0";
        valorGuardado = 0;
        operacaoPendente = '\0';
        iniciarNovaEntrada = true;
        atualizarVisor();
    }

    private static void apagar() {
        if (iniciarNovaEntrada) {
            return;
        }
        entrada = entrada.length() > 1 ? entrada.substring(0, entrada.length() - 1) : "0";
        if (entrada.equals("-")) {
            entrada = "0";
        }
        atualizarVisor();
    }

    private static void inverterSinal() {
        if (!entrada.equals("0")) {
            entrada = entrada.startsWith("-") ? entrada.substring(1) : "-" + entrada;
            atualizarVisor();
        }
    }

    // Converte o número exibido para seu equivalente percentual (25 passa a 0,25).
    private static void porcentagem() {
        if (entrada.equals("Erro")) {
            return;
        }
        entrada = formatar(lerEntrada() / 100);
        iniciarNovaEntrada = true;
        atualizarVisor();
    }

    private static double lerEntrada() {
        return Double.parseDouble(entrada);
    }

    private static String formatar(double valor) {
        if (valor == Math.rint(valor) && Math.abs(valor) < 1e15) {
            return String.format(java.util.Locale.ROOT, "%.0f", valor);
        }
        return String.format(java.util.Locale.ROOT, "%.8f", valor)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }

    private static void mostrarErro() {
        entrada = "Erro";
        operacaoPendente = '\0';
        iniciarNovaEntrada = true;
        atualizarVisor();
    }

    private static void atualizarVisor() {
        visor.setText(entrada);
    }
}
