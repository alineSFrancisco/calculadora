import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** Janela de apresentação: desenha a interface e encaminha ações ao modelo. */
public class JanelaCalculadora extends JFrame {
    private static final Color TEXTO = new Color(244, 241, 255);
    private static final Color TEXTO_SUAVE = new Color(162, 164, 196);
    private static final Color ROXO = new Color(165, 92, 255);
    private static final Color AZUL = new Color(67, 174, 255);
    private static final Color TECLA = new Color(48, 48, 79);

    private final Calculadora calculadora = new Calculadora();
    private final JTextField visor = new JTextField("0");
    private final JLabel expressaoVisor = new JLabel(" ", SwingConstants.RIGHT);
    private final Map<String, TeclaArredondada> botoes = new HashMap<>();
    private final Map<String, Color> coresNormais = new HashMap<>();
    private final JPanel listaHistorico = new ListaHistoricoPanel();
    private final JLabel mensagemHistorico = new JLabel(
            "<html><div style='width:150px'>Suas contas recentes aparecerão aqui</div></html>");

    public JanelaCalculadora() {
        super("Calculadora Neon");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(910, 640);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);
        montarInterface();
        configurarTeclado();
    }

    private void montarInterface() {
        FundoGradiente fundo = new FundoGradiente();
        fundo.setLayout(new BorderLayout(24, 0));
        fundo.setBorder(BorderFactory.createEmptyBorder(28, 32, 30, 32));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        JLabel titulo = new JLabel("CALCULADORA");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXTO);
        /**JLabel status = new JLabel("●  READY");
        status.setFont(new Font("Monospaced", Font.BOLD, 11));
        status.setForeground(new Color(119, 237, 200));*/
        cabecalho.add(titulo, BorderLayout.WEST);
        //cabecalho.add(status, BorderLayout.EAST);

        JPanel area = new JPanel(new BorderLayout(22, 0));
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        area.add(criarPainelCalculadora(), BorderLayout.CENTER);
        area.add(criarPainelHistorico(), BorderLayout.EAST);

        fundo.add(cabecalho, BorderLayout.NORTH);
        fundo.add(area, BorderLayout.CENTER);
        setContentPane(fundo);
    }

    private JPanel criarPainelCalculadora() {
        PainelVidro cartao = new PainelVidro(28);
        cartao.setLayout(new BorderLayout(0, 22));
        cartao.setBorder(BorderFactory.createEmptyBorder(26, 28, 26, 28));

        JPanel visorPainel = new JPanel(new BorderLayout(0, 10));
        visorPainel.setOpaque(false);
        expressaoVisor.setFont(new Font("SansSerif", Font.PLAIN, 19));
        expressaoVisor.setForeground(TEXTO_SUAVE);
        visor.setEditable(false);
        visor.setFocusable(false);
        visor.setHorizontalAlignment(SwingConstants.RIGHT);
        visor.setFont(new Font("SansSerif", Font.BOLD, 48));
        visor.setForeground(TEXTO);
        visor.setCaretColor(TEXTO);
        visor.setOpaque(false);
        visor.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        JPanel valoresVisor = new JPanel(new BorderLayout(0, 4));
        valoresVisor.setOpaque(false);
        valoresVisor.add(expressaoVisor, BorderLayout.NORTH);
        valoresVisor.add(visor, BorderLayout.CENTER);
        visorPainel.add(valoresVisor, BorderLayout.CENTER);

        JPanel teclado = new JPanel(new GridLayout(5, 4, 11, 11));
        teclado.setOpaque(false);
        adicionarTecla(teclado, "AC", calculadora::limpar, false, true);
        adicionarTecla(teclado, "⌫", calculadora::apagar, false, true);
        adicionarTecla(teclado, "%", calculadora::porcentagem, false, true);
        adicionarTecla(teclado, "÷", () -> calculadora.escolherOperacao('/'), true, false);

        adicionarTecla(teclado, "7", () -> calculadora.digitar("7"), false, false);
        adicionarTecla(teclado, "8", () -> calculadora.digitar("8"), false, false);
        adicionarTecla(teclado, "9", () -> calculadora.digitar("9"), false, false);
        adicionarTecla(teclado, "×", () -> calculadora.escolherOperacao('*'), true, false);

        adicionarTecla(teclado, "4", () -> calculadora.digitar("4"), false, false);
        adicionarTecla(teclado, "5", () -> calculadora.digitar("5"), false, false);
        adicionarTecla(teclado, "6", () -> calculadora.digitar("6"), false, false);
        adicionarTecla(teclado, "−", () -> calculadora.escolherOperacao('-'), true, false);

        adicionarTecla(teclado, "1", () -> calculadora.digitar("1"), false, false);
        adicionarTecla(teclado, "2", () -> calculadora.digitar("2"), false, false);
        adicionarTecla(teclado, "3", () -> calculadora.digitar("3"), false, false);
        adicionarTecla(teclado, "+", () -> calculadora.escolherOperacao('+'), true, false);

        adicionarTecla(teclado, "±", calculadora::inverterSinal, false, true);
        adicionarTecla(teclado, "0", () -> calculadora.digitar("0"), false, false);
        adicionarTecla(teclado, ".", calculadora::digitarPonto, false, false);
        adicionarTecla(teclado, "=", this::calcularERegistrar, true, true);

        cartao.add(visorPainel, BorderLayout.NORTH);
        cartao.add(teclado, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarPainelHistorico() {
        PainelVidro painel = new PainelVidro(24);
        painel.setPreferredSize(new Dimension(270, 0));
        painel.setLayout(new BorderLayout(0, 18));
        painel.setBorder(BorderFactory.createEmptyBorder(22, 20, 20, 20));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("HISTÓRICO");
        titulo.setFont(new Font("Monospaced", Font.BOLD, 13));
        titulo.setForeground(TEXTO);
        JButton limpar = new JButton("Limpar");
        limpar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        limpar.setForeground(TEXTO_SUAVE);
        limpar.setContentAreaFilled(false);
        limpar.setBorderPainted(false);
        limpar.setFocusPainted(false);
        limpar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        limpar.addActionListener(evento -> limparHistorico());
        topo.add(titulo, BorderLayout.WEST);
        topo.add(limpar, BorderLayout.EAST);

        listaHistorico.setOpaque(false);
        listaHistorico.setLayout(new javax.swing.BoxLayout(listaHistorico, javax.swing.BoxLayout.Y_AXIS));
        mensagemHistorico.setFont(new Font("SansSerif", Font.PLAIN, 13));
        mensagemHistorico.setForeground(TEXTO_SUAVE);
        mensagemHistorico.setAlignmentX(LEFT_ALIGNMENT);
        listaHistorico.add(mensagemHistorico);

        JScrollPane rolagem = new JScrollPane(listaHistorico);
        rolagem.setBorder(null);
        rolagem.setOpaque(false);
        rolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rolagem.getViewport().setOpaque(false);
        rolagem.getVerticalScrollBar().setUnitIncrement(14);
        rolagem.getVerticalScrollBar().setOpaque(false);

        JLabel rodape = new JLabel("CÁLCULOS DESTA SESSÃO");
        rodape.setFont(new Font("Monospaced", Font.PLAIN, 9));
        rodape.setForeground(TEXTO_SUAVE);
        painel.add(topo, BorderLayout.NORTH);
        painel.add(rolagem, BorderLayout.CENTER);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private void adicionarTecla(JPanel painel, String rotulo, Runnable acao,
            boolean destaque, boolean secundaria) {
        Color normal = secundaria ? new Color(52, 50, 78) : destaque ? ROXO : TECLA;
        TeclaArredondada botao = new TeclaArredondada(rotulo, destaque);
        botao.setFont(new Font("SansSerif", Font.BOLD, secundaria ? 16 : 21));
        botao.setForeground(TEXTO);
        botao.setBackground(normal);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setToolTipText("Tecla " + rotulo);
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evento) {
                botao.setBackground(normal.brighter());
            }

            @Override
            public void mouseExited(MouseEvent evento) {
                botao.setBackground(normal);
                botao.setForeground(TEXTO);
            }

            @Override
            public void mousePressed(MouseEvent evento) {
                botao.setBackground(Color.WHITE);
                botao.setForeground(new Color(39, 31, 67));
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                botao.setBackground(botao.contains(evento.getPoint()) ? normal.brighter() : normal);
                botao.setForeground(TEXTO);
            }
        });
        botao.addActionListener(evento -> {
            acao.run();
            atualizarVisor();
        });
        painel.add(botao);
        botoes.put(rotulo, botao);
        coresNormais.put(rotulo, normal);
    }

    private void calcularERegistrar() {
        String conta = calculadora.calcularResultado();
        if (conta != null) adicionarAoHistorico(conta);
    }

    private void adicionarAoHistorico(String conta) {
        listaHistorico.remove(mensagemHistorico);
        JPanel item = new PainelVidro(16);
        item.setLayout(new BorderLayout());
        item.setBorder(BorderFactory.createEmptyBorder(12, 13, 12, 13));
        JLabel expressao = new JLabel("<html><div style='width:155px'>" + conta + "</div></html>");
        expressao.setFont(new Font("SansSerif", Font.PLAIN, 14));
        expressao.setForeground(new Color(207, 204, 231));
        item.add(expressao, BorderLayout.CENTER);
        // Deixa o cartão crescer quando a expressão quebra em várias linhas.
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, item.getPreferredSize().height));
        listaHistorico.add(item, 0);
        listaHistorico.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)), 1);
        listaHistorico.revalidate();
        listaHistorico.repaint();
    }

    private void limparHistorico() {
        listaHistorico.removeAll();
        listaHistorico.add(mensagemHistorico);
        listaHistorico.revalidate();
        listaHistorico.repaint();
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
        TeclaArredondada botao = botoes.get(rotulo);
        if (botao == null) return;
        Color cor = coresNormais.get(rotulo);
        botao.setBackground(Color.WHITE);
        botao.setForeground(new Color(39, 31, 67));
        botao.doClick(0);
        Timer temporizador = new Timer(130, evento -> {
            botao.setBackground(cor);
            botao.setForeground(TEXTO);
        });
        temporizador.setRepeats(false);
        temporizador.start();
    }

    private void atualizarVisor() {
        visor.setText(calculadora.getVisor());
        String expressao = calculadora.getExpressao();
        expressaoVisor.setText(expressao.isEmpty() ? " " : expressao);
    }

    /** Fundo decorativo com gradiente e manchas de luz neon. */
    private static class FundoGradiente extends JPanel {
        @Override
        protected void paintComponent(Graphics grafico) {
            super.paintComponent(grafico);
            Graphics2D g = (Graphics2D) grafico.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(12, 14, 34),
                    getWidth(), getHeight(), new Color(22, 17, 51)));
            g.fillRect(0, 0, getWidth(), getHeight());
            for (int i = 9; i >= 0; i--) {
                int alpha = 5 + (10 - i) * 2;
                g.setColor(new Color(131, 63, 245, alpha));
                int tamanho = 210 + i * 28;
                g.fillOval(getWidth() - tamanho / 2, -tamanho / 3, tamanho, tamanho);
                g.setColor(new Color(32, 134, 255, alpha));
                g.fillOval(-tamanho / 2, getHeight() - tamanho / 2, tamanho, tamanho);
            }
            g.dispose();
        }
    }

    /** Painel translúcido de cantos arredondados para o visual de vidro. */
    private static class PainelVidro extends JPanel {
        private final int raio;

        PainelVidro(int raio) {
            this.raio = raio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            Graphics2D g = (Graphics2D) grafico.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(30, 32, 62, 208));
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g.setColor(new Color(190, 176, 255, 48));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, raio, raio);
            g.dispose();
            super.paintComponent(grafico);
        }
    }

    /** Mantém a lista estreita e deixa sua altura acompanhar a quantidade de itens. */
    private static class ListaHistoricoPanel extends JPanel {
        @Override
        public Dimension getPreferredSize() {
            Dimension tamanho = super.getPreferredSize();
            return new Dimension(185, tamanho.height);
        }
    }

    /** Botão desenhado com gradiente e cantos arredondados. */
    private static class TeclaArredondada extends JButton {
        private final boolean neon;

        TeclaArredondada(String rotulo, boolean neon) {
            super(rotulo);
            this.neon = neon;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        }

        @Override
        protected void paintComponent(Graphics grafico) {
            Graphics2D g = (Graphics2D) grafico.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int margem = getModel().isPressed() ? 2 : 0;
            if (neon && !Color.WHITE.equals(getBackground())) {
                g.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), AZUL));
            } else {
                g.setColor(getBackground());
            }
            g.fillRoundRect(margem, margem, getWidth() - 2 * margem, getHeight() - 2 * margem, 20, 20);
            if (neon) {
                g.setColor(new Color(185, 137, 255, 110));
                g.drawRoundRect(margem, margem, getWidth() - 2 * margem - 1,
                        getHeight() - 2 * margem - 1, 20, 20);
            }
            g.dispose();
            super.paintComponent(grafico);
        }
    }
}
