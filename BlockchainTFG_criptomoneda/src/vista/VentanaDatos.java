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
import java.awt.HeadlessException;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.TitledBorder;

import bd.databaseControl;
import blockchain.StringUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.WindowConstants;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.JComboBox;

public class VentanaDatos extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5735550625691210170L;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			VentanaDatos dialog = new VentanaDatos();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Create the dialog.
	 */
	public VentanaDatos() {
		initialize();
	}

	private void initialize() {
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(VentanaDatos.class.getResource("/resources/ico32.png")));
		setSize(new Dimension(600, 160));
		setTitle("Mi cartera");
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
				JSpinner spinner = new JSpinner();
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
				JLabel lblMonedas = new JLabel("monedas");
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
				JButton btnCopiarMiClave = new JButton("Copiar mi clave p\u00FAblica");
				panelBotones.add(btnCopiarMiClave);
				btnCopiarMiClave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						StringSelection stringSelection = new StringSelection(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica()));
						clipboard.setContents(stringSelection, null);
					}
				});
			}
			{
				JButton btnBorrarCuenta = new JButton("Borrar cuenta");
				panelBotones.add(btnBorrarCuenta);
			}
			{
				JButton btnCancelar = new JButton("Salir (log out)");
				panelBotones.add(btnCancelar);
				btnCancelar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						/*VentanaPrincipalEN v = new VentanaPrincipalEN();
						v.setVisible(true);*/
						VentanaLogin.setCarteraActual(null);
						dispose();
					}
				});
			}

		}
	}
	

}
