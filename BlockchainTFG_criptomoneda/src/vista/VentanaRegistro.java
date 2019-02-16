package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.TitledBorder;
import java.security.Security;
import bd.databaseControl;
import blockchain.Cartera;
import blockchain.StringUtils;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Color;

public class VentanaRegistro extends JDialog {

	private static final long serialVersionUID = 5735550625691210170L;
	private static JTextField txtNombre;
	private static JPasswordField passwordField;
	private ArrayList<Cartera> carteras = new ArrayList<Cartera>();

	public static void main(String[] args) {
		try {
			VentanaRegistro dialog = new VentanaRegistro();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static JTextField getTxtNombre() {
		return txtNombre;
	}

	public JPasswordField getPasswordField() {
		return passwordField;
	}

	/**
	 * Create the dialog.
	 */
	public VentanaRegistro() {
		initialize();
	}

	private void initialize() {
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(VentanaRegistro.class.getResource("/resources/ico32.png")));
		//setSize(new Dimension(530, 210));
		setSize(new Dimension(500, 160));
		setTitle("Crear cartera");
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
			gbl_panelDatos.columnWidths = new int[] { 101, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 116, 56, 0 };
			gbl_panelDatos.rowHeights = new int[] { 22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			gbl_panelDatos.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
			gbl_panelDatos.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
			panelDatos.setLayout(gbl_panelDatos);

			{
				JLabel lblNombre = new JLabel("Nobre de usuario:");
				lblNombre.setForeground(Color.BLACK);
				GridBagConstraints gbc_lblNombre = new GridBagConstraints();
				gbc_lblNombre.anchor = GridBagConstraints.EAST;
				gbc_lblNombre.insets = new Insets(0, 0, 5, 5);
				gbc_lblNombre.gridx = 1;
				gbc_lblNombre.gridy = 1;
				panelDatos.add(lblNombre, gbc_lblNombre);
			}
			{
				txtNombre = new JTextField();
				txtNombre.setToolTipText("Los espacios en blanco delante o detrás de tu nombre de usuario serán borrados.");
				txtNombre.setForeground(Color.BLACK);
				GridBagConstraints gbc_txtNombre = new GridBagConstraints();
				gbc_txtNombre.gridwidth = 13;
				gbc_txtNombre.insets = new Insets(0, 0, 5, 5);
				gbc_txtNombre.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtNombre.gridx = 5;
				gbc_txtNombre.gridy = 1;
				panelDatos.add(txtNombre, gbc_txtNombre);
				txtNombre.setColumns(10);
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
				GridBagConstraints gbc_passwordField = new GridBagConstraints();
				gbc_passwordField.gridwidth = 13;
				gbc_passwordField.insets = new Insets(0, 0, 5, 5);
				gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
				gbc_passwordField.gridx = 5;
				gbc_passwordField.gridy = 2;
				panelDatos.add(passwordField, gbc_passwordField);
			}
			{
			}
		}
		{
			JPanel panelBotones = new JPanel();
			getContentPane().add(panelBotones, BorderLayout.SOUTH);
			{
				JButton btnAceptar = new JButton("Crear cartera");
				panelBotones.add(btnAceptar);
				btnAceptar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Cartera c = new Cartera();
						
						if(!(txtNombre.getText().toString().trim().equals("")) && !(passwordField.getText().toString().trim().equals(""))) {
						try {
							String u = databaseControl.getUsuario(txtNombre.getText().toString().trim());
							if(u == null) {
								databaseControl.crearCartera(txtNombre.getText().toString().trim(), passwordField.getText().toString(), c.clavePublica.toString(), c.clavePrivada.toString());
								carteras.add(c);
								JOptionPane.showMessageDialog(null, "Su cartera se ha creado correctamente.");
								dispose();
							}
							else
								JOptionPane.showMessageDialog(null, "Este usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
							
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						
						}
						else 
							JOptionPane.showMessageDialog(null, "El nombre de usuario o la contraseña no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
					}});
			}

			{
				JButton btnBorrar = new JButton("Resetear campos");
				btnBorrar.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						gettxtNombre().setText("");
						gettxtNombre().requestFocus();
						getPasswordField().setText("");
					}

					private JTextField gettxtNombre() {
						// TODO Auto-generated method stub
						return txtNombre;
					}
				});
				panelBotones.add(btnBorrar);
			}
			{
				JButton btnCancelar = new JButton("Cancelar");
				panelBotones.add(btnCancelar);
				btnCancelar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
			}

		}
	}

	public static void setTxtNombre(JTextField txtNombre) {
		VentanaRegistro.txtNombre = txtNombre;
	}

	public static JTextField gettxtNombre() {
		return txtNombre;
	}
	
	public ArrayList<Cartera> getCarteras() {
		return carteras;
	}

}
