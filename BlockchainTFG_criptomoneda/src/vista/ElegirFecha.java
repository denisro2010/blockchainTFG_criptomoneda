package vista;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeArea;

import algoritmosCriptograficos.StringUtils;
import bd.databaseControl;
import blockchain.ProgramaPrincipal;
import blockchain.SmartContract;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

/**
 * BasicDemo,
 *
 * This class demonstrates the most basic usage of the date and time picker components. More
 * specifically, this class only demonstrates how to create the components and add them to a form.
 * For a more extensive demonstration of all library components and their various optional settings,
 * see "FullDemo.java".
 */
public class ElegirFecha extends JFrame {

    /**
     * main, This is the entry point for the basic demo.
     */
    public static void main(String[] args) {
        // Use the standard swing code to start this demo inside a swing thread.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create an instance of the demo.
            	ElegirFecha basicDemo = new ElegirFecha();
                // Make the demo visible on the screen.
                basicDemo.setVisible(true);
            }
        });
    }

    /**
     * Default Constructor.
     */
    public ElegirFecha() {
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new FlowLayout());
        setSize(new Dimension(400, 120));
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
        		ProgramaPrincipal.getContratos().add(sc);
        		databaseControl.crearContrato(sc.getID(), PK_receptor, cantidad, PK_remitente, marcaTemp);
        	}
        });
        panel_1.add(btnNewButton);
        
        JButton btnNewButton_1 = new JButton("Cancelar contrato");
        btnNewButton_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		VentanaDatos.setVentanaContracts(new VentanaContracts());
        		dispose();
        		VentanaDatos.setVentanaFecha(new ElegirFecha());
        	}
        });
        panel_1.add(btnNewButton_1);
    }
}
