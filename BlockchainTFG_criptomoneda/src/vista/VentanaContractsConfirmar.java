package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;

public class VentanaContractsConfirmar extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static VentanaContractsConfirmar frame;
	private static JPanel jp = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new VentanaContractsConfirmar();
					frame.setVisible(true);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaContractsConfirmar() {
		ArrayList<String> contratosSinConfirmar = databaseControl.contratosPendientes(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica()));
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 300, contratosSinConfirmar.size()*20);
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
		
		List<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
		
		if(contratosSinConfirmar.size() > 0) {
			String remitente;
			Date fecha;
			int cantidad;
			
			for(int i=0; i < contratosSinConfirmar.size(); i++) {
				if((i % 5 == 0) || (i == 0)) {
					fecha = new Date(Long.parseLong(contratosSinConfirmar.get(i+3)));
					String sdf = new SimpleDateFormat("dd-MM-yyyy").format(fecha);
					remitente = contratosSinConfirmar.get(i+1);
					cantidad = Integer.parseInt(contratosSinConfirmar.get(i+4));
					String datos = cantidad + " monedas de " + databaseControl.getNombreUsuario(remitente) + " en la fecha: " + sdf;
					
				    JCheckBox box = new JCheckBox(datos);
				    checkboxes.add(box);
				    //jp.add(box);
				}
			}
		}
		for(int j=0; j<checkboxes.size(); j++) {
			jp.add(checkboxes.get(j));
		}
		contentPane.add(jp, BorderLayout.CENTER);
	}

}
