package cn.hopefulme.jfortran.client;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.*;

 public class UI extends JFrame implements ActionListener{

	private JButton btn_compile, btn_run, btn_input;

	private JPanel pl_io, pl_tools;

	private GridBagLayout gbl_all, gbl_io, gbl_tools;
	private GridBagConstraints gbc;

	private JTextField tf_input;

	private JScrollPane sp_code, sp_output;
	private JTextArea ta_code, ta_output;

 	public UI () { 
		super("JFortran-Client");
		setSize(800,600);
		setLocation(200,100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Container and LayoutManager
		gbl_all = new GridBagLayout();
		setLayout(gbl_all);
		gbl_io = new GridBagLayout();
		pl_io = new JPanel(gbl_io);
		gbl_tools = new GridBagLayout();
		pl_tools = new JPanel(gbl_tools);
		gbc = new GridBagConstraints();
		// Buttons
		btn_compile = new JButton("Compile");
		btn_run = new JButton("Run");
		btn_input = new JButton("Input");
		// TextField
		tf_input = new JTextField(30);
		tf_input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					Client.sendInput(tf_input.getText());
				}
			}
		});
		// ScrollPane and TextArea
		ta_code = new JTextArea(35, 40);
		ta_code.setLineWrap(true);
		ta_code.setBackground(new Color(1.0f, 1.0f, 0.8f));
		ta_code.setTabSize(4);
		sp_code = new JScrollPane(ta_code, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ta_output = new JTextArea(35, 30);
		ta_output.setLineWrap(true);
		ta_output.setEditable(false);
		ta_output.setBackground(Color.BLACK);
		ta_output.setForeground(Color.GREEN);
		sp_output = new JScrollPane(ta_output, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// layout
		gbc.fill=GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weighty	= 1.0;
		gbl_all.setConstraints(pl_io, gbc);
		add(pl_io);
		add(pl_tools);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbl_io.setConstraints(sp_code, gbc);
		pl_io.add(sp_code);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbl_io.setConstraints(sp_output, gbc);
		pl_io.add(sp_output);
		pl_tools.add(btn_compile);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridwidth = 1;
		gbl_tools.setConstraints(tf_input, gbc);
		pl_tools.add(tf_input);
		pl_tools.add(btn_input);
		pl_tools.add(btn_run);
		// add listener
		btn_compile.setActionCommand("compile");
		btn_run.setActionCommand("run");
		btn_input.setActionCommand("input");
		btn_compile.addActionListener(this);
		btn_run.addActionListener(this);
		btn_input.addActionListener(this);
		setResizable(false);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch(event.getActionCommand()) {
			case "compile":
				Client.start(new Client.Callback() {
					@Override
					public void output(String msg) {
						updateOutput(msg+'\n');
					}
					@Override
					public void connected() {
						Client.sendCommand("compile");
						Client.sendCode(ta_code.getText());
					}
					@Override
					public void connectFailed() {
						updateOutput("Connected Failed!\n");
					}
				});
				break;
			case "run":
				Client.start(new Client.Callback() {
					@Override
					public void output(String msg) {
						updateOutput(msg+'\n');
					}
					@Override
					public void connected() {
						Client.sendCommand("run");
						Client.sendCode(ta_code.getText());
					}
					@Override
					public void connectFailed() {
						updateOutput("Connected Failed!\n");
					}
				});
				break;
			case "input":
				Client.sendInput(tf_input.getText());
				break;
		}
	}

	private void updateOutput(String output) {
		ta_output.append(output + "\n");
		ta_output.setCaretPosition(ta_output.getText().length());
	}

}
