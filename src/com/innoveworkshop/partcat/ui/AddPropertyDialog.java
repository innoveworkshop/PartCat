package com.innoveworkshop.partcat.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;

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
	public static final int CANCEL_OPTION = 0;
	public static final int ADD_PROPERTY_OPTION = 1;
	
	private static final long serialVersionUID = 1100137279963592982L;
	private final JPanel contentPanel = new JPanel();
	private JComboBox<String> cmbName;
	private JTextField txtValue;

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
	 * Shows the dialog and returns a reference to what option the user choose.
	 * 
	 * @return The option the user selected before closing the dialog. This can be
	 *         {@link AddPropertyDialog#ADD_PROPERTY_OPTION} or
	 *         {@link AddPropertyDialog#CANCEL_OPTION}. Pretty self-explanatory.
	 * 
	 * @see {@link AddPropertyDialog#getName()}
	 * @see {@link AddPropertyDialog#getValue()}
	 */
	public int showDialog() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
		return CANCEL_OPTION;
	}
	
	/**
	 * Gets the name of the entered property.
	 */
	public String getName() {
		// TODO: Handle "dashes" and empty strings.
		return (String)cmbName.getEditor().getItem();
	}
	
	/**
	 * Gets the value of the property.
	 * 
	 * @return Property value.
	 */
	public String getValue() {
		return txtValue.getText().trim();
	}
	
	/**
	 * Populate the name {@link JComboBox} with common used items.
	 */
	private void populateNameComboBox() {
		ArrayList<String> commons = new ArrayList<String>();
		commons.add(PartCatConstants.PROPERTY_CATEGORY);
		commons.add(PartCatConstants.PROPERTY_SUBCATEGORY);
		commons.add(PartCatConstants.PROPERTY_PACKAGE);
		
		// Convert common properties array list into array for the model.
		Object[] commonsObjects = commons.toArray();
        String[] commonsStrings = Arrays.copyOf(commonsObjects, commonsObjects.length, 
                                   String[].class);
		
		// Set the model and select the first item.
		DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<String>(commonsStrings);
		cmbName.setModel(comboModel);
		cmbName.setSelectedIndex(0);
	}
	
	/**
	 * Initializes the UI components.
	 */
	private void initializeUI() {
		setBounds(100, 100, 360, 165);
		setTitle("Add Component Property");
		setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
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
