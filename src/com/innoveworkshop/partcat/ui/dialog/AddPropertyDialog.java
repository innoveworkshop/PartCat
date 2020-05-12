package com.innoveworkshop.partcat.ui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.innoveworkshop.partcat.PartCatConstants;

public class AddPropertyDialog extends JDialog {
	private static final long serialVersionUID = 1100137279963592982L;
	public static final int CANCEL_OPTION = 0;
	public static final int ADD_PROPERTY_OPTION = 1;
	
	private boolean isPropertyValue;
	private int selectedOption;
	
	private final JPanel contentPanel = new JPanel();
	private JComboBox<String> cmbName;
	private JTextField txtValue;
	private JCheckBox chkValueProperty;

	/**
	 * Creates the component property add dialog.
	 * @wbp.parser.constructor
	 */
	public AddPropertyDialog() {
		// Set the initial state of things.
		isPropertyValue = false;
		selectedOption = CANCEL_OPTION;
		
		// Initialize the UI components and populate the name combo with common stuff.
		initializeUI();
		populateNameComboBox();
	}
	
	/**
	 * Shows the dialog and returns a reference to what option the user choose.
	 * 
	 * @param  parent Parent frame. Used to properly place the dialog on
	 *                screen. This value can be {@code null}.
	 * @return        The option the user selected before closing the dialog.
	 *                This can be {@link AddPropertyDialog#ADD_PROPERTY_OPTION}
	 *                or {@link AddPropertyDialog#CANCEL_OPTION}. Pretty
	 *                self-explanatory.
	 * 
	 * @see {@link AddPropertyDialog#getName()}
	 * @see {@link AddPropertyDialog#getValue()}
	 */
	public int showDialog(JFrame parent) {
		if (parent != null)
			this.setLocationRelativeTo(parent);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
		return selectedOption;
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
		return showDialog(null);
	}
	
	/**
	 * Gets the name of the entered property.
	 */
	public String getName() {
		String name = (String)cmbName.getEditor().getItem();
		
		// Make the name string properly "property".
		name = name.trim().replace(' ', '-');
		if (isPropertyValue)
			name = PartCatConstants.PROPERTY_VALUE + "-" + name;
		
		return name;
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
	 * Is this property of type value?
	 * 
	 * @return {@code True} if the property is of the value type.
	 */
	public boolean isPropertyValue() {
		return isPropertyValue;
	}
	
	/**
	 * Closes the dialog window.
	 */
	public void closeDialog() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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
		setBounds(100, 100, 360, 175);
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
		
		chkValueProperty = new JCheckBox("This Property is a Value");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, chkValueProperty, 5, SpringLayout.SOUTH, txtValue);
		sl_contentPanel.putConstraint(SpringLayout.WEST, chkValueProperty, 0, SpringLayout.WEST, lblPropertyName);
		chkValueProperty.setHorizontalAlignment(SwingConstants.CENTER);
		sl_contentPanel.putConstraint(SpringLayout.EAST, chkValueProperty, 0, SpringLayout.EAST, txtValue);
		chkValueProperty.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isPropertyValue = chkValueProperty.isSelected();
			}
		});
		contentPanel.add(chkValueProperty);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton addButton = new JButton("Add Propety");
				addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedOption = ADD_PROPERTY_OPTION;
						closeDialog();
					}
				});
				addButton.setActionCommand("OK");
				buttonPane.add(addButton);
				getRootPane().setDefaultButton(addButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						selectedOption = CANCEL_OPTION;
						closeDialog();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
