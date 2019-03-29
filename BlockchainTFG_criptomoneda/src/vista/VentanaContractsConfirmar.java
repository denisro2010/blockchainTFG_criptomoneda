package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;
import blockchain.Bloque;
import blockchain.ProgramaPrincipal;

import javax.swing.JButton;

public class VentanaContractsConfirmar extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panelBotones;
	private JButton btnConfirmar;
	private JButton btnRechazar;
	private List<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
	private List<String> idContracts = new ArrayList<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaContractsConfirmar frame = new VentanaContractsConfirmar();
					frame.setVisible(true);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaContractsConfirmar() {
		setTitle("Contratos pendientes");
		setIconImage(Toolkit.getDefaultToolkit().getImage(ElegirFecha.class.getResource("/resources/ico32.png")));
		initialize();
		if(checkboxes.size() == 0) {
			dispose();
		}
	}

	private void initialize() {
		ArrayList<String> contratosSinConfirmar = databaseControl
				.contratosPendientes(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica()));
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 300, contratosSinConfirmar.size() * 50);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

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

		JPanel jp = new JPanel();
		if (contratosSinConfirmar.size() > 0) {
			String remitente;
			Date fecha;
			int cantidad;

			for (int i = 0; i < contratosSinConfirmar.size(); i++) {
				if ((i % 5 == 0) || (i == 0)) {
					fecha = new Date(Long.parseLong(contratosSinConfirmar.get(i + 3)));
					String sdf = new SimpleDateFormat("dd-MM-yyyy").format(fecha);
					remitente = contratosSinConfirmar.get(i + 1);
					cantidad = Integer.parseInt(contratosSinConfirmar.get(i + 4));
					String datos = cantidad + " monedas de " + databaseControl.getNombreUsuario(remitente)
							+ " en la fecha: " + sdf;

					JCheckBox box = new JCheckBox(datos);
					checkboxes.add(box);
					idContracts.add(contratosSinConfirmar.get(i)); // Guardar el id del contrato
				}
			}
		}
		for (int j = 0; j < checkboxes.size(); j++) {
			jp.add(checkboxes.get(j));
		}
		contentPane.add(jp, BorderLayout.CENTER);
		contentPane.add(getPanelBotones(), BorderLayout.SOUTH);
	}

	private JPanel getPanelBotones() {
		if (panelBotones == null) {
			panelBotones = new JPanel();
			panelBotones.add(getBtnConfirmar());
			panelBotones.add(getBtnRechazar());
		}
		return panelBotones;
	}

	private JButton getBtnConfirmar() {
		if (btnConfirmar == null) {
			btnConfirmar = new JButton("Confirmar");
		}
		btnConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int contSelected = 0;
				if (checkboxes.size() > 0) {
					for (int i = 0; i < checkboxes.size(); i++) {
						if (checkboxes.get(i).isSelected()) {
							contSelected = contSelected + 1;
							Bloque bl = null;
							try {
								bl = new Bloque(databaseControl.getHashUltimoBloque());
								bl.setContratoConfirmado("true");
								bl.setContratoEjecutado("false");
								bl.anadirContrato(databaseControl.getContrato(idContracts.get(i)));
								if(ProgramaPrincipal.anadirBloque(bl)) {
									databaseControl.insertarBloque(bl);
								}
							} catch (Exception e1) {}
							checkboxes.remove(i);
							idContracts.remove(i);
						}
					}
					if(checkboxes.size() > 0 && contSelected > 0) {
						VentanaContractsConfirmar v = new VentanaContractsConfirmar();
						v.setVisible(true);
					}
					if(contSelected > 0)
						dispose();
				}
			}
		});
		return btnConfirmar;
	}

	private JButton getBtnRechazar() {
		if (btnRechazar == null) {
			btnRechazar = new JButton("Rechazar");
		}
		btnRechazar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int contSelected = 0;
				if (checkboxes.size() > 0) {
					for (int i = 0; i < checkboxes.size(); i++) {
						if (checkboxes.get(i).isSelected()) {
							contSelected = contSelected + 1;
							try {
								databaseControl.borrarContrato(idContracts.get(i).toString());
							} catch (Exception e1) {
							}
							checkboxes.remove(i);
							idContracts.remove(i);
						}
					}
					if(checkboxes.size() > 0 && contSelected > 0) {
						VentanaContractsConfirmar v = new VentanaContractsConfirmar();
						v.setVisible(true);
					}
					if(contSelected > 0)
						dispose();
				}
			}
		});
		return btnRechazar;
	}
}
