package vista;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeArea;
import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;
import blockchain.Bloque;
import blockchain.ProgramaPrincipal;
import blockchain.SmartContract;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class ElegirFecha extends JFrame {

    /**
     * main, This is the entry point for the basic demo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	ElegirFecha ventana = new ElegirFecha();
                ventana.setVisible(true);
            }
        });
    }

    /**
     * Default Constructor.
     */
    public ElegirFecha() {
    	setIconImage(Toolkit.getDefaultToolkit().getImage(ElegirFecha.class.getResource("/resources/ico32.png")));
    	setResizable(false);
        initializeComponents();
    }

    /**
     * initializeComponents, This creates the user interface for the basic demo.
     */
    private void initializeComponents() {
    	DatePickerSettings dateSettings;
    	final LocalDate today = LocalDate.now(); 
    	
        // Set up the form which holds the date picker components. 
        setTitle("Elegir fecha y hora del contrato");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());
        setSize(new Dimension(500, 150));
        setLocationRelativeTo(null);

        // Create a date picker, and add it to the form.
        dateSettings = new DatePickerSettings();
        DatePicker datePicker1 = new DatePicker(dateSettings);
        dateSettings.setAllowEmptyDates(false);
        dateSettings.setAllowKeyboardEditing(false);
        dateSettings.setDateRangeLimits(today.plusDays(1), today.plusDays(1095));

        // Create a time picker with some custom settings.
        TimePickerSettings timeSettings = new TimePickerSettings();
        TimePicker timePicker2 = new TimePicker(timeSettings);
        timeSettings.setColor(TimeArea.TimePickerTextValidTime, Color.blue);
        timeSettings.setAllowEmptyTimes(false);
        timeSettings.setAllowKeyboardEditing(false);
        timeSettings.initialTime = LocalTime.of(00, 00);
        
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.add(datePicker1);
        datePicker1.setDate(today.plusDays(1));
        panel.add(timePicker2);
        
        JPanel panel_1 = new JPanel();
        getContentPane().add(panel_1);
        
        JButton btnNewButton = new JButton("Confirmar contrato");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		String PK_receptor = VentanaDatos.getVentanaContracts().getPK_receptor();
        		int cantidad =  VentanaDatos.getVentanaContracts().getCantidad();
        		String PK_remitente = StringUtils.getStringClave(VentanaLogin.getCarteraActual().getClavePublica());
        		
        		LocalDate d = datePicker1.getDate();
        		LocalTime t = timePicker2.getTime();
        		
        		Instant instant = d.atTime(t).atZone(ZoneId.systemDefault()).toInstant();
        		Date fecha = Date.from(instant);
        		long marcaTemp = fecha.getTime();
        		
        		SmartContract sc = new SmartContract(marcaTemp, cantidad, PK_remitente, PK_receptor);
        		sc.generarFirmaTransaccionContract(VentanaLogin.getCarteraActual().getClavePrivada(), PK_remitente, PK_receptor, cantidad); 
        		Bloque bl = null;
        		try {
					bl = new Bloque(databaseControl.getHashUltimoBloque());
	        		bl.setContratoConfirmado("false");
	        		bl.setContratoEjecutado("false");
	        		bl.anadirContrato(sc);
				} catch (Exception e1) {}
        		
        		if(ProgramaPrincipal.anadirBloque(bl)) {
            		ProgramaPrincipal.getContratos().add(sc);
	        		try {
						databaseControl.insertarBloque(bl);
						databaseControl.insertarContrato(sc.getID(), PK_receptor, cantidad, PK_remitente, marcaTemp, sc.getFirmaTransaccion());
					} catch (Exception e) {}
	        		JOptionPane.showMessageDialog(null, "El contrato se ha definido correctamente. Si el receptor lo acepta antes de la fecha, entonces se ejecutará el " + d + " a las " + t);
        		}
        		else {
        			ProgramaPrincipal.getBlockchain().remove(bl);
        			JOptionPane.showMessageDialog(null, "Ha habido un error al definir el contrato. Blockchain no válido.", "Error", JOptionPane.ERROR_MESSAGE);
        		}
        		
        		dispose();
        		VentanaDatos.setVentanaContracts(new VentanaContracts());
        		VentanaDatos vD = new VentanaDatos();
        		vD.setVisible(true);
        		VentanaDatos.setVentanaFecha(new ElegirFecha());
        	}
        });
        panel_1.add(btnNewButton);
        
        JButton btnNewButton_1 = new JButton("Cancelar contrato");
        btnNewButton_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		VentanaDatos.setVentanaContracts(new VentanaContracts());
        		dispose();
        		VentanaDatos.setVentanaFecha(new ElegirFecha());
        		VentanaDatos vD = new VentanaDatos();
        		vD.setVisible(true);
        	}
        });
        panel_1.add(btnNewButton_1);
        
        JLabel lblSiALa = new JLabel("Los fondos que defina en este contrato se reservarán para el mismo.");
        getContentPane().add(lblSiALa);
    }
}
