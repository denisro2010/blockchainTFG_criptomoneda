package vista;
 
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
 
/**
 * 
 * Clase que extiende de JPanel y permite poner una imagen como fondo.
 * 
 */
 
public class JPanelBackground extends JPanel {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -6119013375811262251L;
	// Atributo que guardara la imagen de Background que le pasemos.
	private Image background;
 
	// Metodo que es llamado automaticamente por la maquina virtual Java cada vez que repinta
	public void paintComponent(Graphics g) {
 
		/* Obtenemos el tamaño del panel para hacer que se ajuste a este
		cada vez que redimensionemos la ventana y se lo pasamos al drawImage */
		int width = this.getSize().width;
		int height = this.getSize().height;
 
		// Mandamos que pinte la imagen en el panel
		if (this.background != null) {
			g.drawImage(this.background, 0, 0, width, height, null);
		}
 
		super.paintComponent(g);
	}
 
	// Metodo donde le pasaremos la dirección de la imagen a cargar, no funciona con path relativo al exportar.
	/*
	 * public void setBackground(String imagePath) {
		
		// Construimos la imagen y se la asignamos al atributo background.
		this.setOpaque(false);
		this.background = new ImageIcon(imagePath).getImage();
		repaint();
	}
	 */

	// Metodo donde le pasaremos la dirección de la imagen a cargar. FUNCIONA
	 public void setBackground(URL pResource) {
		// TODO Auto-generated method stub
		this.setOpaque(false);
		this.background = new ImageIcon(pResource).getImage();
		repaint();
		
	}
	
 
}
