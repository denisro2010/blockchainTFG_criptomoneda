package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.TitledBorder;

import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;
import blockchain.Bloque;
import blockchain.ProgramaPrincipal;
import blockchain.Transaccion;

import java.awt.event.ActionListener;
import java.security.PublicKey;
import java.awt.event.ActionEvent;
import javax.swing.JSpinner;

public class VentanaContracts extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5735550625691210170L;
	private JTextField textField;
	private float balance = VentanaLogin.getCarteraActual().getBalanceCartera();
	private static JLabel lblMonedas;
	private JSpinner spinner;
	private String PK_receptor = null;
	private int cantidad = 0;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			VentanaContracts dialog = new VentanaContracts();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Create the dialog.
	 */
	public VentanaContracts() {
		initialize();
	}

	private void initialize() {
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(VentanaContracts.class.getResource("/resources/ico32.png")));
		setSize(new Dimension(600, 160));
		setTitle("Crear smart contract");
		setResizable(false);
		// setBounds(100, 100, 800, 800);
		getContentPane().setLayout(new BorderLayout());

		// Colocar ventana en el centro
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = this.getSize();
		if (windowSize.height > screenSize.height) {
			windowSize.height = screenSize.height;

		}
		if (windowSize.width > screenSize.width) {
			windowSize.width = screenSize.width;
		}
		setLocation((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) / 2);
		{
			JPanel panelDatos = new JPanel();
			panelDatos.setBorder(
					new TitledBorder(null, "", TitledBorder.CENTER, TitledBorder.TOP, null, null));
			getContentPane().add(panelDatos, BorderLayout.WEST);
			GridBagLayout gbl_panelDatos = new GridBagLayout();
			gbl_panelDatos.columnWidths = new int[] { 101, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 116, 0, 56, 0 };
			gbl_panelDatos.rowHeights = new int[] { 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_panelDatos.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
			gbl_panelDatos.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
			panelDatos.setLayout(gbl_panelDatos);
			{
				JLabel lblMandar = new JLabel("Mandar");
				GridBagConstraints gbc_lblMandar = new GridBagConstraints();
				gbc_lblMandar.insets = new Insets(0, 0, 5, 5);
				gbc_lblMandar.gridx = 1;
				gbc_lblMandar.gridy = 3;
				panelDatos.add(lblMandar, gbc_lblMandar);
			}
			{
				spinner = new JSpinner();
				GridBagConstraints gbc_spinner = new GridBagConstraints();
				gbc_spinner.gridwidth = 3;
				gbc_spinner.insets = new Insets(0, 0, 5, 5);
				gbc_spinner.gridx = 2;
				gbc_spinner.gridy = 3;
				panelDatos.add(spinner, gbc_spinner);
			}
			{
				JLabel lblMonedasA = new JLabel("monedas a");
				GridBagConstraints gbc_lblMonedasA = new GridBagConstraints();
				gbc_lblMonedasA.insets = new Insets(0, 0, 5, 5);
				gbc_lblMonedasA.gridx = 5;
				gbc_lblMonedasA.gridy = 3;
				panelDatos.add(lblMonedasA, gbc_lblMonedasA);
			}
			{
				textField = new JTextField();
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.gridwidth = 7;
				gbc_textField.insets = new Insets(0, 0, 5, 5);
				gbc_textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_textField.gridx = 6;
				gbc_textField.gridy = 3;
				panelDatos.add(textField, gbc_textField);
				textField.setColumns(10);
			}
			{
				JLabel lblclavePblicaDel = new JLabel("(Clave p\u00FAblica del receptor)");
				GridBagConstraints gbc_lblclavePblicaDel = new GridBagConstraints();
				gbc_lblclavePblicaDel.insets = new Insets(0, 0, 5, 5);
				gbc_lblclavePblicaDel.gridx = 13;
				gbc_lblclavePblicaDel.gridy = 3;
				panelDatos.add(lblclavePblicaDel, gbc_lblclavePblicaDel);
			}
			{
				JLabel lblMiSaldo = new JLabel("Mi saldo: ");
				GridBagConstraints gbc_lblMiSaldo = new GridBagConstraints();
				gbc_lblMiSaldo.insets = new Insets(0, 0, 5, 5);
				gbc_lblMiSaldo.gridx = 5;
				gbc_lblMiSaldo.gridy = 6;
				panelDatos.add(lblMiSaldo, gbc_lblMiSaldo);
			}
			{
				lblMonedas = new JLabel((int) balance + " monedas");
				GridBagConstraints gbc_lblMonedas = new GridBagConstraints();
				gbc_lblMonedas.insets = new Insets(0, 0, 5, 5);
				gbc_lblMonedas.gridx = 6;
				gbc_lblMonedas.gridy = 6;
				panelDatos.add(lblMonedas, gbc_lblMonedas);
			}
		}
		{
			JPanel panelBotones = new JPanel();
			getContentPane().add(panelBotones, BorderLayout.SOUTH);
			{
				JButton btnCopiarMiClave = new JButton("Siguiente paso -> elegir fecha");
				panelBotones.add(btnCopiarMiClave);
				btnCopiarMiClave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int cantidad = (int) spinner.getValue();
						
						if(databaseControl.comprobarCartera((textField.getText().toString().trim()))) {
							if(textField.getText().toString().trim().equals(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica())))
								JOptionPane.showMessageDialog(null, "Usted no puede mandarse monedas a sí mismo.", "Error", JOptionPane.ERROR_MESSAGE);
							else if(cantidad < 1)
								JOptionPane.showMessageDialog(null, "La cantidad de monedas que desea mandar no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
							else { //TODO_BIEN, PASAR A ELEGIR FECHA
								PK_receptor = textField.getText().toString().trim();
								cantidad = (int) spinner.getValue();
								
								dispose();
								VentanaDatos.getVentanaFecha().setVisible(true);
							}
						}
						else { //Cartera no válida
							JOptionPane.showMessageDialog(null, "La cartera que ha introducido no existe.", "Error", JOptionPane.ERROR_MESSAGE);
						}
						
					   lblMonedas.setText((int) VentanaLogin.getCarteraActual().getBalanceCartera() + " monedas");
					}
				});
			}
			{
				JButton btnResetearCampos = new JButton("Resetear campos");
				panelBotones.add(btnResetearCampos);
				btnResetearCampos.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						spinner.setValue(0);
						textField.setText("");
					}
					});
			}
			{
				JButton btnCancelar = new JButton("Cancelar");
				panelBotones.add(btnCancelar);
				btnCancelar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {	
						VentanaDatos.getVentanaFecha().dispose();
						VentanaDatos.setVentanaFecha(new ElegirFecha());
						dispose();
						VentanaDatos.setVentanaContracts(new VentanaContracts());
					}
				});
			}

		}
	}
	
	public static void setLblMonedasText(String pTexto) {
		lblMonedas.setText(pTexto);
	}


	public String getPK_receptor() {
		return PK_receptor;
	}


	public int getCantidad() {
		return cantidad;
	}

}
