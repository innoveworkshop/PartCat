package com.innoveworkshop.partcat.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.innoveworkshop.partcat.PartCatConstants;

import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;

public class AddPropertyDialog extends JDialog {
	private static final long serialVersionUID = 1100137279963592982L;
	private final JPanel contentPanel = new JPanel();
	private JComboBox<String> cmbName;
	private JTextField txtValue;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddPropertyDialog dialog = new AddPropertyDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the component property add dialog.
	 * @wbp.parser.constructor
	 */
	public AddPropertyDialog() {
		// Initialize the UI components and populate the name combo with common stuff.
		initializeUI();
		populateNameComboBox();
	}
	
	/**
	 * Populate the name {@link JComboBox} with common used items.
	 */
	private void populateNameComboBox() {
		ArrayList<String> commons = new ArrayList<String>();
		commons.add(PartCatConstants.PROPERTY_CATEGORY);
		commons.add(PartCatConstants.PROPERTY_SUBCATEGORY);
		commons.add(PartCatConstants.PROPERTY_PACKAGE);
		
		// Set the model and select the first item.
		DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>((String[])commons.toArray());
		cmbName.setModel(comboModel);
		cmbName.setSelectedIndex(0);
	}
	
	/**
	 * Initializes the UI components.
	 */
	private void initializeUI() {
		setBounds(100, 100, 360, 165);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		JLabel lblPropertyName = new JLabel("Property Name");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblPropertyName, 5, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblPropertyName, 5, SpringLayout.WEST, contentPanel);
		contentPanel.add(lblPropertyName);
		
		cmbName = new JComboBox<String>();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, cmbName, 5, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblPropertyName, 0, SpringLayout.SOUTH, cmbName);
		sl_contentPanel.putConstraint(SpringLayout.WEST, cmbName, 5, SpringLayout.EAST, lblPropertyName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, cmbName, -5, SpringLayout.EAST, contentPanel);
		cmbName.setEditable(true);
		cmbName.setMaximumRowCount(20);
		contentPanel.add(cmbName);
		
		JLabel lblPropertyValue = new JLabel("Property Value");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblPropertyValue, 5, SpringLayout.SOUTH, lblPropertyName);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblPropertyValue, 0, SpringLayout.WEST, lblPropertyName);
		contentPanel.add(lblPropertyValue);
		
		txtValue = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, txtValue, 36, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblPropertyValue, 0, SpringLayout.SOUTH, txtValue);
		sl_contentPanel.putConstraint(SpringLayout.WEST, txtValue, 0, SpringLayout.WEST, cmbName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, txtValue, 0, SpringLayout.EAST, cmbName);
		contentPanel.add(txtValue);
		txtValue.setColumns(10);
		
		JCheckBox chkValueProperty = new JCheckBox("This Property is a Value");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, chkValueProperty, 5, SpringLayout.SOUTH, txtValue);
		sl_contentPanel.putConstraint(SpringLayout.WEST, chkValueProperty, 0, SpringLayout.WEST, lblPropertyName);
		chkValueProperty.setHorizontalAlignment(SwingConstants.CENTER);
		sl_contentPanel.putConstraint(SpringLayout.EAST, chkValueProperty, 0, SpringLayout.EAST, txtValue);
		contentPanel.add(chkValueProperty);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton addButton = new JButton("Add Propety");
				addButton.setActionCommand("OK");
				buttonPane.add(addButton);
				getRootPane().setDefaultButton(addButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
