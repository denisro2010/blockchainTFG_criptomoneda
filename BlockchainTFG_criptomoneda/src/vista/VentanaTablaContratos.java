package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;
import blockchain.Bloque;
import blockchain.ProgramaPrincipal;
import blockchain.SmartContract;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JTable;
import java.awt.Font;
import javax.swing.ListSelectionModel;

public class VentanaTablaContratos extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5735550625691210170L;
	private static JTable table;
	private static DefaultTableModel dataModel;
	private ArrayList<String> listaContratos = databaseControl.rellenarTablaRemitente();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			VentanaTablaContratos dialog = new VentanaTablaContratos();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static JTable getTable() {
		return table;
	}
	
	/**
	 * Create the dialog.
	 */
	public VentanaTablaContratos() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		initialize();
	}
	
	private void initialize() {
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(VentanaTablaContratos.class.getResource("/resources/ico32.png")));
		setSize(new Dimension(800, 800));
		setTitle("Mis contratos: "); //TODO
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
			panelDatos.setBorder(new TitledBorder(null, "", TitledBorder.CENTER, TitledBorder.TOP, null, null));
			getContentPane().add(panelDatos, BorderLayout.CENTER);
			panelDatos.setLayout(null);
			{
			    table = new miTabla();
				table.setSurrendersFocusOnKeystroke(true);
				table.setForeground(Color.WHITE);
				table.setBackground(Color.DARK_GRAY);
				table.setCellSelectionEnabled(true);
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.setFont(new Font("Tahoma", Font.PLAIN, 13));
				table.setBounds(48, 58, 736, 669);
				panelDatos.add(table);
				String[] dat = {"", "", "", "", ""};
				dataModel = new DefaultTableModel(dat, 0);
				
				for (int i = 0; i < listaContratos.size(); i++){
					if(i == 0 || (i % 6 == 0)) {
						if( listaContratos.get(i+1).toLowerCase().equals(databaseControl.getNombreUsuario(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica())))
								|| listaContratos.get(i+2).toLowerCase().equals(databaseControl.getNombreUsuario
										(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica()))) ) {
							String obj1 = listaContratos.get(i);
							String obj2 = listaContratos.get(i+1);
							String obj3 = listaContratos.get(i+2);
							String obj4 = listaContratos.get(i+3);
							String obj5 = listaContratos.get(i+4);
							String[] datos = {obj1, obj2, obj3, obj4, obj5};
							dataModel.addRow(datos);
						}
					}
				}
				table.setModel(dataModel);
			}
			
			JLabel lblNewLabel = new JLabel("Receptor");
			lblNewLabel.setBackground(Color.WHITE);
			lblNewLabel.setBounds(194, 33, 66, 14);
			panelDatos.add(lblNewLabel);
			
			JLabel lblPassword = new JLabel("Remitente");
			lblPassword.setBackground(Color.WHITE);
			lblPassword.setBounds(341, 33, 71, 14);
			panelDatos.add(lblPassword);
			
			JLabel lblFechaDeEjecucin = new JLabel("Fecha de ejecuci\u00F3n");
			lblFechaDeEjecucin.setBounds(489, 33, 126, 14);
			panelDatos.add(lblFechaDeEjecucin);
			
			JLabel lblCantidad = new JLabel("Cantidad");
			lblCantidad.setBounds(636, 33, 66, 14);
			panelDatos.add(lblCantidad);
			
			JLabel lblSeleccionarContrato = new JLabel("Seleccionar contrato");
			lblSeleccionarContrato.setBounds(48, 33, 136, 14);
			panelDatos.add(lblSeleccionarContrato);
		}
		{
			JPanel panelBotones = new JPanel();
			getContentPane().add(panelBotones, BorderLayout.SOUTH);
			
			JButton btnNewButton = new JButton("Cancelar contrato seleccionado");
			panelBotones.add(btnNewButton);
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					int fila = table.getSelectedRow();
					
					if(fila == -1)
						JOptionPane.showMessageDialog(null, "Primero tiene que seleccionar un contrato.", "Error", JOptionPane.ERROR_MESSAGE);
					else {
						//if(listaContratos.get( (6*fila)+2 ).toString().toLowerCase().equals //Si el usuario logueado es el remitente entonces puede borrar el contrato
								//(databaseControl.getNombreUsuario(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica())).toLowerCase())){
							
							int opcion = JOptionPane.showConfirmDialog(null, "¿Seguro que quiere mandarle al otro usuario la petición de cancelar el contrato?", "Aviso", JOptionPane.YES_NO_OPTION);
							
							SmartContract sc = databaseControl.getContrato(listaContratos.get((fila*6)+5));
							boolean soyReceptor = sc.getPK_receptor().equals(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica()));
							
							if (opcion == 0) {
								if(sc.esContratoValido() && ((soyReceptor && !databaseControl.contratoCancelarTrueReceptor(sc.getIDsmartContract())) 
										|| (!soyReceptor && !databaseControl.contratoCancelarTrueRemitente(sc.getIDsmartContract())))) { //Si el otro no ha rechazado la peticion
									Bloque bl = null;
									try {
										bl = new Bloque(databaseControl.getHashUltimoBloque());
										bl.setContratoConfirmado("");
										bl.setContratoEjecutado("false");
										if(soyReceptor) //para saber quien quiere cancelarlo
											bl.setContratoPorEliminar("Receptor.true");
										else
											bl.setContratoPorEliminar("Remitente.true");
										bl.anadirContrato(sc);
										if(ProgramaPrincipal.anadirBloque(bl)) {
											databaseControl.insertarBloque(bl);
										}
									} catch (Exception e1) {
										e1.printStackTrace();
									}
									JOptionPane.showMessageDialog(null, "La petición se ha realizado correctamente. Si el otro usuario involucrado la acepta, el contrato se cancelará.");
								}
								else if(((soyReceptor && databaseControl.contratoCancelarTrueReceptor(sc.getIDsmartContract())) 
												|| (!soyReceptor && databaseControl.contratoCancelarTrueRemitente(sc.getIDsmartContract())))){
									JOptionPane.showMessageDialog(null, "El otro usuario ya ha rechazado la petición de cancelación.", "Error", JOptionPane.ERROR_MESSAGE);
								}
								else if(!sc.esContratoValido()) {
									JOptionPane.showMessageDialog(null, "El contrato que desea cancelar ha sido manipulado.", "Error", JOptionPane.ERROR_MESSAGE);
								}
								
								/*
								//Borrar de la bd
								try {
									databaseControl.borrarContrato(listaContratos.get((fila*6)+5));
								} catch (Exception e1) {}
								//borrar del programa
								ProgramaPrincipal.borrarContrato(listaContratos.get((fila*6)+5));
								for(int i=5+(fila*6); i<(fila*6); i--) {
									listaContratos.remove(i);
								}
								JOptionPane.showMessageDialog(null, "El contrato se ha borrado correctamente.");
								dispose();
								VentanaTablaContratos vent = new VentanaTablaContratos();
								listaContratos = databaseControl.rellenarTablaRemitente();
								vent.setVisible(true);
								*/							
							}
						//}
							/*else {
								JOptionPane.showMessageDialog(null, "No puede borrar un contrato que no es suyo. Póngase en contacto con el remitente.", "Error", JOptionPane.ERROR_MESSAGE);
							}*/
					}
				} //fin action performed
			});
			{
				JButton btnCancelar = new JButton("Volver");
				panelBotones.add(btnCancelar);
				btnCancelar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						VentanaDatos vD = new VentanaDatos();
						vD.setVisible(true);
						dispose();
					}
				});
			}
	}

		
	}
}
