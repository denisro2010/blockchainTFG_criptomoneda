package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
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

public class VentanaLogin extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5735550625691210170L;
	//protected static User usuario;
	private JPasswordField passwordField;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			VentanaLogin dialog = new VentanaLogin();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public JPasswordField getPasswordField() {
		return passwordField;
	}
	
	

	public JTextField getTextField() {
		return textField;
	}


	public void setTextField(JTextField textField) {
		this.textField = textField;
	}


	/**
	 * Create the dialog.
	 */
	public VentanaLogin() {
		initialize();
	}

	private void initialize() {
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(VentanaLogin.class.getResource("/resources/ico32.png")));
		setSize(new Dimension(530, 160));
		setTitle("Acceder a mi cartera");
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
			getContentPane().add(panelDatos, BorderLayout.CENTER);
			GridBagLayout gbl_panelDatos = new GridBagLayout();
			gbl_panelDatos.columnWidths = new int[] { 101, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 116, 56, 0 };
			gbl_panelDatos.rowHeights = new int[] { 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_panelDatos.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
			gbl_panelDatos.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
			panelDatos.setLayout(gbl_panelDatos);
			{
				JLabel lblYourName = new JLabel("Usuario");
				lblYourName.setForeground(Color.BLACK);
				GridBagConstraints gbc_lblYourName = new GridBagConstraints();
				gbc_lblYourName.anchor = GridBagConstraints.EAST;
				gbc_lblYourName.insets = new Insets(0, 0, 5, 5);
				gbc_lblYourName.gridx = 1;
				gbc_lblYourName.gridy = 1;
				panelDatos.add(lblYourName, gbc_lblYourName);
			}
			{
				textField = new JTextField();
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.gridwidth = 10;
				gbc_textField.insets = new Insets(0, 0, 5, 5);
				gbc_textField.fill = GridBagConstraints.HORIZONTAL;
				gbc_textField.gridx = 6;
				gbc_textField.gridy = 1;
				panelDatos.add(textField, gbc_textField);
				textField.setColumns(10);
			}
			{
				JLabel lblMasterPassword = new JLabel("Contraseña");
				lblMasterPassword.setForeground(Color.BLACK);
				GridBagConstraints gbc_lblMasterPassword = new GridBagConstraints();
				gbc_lblMasterPassword.anchor = GridBagConstraints.EAST;
				gbc_lblMasterPassword.insets = new Insets(0, 0, 5, 5);
				gbc_lblMasterPassword.gridx = 1;
				gbc_lblMasterPassword.gridy = 2;
				panelDatos.add(lblMasterPassword, gbc_lblMasterPassword);
			}
			{
				passwordField = new JPasswordField();
				passwordField.setToolTipText("");
				GridBagConstraints gbc_passwordField = new GridBagConstraints();
				gbc_passwordField.gridwidth = 8;
				gbc_passwordField.insets = new Insets(0, 0, 5, 5);
				gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
				gbc_passwordField.gridx = 8;
				gbc_passwordField.gridy = 2;
				panelDatos.add(passwordField, gbc_passwordField);
			}
		}
		{
			JPanel panelBotones = new JPanel();
			getContentPane().add(panelBotones, BorderLayout.SOUTH);
			{
				JButton btnAceptar = new JButton("Acceder");
				panelBotones.add(btnAceptar);
				btnAceptar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String passBD = null;
						String usuario = textField.getText().toString().trim();
						String contra = StringUtils.applySha256(passwordField.getPassword().toString());
						
						try {
							passBD = databaseControl.getPass(usuario);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
						if(passBD.equals(contra)) {
							/*VentanaCartera v = new VentanaCartera();
							v.setVisible(true);*/ //TOOOOOOOOOOOOOOOOOOOOOOOOOOODOOOOOOOOOOOOOOOO
						}
						else
							JOptionPane.showMessageDialog(null,
								    "La contraseña que ha introducido no es correcta.",
								    "Error",
								    JOptionPane.ERROR_MESSAGE);
					}

				});
			}
			{
				JButton btnCancelar = new JButton("Cancelar");
				panelBotones.add(btnCancelar);
				btnCancelar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						/*VentanaPrincipalEN v = new VentanaPrincipalEN();
						v.setVisible(true);*/
						dispose();
					}
				});
			}

		}
	}
	

}
