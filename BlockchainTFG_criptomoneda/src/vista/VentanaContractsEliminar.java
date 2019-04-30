package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;
import blockchain.Bloque;
import blockchain.ProgramaPrincipal;
import blockchain.SmartContract;

import javax.swing.JButton;

public class VentanaContractsEliminar extends JFrame {

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
					VentanaContractsEliminar frame = new VentanaContractsEliminar();
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
	public VentanaContractsEliminar() {
		setTitle("ELIMINAR contrato(s)");
		setIconImage(Toolkit.getDefaultToolkit().getImage(ElegirFecha.class.getResource("/resources/ico32.png")));
		initialize();
		if(checkboxes.size() == 0) {
			dispose();
		}
	}

	private void initialize() {
		ArrayList<String> contratos1 = databaseControl
				.contratosPendientesEliminarReceptor(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica()));
		ArrayList<String> contratos2 = databaseControl
				.contratosPendientesEliminarRemitente(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica()));
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 550, (contratos1.size() + contratos2.size()) * 50);
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
		if (contratos1.size() > 0) { //cuando el receptor quiere cancelarlo
			String receptor; // el que lo quiere cancelar
			String remitente; //el usuario logueado
			Date fecha;
			int cantidad;

			for (int i = 0; i < contratos1.size(); i++) {
				if ((i % 5 == 0) || (i == 0)) {
					if(databaseControl.getContrato(contratos1.get(i)).esContratoValido()) { //Si no ha sido manipulado
						fecha = new Date(Long.parseLong(contratos1.get(i + 3)));
						String sdf = new SimpleDateFormat("dd-MM-yyyy").format(fecha);
						receptor = contratos1.get(i + 2);
						remitente = contratos1.get(i+1);
						cantidad = Integer.parseInt(contratos1.get(i + 4));
						String datos = databaseControl.getNombreUsuario(receptor) + " desea cancelar el contrato: " + cantidad + " monedas de " + 
						databaseControl.getNombreUsuario(remitente) + " a " 
						+ databaseControl.getNombreUsuario(receptor) + " en la fecha: " + sdf;
	
						JCheckBox box = new JCheckBox(datos);
						checkboxes.add(box);
						idContracts.add(contratos1.get(i)); // Guardar el id del contrato
					}
				}
			}
		}
		
		if (contratos2.size() > 0) { //cuando el remitente quiere cancelarlo
			String receptor; // el logueado
			String remitente; //el que lo quiere cancelar
			Date fecha;
			int cantidad;

			for (int i = 0; i < contratos2.size(); i++) {
				if ((i % 5 == 0) || (i == 0)) {
					if(databaseControl.getContrato(contratos2.get(i)).esContratoValido()) { //Si no ha sido manipulado
						fecha = new Date(Long.parseLong(contratos2.get(i + 3)));
						String sdf = new SimpleDateFormat("dd-MM-yyyy").format(fecha);
						receptor = contratos2.get(i + 2);
						remitente = contratos2.get(i+1);
						cantidad = Integer.parseInt(contratos2.get(i + 4));
						String datos = databaseControl.getNombreUsuario(remitente) + " desea cancelar el contrato: " + cantidad + " monedas de " + databaseControl.getNombreUsuario(remitente) + " a " 
						+ databaseControl.getNombreUsuario(receptor) + " en la fecha: " + sdf;
	
						JCheckBox box = new JCheckBox(datos);
						checkboxes.add(box);
						idContracts.add(contratos2.get(i)); // Guardar el id del contrato
					}
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
			btnConfirmar = new JButton("Confirmar cancelación");
		}
		btnConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int contSelected = 0;
				if (checkboxes.size() > 0) {
					for (int i = 0; i < checkboxes.size(); i++) {
						if (checkboxes.get(i).isSelected()) {
							contSelected = contSelected + 1;
							SmartContract sc = databaseControl.getContrato(idContracts.get(i));				
							Bloque bl = null;
							try {
								bl = new Bloque(databaseControl.getHashUltimoBloque());
								bl.setContratoConfirmado("");
								bl.setContratoEjecutado("");
								bl.setContratoPorEliminar("true");
								bl.anadirContrato(sc);
								if(ProgramaPrincipal.anadirBloque(bl)) {
									databaseControl.insertarBloque(bl);
								}
							} catch (Exception e1) {}
							/*try {
								databaseControl.borrarContrato(idContracts.get(i).toString());
							} catch (Exception e1) {
							}*/
							checkboxes.remove(i);
							idContracts.remove(i);
						}
					}
					if(checkboxes.size() > 0 && contSelected > 0) {
						VentanaContractsEliminar v = new VentanaContractsEliminar();
						v.setVisible(true);
					}
					if(contSelected > 0) {
						dispose();
						VentanaDatos vd = new VentanaDatos();
						vd.setVisible(true);
					}
				}
			}
		});
		return btnConfirmar;
	}

	private JButton getBtnRechazar() {
		if (btnRechazar == null) {
			btnRechazar = new JButton("Rechazar cancelación");
		}
		btnRechazar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int contSelected = 0;
				if (checkboxes.size() > 0) {
					for (int i = 0; i < checkboxes.size(); i++) {
						if (checkboxes.get(i).isSelected()) {
							contSelected = contSelected + 1;
							SmartContract sc = databaseControl.getContrato(idContracts.get(i));
							boolean soyReceptor = sc.getPK_receptor().equals(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica()));
							Bloque bl = null;
							try {
								bl = new Bloque(databaseControl.getHashUltimoBloque());
								bl.setContratoConfirmado("");
								bl.setContratoEjecutado("");
								if(soyReceptor)
									bl.setContratoPorEliminar("Receptor.false");
								else
									bl.setContratoPorEliminar("Remitente.false");
								bl.anadirContrato(sc);
								if(ProgramaPrincipal.anadirBloque(bl)) {
									databaseControl.insertarBloque(bl);
								}
							} catch (Exception e1) {}
							checkboxes.remove(i);
							idContracts.remove(i);
						}
					}
					if(checkboxes.size() > 0 && contSelected > 0) {
						VentanaContractsEliminar v = new VentanaContractsEliminar();
						v.setVisible(true);
					}
					if(contSelected > 0) {
						VentanaDatos vd = new VentanaDatos();
						vd.setVisible(true);
						dispose();
					}
				}
			}
		});
		return btnRechazar;
	}
}
