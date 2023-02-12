package pcr.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.TextField;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JTabbedPane;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pcr.PCR;
import pmr.PMR.Mensajes;

public class PanelPCR extends JFrame {
    private JTextField textField;
    private JTextField trainNumber;
    public JTextArea textLlamadaEnt;
    public JTextArea textLlamadaSal;

    public PCR pcr;
    public List<BotonPCR> botones = new ArrayList<>();

    public PanelPCR(PCR pcr) {

        this.pcr = pcr;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.weightx = gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel panelEnviarMensaje = new JPanel();
        panelEnviarMensaje.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Mensaje de texto", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.weightx = 0;
        add(panelEnviarMensaje, gbc);
        panelEnviarMensaje.setLayout(new BorderLayout());

        textField = new JTextField();
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        panelEnviarMensaje.add(textField, BorderLayout.CENTER);
        textField.setColumns(14);

        JPanel panelBotones = new JPanel();
        panelBotones.setBorder(new TitledBorder(null, "Acciones", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 5;
        gbc.weightx = 1;
        add(panelBotones,gbc);
        panelBotones.setLayout(new GridLayout(6, 2));

        botones.add(new BotonPCR("Hable", BotonPCR.Botones.Hable));
        botones.add(new BotonPCR("Conexión Megafonia", BotonPCR.Botones.ConexionMegafonia));
        botones.add(new BotonPCR("Conforme", BotonPCR.Botones.ConformeTierra));
        botones.add(new BotonPCR("Autorizo sistema C", BotonPCR.Botones.AutModC));
        botones.add(new BotonPCR("Suprima parada", BotonPCR.Botones.SupresionParada));
        botones.add(new BotonPCR("Atento a señal", BotonPCR.Botones.AtentoSeñal));
        botones.add(new BotonPCR("Reduzca marcha", BotonPCR.Botones.ReduzcaMarcha));
        botones.add(new BotonPCR("Baje pantografo", BotonPCR.Botones.BajePantografo));
        botones.add(new BotonPCR("Suba pantografo", BotonPCR.Botones.SubaPantografo));
        botones.add(new BotonPCR("Parada inmediata", BotonPCR.Botones.AltoUrgente));
        botones.add(new BotonPCR("Texto", BotonPCR.Botones.Texto));
        botones.add(new BotonPCR("Llamada general", BotonPCR.Botones.LlamadaGeneral));
        for (BotonPCR b : botones) {
            panelBotones.add(b);
        }
        BotonPCR.setPressedActions(this);

        JPanel panelMensajes = new JPanel();
        panelMensajes.setBorder(new TitledBorder(null, "Mensajes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        add(panelMensajes, gbc);

        /*panelMensajes.setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        panelMensajes.add(tabbedPane, BorderLayout.CENTER);*/
        
        panelMensajes.setLayout(new GridLayout());

        textLlamadaEnt = new JTextArea();
        textLlamadaEnt.setWrapStyleWord(true);
        textLlamadaEnt.setText("");
        textLlamadaEnt.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textLlamadaEnt.setEditable(false);

        JScrollPane scrollpaneLE = new JScrollPane();
        scrollpaneLE.setBorder(new TitledBorder(null, "Entrantes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        //tabbedPane.addTab("Entrantes", null, scrollpaneLE, null);
        panelMensajes.add(scrollpaneLE);
        scrollpaneLE.setViewportView(textLlamadaEnt);

        textLlamadaSal = new JTextArea();
        textLlamadaSal.setText("");
        textLlamadaSal.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textLlamadaSal.setEditable(false);

        JScrollPane scrollpaneLS = new JScrollPane();
        scrollpaneLS.setBorder(new TitledBorder(null, "Salientes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        //tabbedPane.addTab("Salientes", null, scrollpaneLS, null);
        panelMensajes.add(scrollpaneLS);
        scrollpaneLS.setViewportView(textLlamadaSal);

        JPanel panelListaTren = new JPanel();
        panelListaTren.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Nº de tren", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.weightx = 0;
        add(panelListaTren, gbc);
        panelListaTren.setLayout(new BorderLayout());
        trainNumber = new JTextField();
        trainNumber.setHorizontalAlignment(SwingConstants.LEFT);
        trainNumber.setColumns(14);
        panelListaTren.add(trainNumber, BorderLayout.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        JCheckBox onTop = new JCheckBox("Traer al frente");
        onTop.addActionListener((arg) -> {
        	setAlwaysOnTop(onTop.isSelected());
        });
        add(onTop, gbc);
        
        JButton listaTrenes = new JButton("Trenes en línea");
	    listaTrenes.addActionListener((arg0) -> {
			List<Integer> old = new ArrayList<>();
			for (Integer i : pcr.listatrenes.keySet())
			{
				if (pcr.listatrenes.get(i)<System.currentTimeMillis()-30000) old.add(i);
			}
			for (Integer i : old)
			{
				pcr.listatrenes.remove(i);
			}
			String trenes = "";
			if (pcr.listatrenes.isEmpty())
			{
				trenes = "Sin trenes en la banda de regulación";
			}
			else
			{
				for (Integer i : pcr.listatrenes.keySet())
				{
					trenes += i.toString() + "\n";
				}
			}
			JOptionPane.showMessageDialog(PanelPCR.this, trenes, "Lista de trenes", JOptionPane.INFORMATION_MESSAGE);
		});
	    gbc.gridy++;
	    add(listaTrenes, gbc);

        //Producion
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        this.addComponentListener(new ComponentListener() 
		{
			@Override
			public void componentHidden(ComponentEvent arg0) {}
			@Override
			public void componentMoved(ComponentEvent arg0) {}
			@Override
			public void componentResized(ComponentEvent arg0) {
				Dimension size = getSize();
				float scale = (float) Math.min(size.getHeight()/500, size.getWidth()/700);
				if (scale > 0)
				{
					textLlamadaSal.setFont(new Font("Monospaced", Font.PLAIN, (int)(11*scale)));
					textLlamadaEnt.setFont(new Font("Monospaced", Font.PLAIN, (int)(11*scale)));
				}
			}
			@Override
			public void componentShown(ComponentEvent arg0) {}
		});
    }

    void send(Mensajes msg) {
        int trn = -1;
        try {
            trn = Integer.parseInt(trainNumber.getText());
        } catch (Exception e) {
        	return;
        }
        pcr.enviarMensaje(msg, trn);
    }

    void sendText() {
        //pcr.enviarTexto(JOptionPane.showInputDialog("Texto a enviar"), Integer.parseInt(ntren.getText()));

        String textoRevisado = textField.getText().replace("\n", "").replace("\r", "");
        pcr.enviarTexto(textoRevisado, Integer.parseInt(trainNumber.getText()));
        textField.setText("");
    }
}
