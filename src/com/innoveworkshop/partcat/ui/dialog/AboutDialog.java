package com.innoveworkshop.partcat.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * A simple about dialog.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 8079171795639372073L;

	/**
	 * Creates the about dialog.
	 */
	public AboutDialog() {
		this(null);
	}
	
	/**
	 * Creates the about dialog.
	 * 
	 * @param parent Parent window to center it on.
	 */
	public AboutDialog(JFrame parent) {
		initializeUI();
		
		if (parent != null)
			this.setLocationRelativeTo(parent);
	}

	/**
	 * Opens URLs using the default browser.
	 * 
	 * @param url URL to open as a {@link String}.
	 */
	public void openURL(String url) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Initializes the UI elements.
	 */
	public void initializeUI() {
		setBounds(100, 100, 345, 220);
		
		JLabel lblIcon = new JLabel("");
		lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
		lblIcon.setIcon(new ImageIcon(AboutDialog.class.getResource("/com/innoveworkshop/partcat/resources/Icon-96.png")));
		getContentPane().add(lblIcon, BorderLayout.NORTH);
		
		JPanel panText = new JPanel();
		getContentPane().add(panText, BorderLayout.CENTER);
		panText.setLayout(new GridLayout(4, 1, 0, 0));
		
		JLabel lblPartcat = new JLabel("PartCat");
		lblPartcat.setHorizontalAlignment(SwingConstants.CENTER);
		lblPartcat.setFont(new Font("Dialog", Font.BOLD, 15));
		panText.add(lblPartcat);
		
		JLabel lblInnoveWorkshopCompany = new JLabel("Innove Workshop Company");
		lblInnoveWorkshopCompany.setHorizontalAlignment(SwingConstants.CENTER);
		lblInnoveWorkshopCompany.setFont(new Font("Dialog", Font.PLAIN, 12));
		panText.add(lblInnoveWorkshopCompany);
		
		JLabel lblBlank = new JLabel("");
		panText.add(lblBlank);
		
		JLabel lblDevelopedByNathan = new JLabel("Developed by Nathan Campos");
		lblDevelopedByNathan.setHorizontalAlignment(SwingConstants.CENTER);
		lblDevelopedByNathan.setFont(new Font("Dialog", Font.PLAIN, 12));
		panText.add(lblDevelopedByNathan);
		
		JPanel panButtons = new JPanel();
		getContentPane().add(panButtons, BorderLayout.SOUTH);
		panButtons.setLayout(new GridLayout(0, 3, 0, 0));
		
		JButton btnWebsite = new JButton("Website");
		btnWebsite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openURL("https://github.com/innoveworkshop/PartCat");
			}
		});
		panButtons.add(btnWebsite);
		
		JLabel lblBlank2 = new JLabel("");
		panButtons.add(lblBlank2);
		
		JButton btnAuthor = new JButton("Author");
		btnAuthor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openURL("https://nathancampos.me/");
			}
		});
		panButtons.add(btnAuthor);
	}
}
