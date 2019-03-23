package vista;

import javax.swing.JTable;

public class miTabla extends JTable{

	public boolean isCellEditable(int row, int column) {
        return false;
    }	
	
	public boolean isCellSelected(int row, int column) {
		if(column>0)
			return false;
		
		return super.isCellSelected(row, column);
		
	}
}
