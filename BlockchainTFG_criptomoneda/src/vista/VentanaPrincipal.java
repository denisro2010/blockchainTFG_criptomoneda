package vista;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.border.LineBorder;


public class VentanaPrincipal extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 102673009839160787L;
	// private JPanel panelBotones;
	private JPanelBackground panelBotones;
	private JButton btnJugar;
	private JButton btnAyuda;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btnCreditos;
	private Component horizontalGlue;
	private Component horizontalGlue_1;
	private Component verticalGlue_1;
	private Component verticalStrut;
	private JButton buttonStart;
	private JButton btnNewButton;
	
	//protected static boolean espanol = false;
	

	/**
	 * Iniciar la aplicacion.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal frame = new VentanaPrincipal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Crear la ventana.
	 */
	public VentanaPrincipal() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaPrincipal.class.getResource("/resources/ico32.png")));
		setBackground(Color.DARK_GRAY);
		initialize();
	}

	private void initialize() {
		// setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaPrincipal.class.getResource("/resources/radar.png")));
		setTitle("UltimatePass");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 800);
		// panelBotones = new JPanel();
		panelBotones = new JPanelBackground();
		// panelBotones.setBackground(new Color(176, 224, 230));
		// "./src/main/java/resources/battlePortada.png"
		panelBotones.setBackground(VentanaPrincipal.class.getResource("/resources/battlePortada.png"));
		panelBotones.setBorder(new LineBorder(Color.LIGHT_GRAY, 2, true));
		panelBotones.setForeground(new Color(0, 0, 0));
		setContentPane(panelBotones);
		GridBagLayout gbl_panelBotones = new GridBagLayout();
		gbl_panelBotones.columnWidths = new int[] { 223, 152, 205, 0 };
		gbl_panelBotones.rowHeights = new int[] { 120, 0, 0, 0, 29, 29, 0, 29, 29, 0, 63, 0 };
		gbl_panelBotones.columnWeights = new double[] { 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelBotones.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelBotones.setLayout(gbl_panelBotones);
		centrarVentana();
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 1;
		gbc_verticalStrut.gridy = 0;
		panelBotones.add(getVerticalStrut(), gbc_verticalStrut);
		GridBagConstraints gbc_buttonStart = new GridBagConstraints();
		gbc_buttonStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonStart.insets = new Insets(0, 0, 5, 5);
		gbc_buttonStart.gridx = 1;
		gbc_buttonStart.gridy = 4;
		panelBotones.add(getButtonStart(), gbc_buttonStart);
		GridBagConstraints gbc_btnJugar = new GridBagConstraints();
		gbc_btnJugar.fill = GridBagConstraints.BOTH;
		gbc_btnJugar.insets = new Insets(0, 0, 5, 5);
		gbc_btnJugar.gridx = 1;
		gbc_btnJugar.gridy = 5;
		panelBotones.add(getBtnJugar(), gbc_btnJugar);
		GridBagConstraints gbc_horizontalGlue = new GridBagConstraints();
		gbc_horizontalGlue.fill = GridBagConstraints.HORIZONTAL;
		gbc_horizontalGlue.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalGlue.gridx = 0;
		gbc_horizontalGlue.gridy = 6;
		panelBotones.add(getHorizontalGlue_1(), gbc_horizontalGlue);
		GridBagConstraints gbc_horizontalGlue_1 = new GridBagConstraints();
		gbc_horizontalGlue_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_horizontalGlue_1.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalGlue_1.gridx = 2;
		gbc_horizontalGlue_1.gridy = 6;
		panelBotones.add(getHorizontalGlue_1_1(), gbc_horizontalGlue_1);
		GridBagConstraints gbc_btnAyuda = new GridBagConstraints();
		gbc_btnAyuda.fill = GridBagConstraints.BOTH;
		gbc_btnAyuda.insets = new Insets(0, 0, 5, 5);
		gbc_btnAyuda.gridx = 1;
		gbc_btnAyuda.gridy = 7;
		panelBotones.add(getBtnAyuda(), gbc_btnAyuda);
		GridBagConstraints gbc_btnCreditos = new GridBagConstraints();
		gbc_btnCreditos.fill = GridBagConstraints.BOTH;
		gbc_btnCreditos.insets = new Insets(0, 0, 5, 5);
		gbc_btnCreditos.gridx = 1;
		gbc_btnCreditos.gridy = 8;
		panelBotones.add(getBtnCreditos(), gbc_btnCreditos);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 9;
		panelBotones.add(getBtnNewButton(), gbc_btnNewButton);
		GridBagConstraints gbc_verticalGlue_1 = new GridBagConstraints();
		gbc_verticalGlue_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_verticalGlue_1.insets = new Insets(0, 0, 0, 5);
		gbc_verticalGlue_1.gridx = 1;
		gbc_verticalGlue_1.gridy = 10;
		panelBotones.add(getVerticalGlue_1_1(), gbc_verticalGlue_1);
	}
	

	private void centrarVentana() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = this.getSize();
		if (windowSize.height > screenSize.height) {
			windowSize.height = screenSize.height;
		}
		if (windowSize.width > screenSize.width) {
			windowSize.width = screenSize.width;
		}
		setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
	}

	private JButton getBtnJugar() {
		if (btnJugar == null) {
			btnJugar = new JButton("Create a new user");
			btnJugar.setHorizontalTextPosition(SwingConstants.CENTER);
			buttonGroup.add(btnJugar);
			btnJugar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					VentanaDatosEN v = new VentanaDatosEN();
					v.setVisible(true);
				}
			});
		}
		return btnJugar;
	}

	private JButton getBtnAyuda() {
		if (btnAyuda == null) {
			btnAyuda = new JButton("Help");
			btnAyuda.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					VentanaAyudaEN vA = new VentanaAyudaEN();
					vA.setVisible(true);
				}
			});
			buttonGroup.add(btnAyuda);
		}
		return btnAyuda;
	}

	private JButton getBtnCreditos() {
		if (btnCreditos == null) {
			btnCreditos = new JButton("Credits");
			btnCreditos.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					VentanaCreditosEN vC = new VentanaCreditosEN();
					vC.setVisible(true);
					//dispose();
				}
			});
			buttonGroup.add(btnCreditos);
		}
		return btnCreditos;
	}

	private Component getHorizontalGlue_1() {
		if (horizontalGlue == null) {
			horizontalGlue = Box.createHorizontalGlue();
		}
		return horizontalGlue;
	}

	private Component getHorizontalGlue_1_1() {
		if (horizontalGlue_1 == null) {
			horizontalGlue_1 = Box.createHorizontalGlue();
		}
		return horizontalGlue_1;
	}

	private Component getVerticalGlue_1_1() {
		if (verticalGlue_1 == null) {
			verticalGlue_1 = Box.createVerticalGlue();
		}
		return verticalGlue_1;
	}

	private Component getVerticalStrut() {
		if (verticalStrut == null) {
			verticalStrut = Box.createVerticalStrut(20);
		}
		return verticalStrut;
	}

	private JButton getButtonStart() {
		if (buttonStart == null) {
			buttonStart = new JButton("Start");
			buttonStart.setHorizontalTextPosition(SwingConstants.CENTER);
			buttonStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						boolean emptyDB = true;
						
						try {
							emptyDB = filesControl.checkEmptyUsers();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
			            if(emptyDB) {
			            	JOptionPane.showMessageDialog(null, "No user found. Please create a new user.");
			            	/*filesControl.getFileContras().delete();		            	
			            	if(filesControl.getFileUsers().exists())
			            		filesControl.getFileUsers().delete();*/
			            }
			            else {
			            VentanaIntroduceMasterPassEN v = new VentanaIntroduceMasterPassEN();
						v.setVisible(true);
			            }

					}
			});
				
				
			buttonGroup.add(buttonStart);
		}
		return buttonStart;
	}
	private JButton getBtnNewButton() {
		if (btnNewButton == null) {
			btnNewButton = new JButton("Exit");
		}
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		return btnNewButton;
	}
}
