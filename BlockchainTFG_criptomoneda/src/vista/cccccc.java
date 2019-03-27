package vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;

public class cccccc extends JFrame {

	private ArrayList<String> contratosSinConfirmar = new ArrayList<String>();
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					cccccc frame = new cccccc();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public cccccc() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		List<JCheckBox> checkboxes = new ArrayList<>();
		//contratosSinConfirmar = databaseControl.contratosPendientes(databaseControl.getNombreUsuario(StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica())));
		contratosSinConfirmar.add("a");
		contratosSinConfirmar.add("b");
		contratosSinConfirmar.add("c");
		
		for(String element: contratosSinConfirmar) {
		    JCheckBox box = new JCheckBox(element);
		    checkboxes.add(box);
		    contentPane.add(box);
		}
	}

}
