package vista;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeArea;
import com.github.lgooddatepicker.zinternaltools.InternalUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
        setLayout(new FlowLayout());
        setSize(new Dimension(640, 150));
        setLocationRelativeTo(null);

        // Create a date picker, and add it to the form.
        dateSettings = new DatePickerSettings();
        DatePicker datePicker1 = new DatePicker(dateSettings);
        datePicker1.setDate(today.plusDays(1));
        dateSettings.setAllowEmptyDates(false);
        dateSettings.setAllowKeyboardEditing(false);
        dateSettings.setDateRangeLimits(today.plusDays(1), today.plusDays(1095));
        add(datePicker1);

        // Create a time picker with some custom settings.
        TimePickerSettings timeSettings = new TimePickerSettings();
        timeSettings.setColor(TimeArea.TimePickerTextValidTime, Color.blue);
        timeSettings.setAllowEmptyTimes(false);
        timeSettings.setAllowKeyboardEditing(false);
        timeSettings.initialTime = LocalTime.now();
        TimePicker timePicker2 = new TimePicker(timeSettings);
        add(timePicker2);
    }
}
