package vista;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.awt.event.ActionEvent;

public class VentanaCreditos extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8369194992833170444L;
	private final JPanelBackground panelCreditos = new JPanelBackground();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			VentanaCreditos dialog = new VentanaCreditos();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public VentanaCreditos() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(VentanaCreditos.class.getResource("/resources/ico32.png")));
		initialize();
	}

	private void initialize() {
		setTitle("Cr\u00E9ditos");
		setBounds(100, 100, 600, 500);
		getContentPane().setLayout(new BorderLayout());
		panelCreditos.setLayout(new FlowLayout());
		panelCreditos.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panelCreditos, BorderLayout.CENTER);
		panelCreditos.setBackground(VentanaCreditos.class.getResource("/resources/creditsEN.png"));

		// Centrar ventana
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
			JPanel panelBoton = new JPanel();
			panelBoton.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(panelBoton, BorderLayout.SOUTH);
			{
				JButton botonAceptar = new JButton("Back");
				botonAceptar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				{
					JButton btnMyLinkedin = new JButton("Mi Linkedin");
					btnMyLinkedin.setActionCommand("Open URL");
					panelBoton.add(btnMyLinkedin);
					btnMyLinkedin.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								Desktop.getDesktop().browse(new URL("https://www.linkedin.com/in/denis-stef/").toURI());
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (URISyntaxException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						}
					});
				}
				{
					JButton btnMyGithub = new JButton("Mi Github");
					btnMyGithub.setActionCommand("Open URL");
					panelBoton.add(btnMyGithub);
					btnMyGithub.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								Desktop.getDesktop().browse(new URL("https://github.com/denisro2010").toURI());
							} catch (MalformedURLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (URISyntaxException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						}
					});
				}
				botonAceptar.setActionCommand("Volver");
				panelBoton.add(botonAceptar);
				getRootPane().setDefaultButton(botonAceptar);
				botonAceptar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
			}
		}
	}

}
