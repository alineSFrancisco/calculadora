import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** Constrói a janela e conecta os botões à lógica da calculadora. */
public class JanelaCalculadora extends JFrame {
    private static final Color FUNDO = new Color(198, 174, 224);
    private static final Color PAINEL = new Color(226, 211, 240);
    private static final Color TECLA = new Color(207, 185, 229);
    private static final Color DESTAQUE = new Color(119, 72, 163);
    private static final Color TEXTO = new Color(47, 31, 61);

    private final Calculadora calculadora = new Calculadora();
    private final JTextField visor = new JTextField("0");
    private final Map<String, JButton> botoes = new HashMap<>();
    private final Map<String, Color> coresNormais = new HashMap<>();

    public JanelaCalculadora() {
        super("Calculadora");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(360, 520);
        setMinimumSize(new Dimension(320, 470));
        setLocationRelativeTo(null);
        montarInterface();
        configurarTeclado();
    }

    private void montarInterface() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setBackground(FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Calculadora");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(TEXTO);

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
        adicionarTecla(teclado, "AC", calculadora::limpar, false);
        adicionarTecla(teclado, "⌫", calculadora::apagar, false);
        adicionarTecla(teclado, "%", calculadora::porcentagem, false);
        adicionarTecla(teclado, "÷", () -> calculadora.escolherOperacao('/'), true);

        adicionarTecla(teclado, "7", () -> calculadora.digitar("7"), false);
        adicionarTecla(teclado, "8", () -> calculadora.digitar("8"), false);
        adicionarTecla(teclado, "9", () -> calculadora.digitar("9"), false);
        adicionarTecla(teclado, "×", () -> calculadora.escolherOperacao('*'), true);

        adicionarTecla(teclado, "4", () -> calculadora.digitar("4"), false);
        adicionarTecla(teclado, "5", () -> calculadora.digitar("5"), false);
        adicionarTecla(teclado, "6", () -> calculadora.digitar("6"), false);
        adicionarTecla(teclado, "−", () -> calculadora.escolherOperacao('-'), true);

        adicionarTecla(teclado, "1", () -> calculadora.digitar("1"), false);
        adicionarTecla(teclado, "2", () -> calculadora.digitar("2"), false);
        adicionarTecla(teclado, "3", () -> calculadora.digitar("3"), false);
        adicionarTecla(teclado, "+", () -> calculadora.escolherOperacao('+'), true);

        adicionarTecla(teclado, "±", calculadora::inverterSinal, false);
        adicionarTecla(teclado, "0", () -> calculadora.digitar("0"), false);
        adicionarTecla(teclado, ".", calculadora::digitarPonto, false);
        adicionarTecla(teclado, "=", calculadora::calcularResultado, true);

        conteudo.add(titulo, BorderLayout.NORTH);
        conteudo.add(visor, BorderLayout.CENTER);
        conteudo.add(teclado, BorderLayout.SOUTH);
        setContentPane(conteudo);
    }

    private void adicionarTecla(JPanel painel, String rotulo, Runnable acao, boolean destaque) {
        JButton botao = new JButton(rotulo);
        Color corNormal = destaque ? DESTAQUE : TECLA;
        Color corHover = corNormal.brighter();
        Color textoNormal = destaque ? Color.WHITE : TEXTO;

        botao.setFont(new Font("SansSerif", Font.BOLD, 20));
        botao.setForeground(textoNormal);
        botao.setBackground(corNormal);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                botao.setBackground(corHover);
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                botao.setBackground(corNormal);
                botao.setForeground(textoNormal);
            }

            @Override
            public void mousePressed(MouseEvent evento) {
                botao.setBackground(Color.WHITE);
                botao.setForeground(TEXTO);
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                botao.setBackground(botao.contains(evento.getPoint()) ? corHover : corNormal);
                botao.setForeground(textoNormal);
            }
        });
        botao.addActionListener(evento -> {
            acao.run();
            atualizarVisor();
        });

        painel.add(botao);
        botoes.put(rotulo, botao);
        coresNormais.put(rotulo, corNormal);
    }

    private void configurarTeclado() {
        KeyEventDispatcher dispatcher = evento -> {
            if (!isActive() || evento.getID() != KeyEvent.KEY_PRESSED) return false;
            String rotulo = botaoDaTecla(evento);
            if (rotulo == null) return false;
            clicarBotao(rotulo);
            evento.consume();
            return true;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent evento) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
            }
        });
    }

    private String botaoDaTecla(KeyEvent evento) {
        int codigo = evento.getKeyCode();
        boolean shift = evento.isShiftDown();
        if (codigo >= KeyEvent.VK_0 && codigo <= KeyEvent.VK_9) {
            if (shift && codigo == KeyEvent.VK_5) return "%";
            if (shift && codigo == KeyEvent.VK_8) return "×";
            return shift ? null : Integer.toString(codigo - KeyEvent.VK_0);
        }
        if (codigo >= KeyEvent.VK_NUMPAD0 && codigo <= KeyEvent.VK_NUMPAD9) {
            return Integer.toString(codigo - KeyEvent.VK_NUMPAD0);
        }
        switch (codigo) {
            case KeyEvent.VK_PERIOD:
            case KeyEvent.VK_COMMA:
            case KeyEvent.VK_DECIMAL: return ".";
            case KeyEvent.VK_ADD: return "+";
            case KeyEvent.VK_EQUALS: return shift ? "+" : "=";
            case KeyEvent.VK_MINUS:
            case KeyEvent.VK_SUBTRACT: return "−";
            case KeyEvent.VK_MULTIPLY: return "×";
            case KeyEvent.VK_SLASH:
            case KeyEvent.VK_DIVIDE: return "÷";
            case KeyEvent.VK_ENTER: return "=";
            case KeyEvent.VK_BACK_SPACE: return "⌫";
            case KeyEvent.VK_ESCAPE: return "AC";
            default: return null;
        }
    }

    private void clicarBotao(String rotulo) {
        JButton botao = botoes.get(rotulo);
        if (botao == null) return;
        botao.setBackground(Color.WHITE);
        botao.setForeground(TEXTO);
        botao.doClick(0);
        Timer temporizador = new Timer(120, evento -> {
            botao.setBackground(coresNormais.get(rotulo));
            botao.setForeground(rotulo.equals("+") || rotulo.equals("−")
                    || rotulo.equals("×") || rotulo.equals("÷") || rotulo.equals("=")
                    ? Color.WHITE : TEXTO);
        });
        temporizador.setRepeats(false);
        temporizador.start();
    }

    private void atualizarVisor() {
        visor.setText(calculadora.getVisor());
    }
}
