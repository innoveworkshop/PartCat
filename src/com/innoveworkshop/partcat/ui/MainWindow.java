package com.innoveworkshop.partcat.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.partcat.components.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Our main window class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainWindow {
	private PartCatWorkspace workspace;
	private Component current_component;
	
	private JFrame frmPartcat;
	private JTree treeComponents;
	private JTextField txtSearch;
	private JTextField txtName;
	private JSpinner spnQuantity;
	private JTextArea txtNotes;
	private JTable tblProperties;
	private JButton btnDatasheet;
	private JButton btnModel;
	private JButton btnExtras;

	/**
	 * Creates the main frame.
	 * 
	 * @wbp.parser.constructor
	 */
	public MainWindow() {
		current_component = null;
		
		// Set the look and feel to be more native looking.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			try {
				System.err.println("Couldn't set the correct look and feel " +
						"for this platform. Switching to default");
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		// Initialize the UI controls.
		this.initializeUIControls();
		this.clearComponentView();
	}

	/**
	 * Creates the main frame with a {@link PartCatWorkspace} already set.
	 * 
	 * @param workspace Opened PartCat workspace.
	 */
	public MainWindow(PartCatWorkspace workspace) {
		this();
		this.setWorkspace(workspace);
	}
	
	/**
	 * Sets the components tree view iterator. This will repopulate the control.
	 * 
	 * @param iter Components list iterator.
	 * @see {@link Component.componentIterator()}
	 */
	public void setComponentsViewIterator(ListIterator<Component> iter) {
		// Create the tree root node.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Components");
		
		// Go through components adding them to the tree.
		while (iter.hasNext()) {
			root.add(new ComponentTreeNode(iter.next()));
		}
		
		// Set the tree model.
		treeComponents.setModel(new DefaultTreeModel(root));
	}
	
	/**
	 * Populates the view/edit area with data from a given component.
	 * 
	 * @param component Component to be shown/edited.
	 */
	protected void showComponent(Component component) {
		// Re-enable everything.
		txtName.setEnabled(true);
		spnQuantity.setEnabled(true);
		txtNotes.setEnabled(true);
		tblProperties.setEnabled(true);
		
		// Set text items.
		txtName.setText(component.getName());
		spnQuantity.setValue(Integer.valueOf(component.getQuantity()));
		if (component.hasNotes()) {
			txtNotes.setText(component.getNotes());
		} else {
			txtNotes.setText("");
		}
		
		// Enable the buttons we have files for.
		btnDatasheet.setEnabled(component.hasDatasheet());
		btnModel.setEnabled(component.hasSPICEModel());
		btnExtras.setEnabled(true);
		
		// Populate the table with data.
		this.setPropertiesTableContents(component.getProperties());
	}
	
	/**
	 * Clears the component view area.
	 */
	protected void clearComponentView() {
		// Text controls.
		txtName.setText("");
		txtName.setEnabled(false);
		spnQuantity.setValue(Integer.valueOf(0));
		spnQuantity.setEnabled(false);
		txtNotes.setText("");
		txtNotes.setEnabled(false);
		
		// Buttons.
		btnDatasheet.setEnabled(false);
		btnModel.setEnabled(false);
		btnExtras.setEnabled(false);
		
		// Table.
		this.clearPropertiesTable();
		tblProperties.setEnabled(false);
	}
	
	/**
	 * Clears the properties table.
	 */
	public void clearPropertiesTable() {
		((DefaultTableModel)tblProperties.getModel()).setRowCount(0);
	}
	
	/**
	 * Sets the contents of the properties table using a HashMap of Strings.
	 * 
	 * @param map HashMap with strings to populate the table with.
	 */
	protected void setPropertiesTableContents(HashMap<String, String> map) {
		// Get the table model and clear it.
		DefaultTableModel model = (DefaultTableModel)tblProperties.getModel();
		this.clearPropertiesTable();
		
		// Go through the HashMap.
		for (Map.Entry<String, String> entry : map.entrySet()) {
			model.addRow(new Object[] { entry.getKey(), entry.getValue() });
		}
	}
	
	/**
	 * Sets the current shown component in the view.
	 * 
	 * @param component Component to be shown/edited.
	 */
	public void setCurrentComponent(Component component) {
		current_component = component;
		this.showComponent(current_component);
	}
	
	/**
	 * Sets our current working workspace.
	 * 
	 * @param workspace Opened PartCat workspace.
	 */
	public void setWorkspace(PartCatWorkspace workspace) {
		this.workspace = workspace;
	}

	/**
	 * Shows the main window frame.
	 */
	public void show() {
		frmPartcat.setVisible(true);
	}

	/**
	 * Hides the main window frame.
	 */
	public void hide() {
		frmPartcat.setVisible(false);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeUIControls() {
		frmPartcat = new JFrame();
		frmPartcat.setTitle("PartCat");
		frmPartcat.setBounds(100, 100, 645, 384);
		frmPartcat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmPartcat.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmPartcat.dispatchEvent(new WindowEvent(frmPartcat, 
						WindowEvent.WINDOW_CLOSING));
			}
		});
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mnFile.add(mntmQuit);
		SpringLayout springLayout = new SpringLayout();
		frmPartcat.getContentPane().setLayout(springLayout);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);
		springLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.NORTH, frmPartcat.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, frmPartcat.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, frmPartcat.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, frmPartcat.getContentPane());
		frmPartcat.getContentPane().add(splitPane);
		
		JPanel leftPanel = new JPanel();
		splitPane.setLeftComponent(leftPanel);
		SpringLayout sl_leftPanel = new SpringLayout();
		leftPanel.setLayout(sl_leftPanel);
		
		JScrollPane sclTree = new JScrollPane();
		sl_leftPanel.putConstraint(SpringLayout.NORTH, sclTree, 5, SpringLayout.NORTH, leftPanel);
		sl_leftPanel.putConstraint(SpringLayout.WEST, sclTree, 5, SpringLayout.WEST, leftPanel);
		sl_leftPanel.putConstraint(SpringLayout.EAST, sclTree, -5, SpringLayout.EAST, leftPanel);
		leftPanel.add(sclTree);
		
		treeComponents = new JTree();
		treeComponents.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				componentTreeValueChanged(event);
			}
		});
		sl_leftPanel.putConstraint(SpringLayout.NORTH, treeComponents, 5, SpringLayout.NORTH, leftPanel);
		sl_leftPanel.putConstraint(SpringLayout.WEST, treeComponents, 5, SpringLayout.WEST, leftPanel);
		sl_leftPanel.putConstraint(SpringLayout.EAST, treeComponents, -5, SpringLayout.EAST, leftPanel);
		sclTree.setViewportView(treeComponents);
		
		txtSearch = new JTextField();
		sl_leftPanel.putConstraint(SpringLayout.WEST, txtSearch, 0, SpringLayout.WEST, sclTree);
		leftPanel.add(txtSearch);
		txtSearch.setToolTipText("Search");
		txtSearch.setColumns(10);
		
		JButton btnSearch = new JButton("Search");
		sl_leftPanel.putConstraint(SpringLayout.NORTH, txtSearch, 0, SpringLayout.NORTH, btnSearch);
		sl_leftPanel.putConstraint(SpringLayout.SOUTH, txtSearch, 0, SpringLayout.SOUTH, btnSearch);
		sl_leftPanel.putConstraint(SpringLayout.EAST, txtSearch, -5, SpringLayout.WEST, btnSearch);
		sl_leftPanel.putConstraint(SpringLayout.SOUTH, sclTree, -5, SpringLayout.NORTH, btnSearch);
		sl_leftPanel.putConstraint(SpringLayout.SOUTH, btnSearch, -5, SpringLayout.SOUTH, leftPanel);
		sl_leftPanel.putConstraint(SpringLayout.EAST, btnSearch, 0, SpringLayout.EAST, sclTree);
		leftPanel.add(btnSearch);
		
		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		SpringLayout sl_rightPanel = new SpringLayout();
		rightPanel.setLayout(sl_rightPanel);
		
		JLabel lblImage = new JLabel("Image");
		lblImage.setBackground(Color.GRAY);
		sl_rightPanel.putConstraint(SpringLayout.NORTH, lblImage, 5, SpringLayout.NORTH, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.WEST, lblImage, 5, SpringLayout.WEST, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, lblImage, 120, SpringLayout.NORTH, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.EAST, lblImage, 120, SpringLayout.WEST, rightPanel);
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		rightPanel.add(lblImage);
		
		JLabel lblName = new JLabel("Name");
		sl_rightPanel.putConstraint(SpringLayout.NORTH, lblName, 5, SpringLayout.NORTH, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.WEST, lblName, 5, SpringLayout.EAST, lblImage);
		rightPanel.add(lblName);
		
		txtName = new JTextField();
		sl_rightPanel.putConstraint(SpringLayout.NORTH, txtName, 5, SpringLayout.NORTH, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.WEST, txtName, 8, SpringLayout.EAST, lblName);
		sl_rightPanel.putConstraint(SpringLayout.EAST, txtName, -5, SpringLayout.EAST, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, lblName, 0, SpringLayout.SOUTH, txtName);
		txtName.setEnabled(false);
		rightPanel.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblQuantity = new JLabel("Quantity");
		sl_rightPanel.putConstraint(SpringLayout.NORTH, lblQuantity, 4, SpringLayout.SOUTH, lblName);
		sl_rightPanel.putConstraint(SpringLayout.WEST, lblQuantity, 0, SpringLayout.WEST, lblName);
		rightPanel.add(lblQuantity);
		
		spnQuantity = new JSpinner();
		spnQuantity.setModel(new SpinnerNumberModel(Integer.valueOf(0), null, null, Integer.valueOf(1)));
		sl_rightPanel.putConstraint(SpringLayout.NORTH, spnQuantity, 28, SpringLayout.NORTH, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.WEST, spnQuantity, 8, SpringLayout.EAST, lblQuantity);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, lblQuantity, 0, SpringLayout.SOUTH, spnQuantity);
		sl_rightPanel.putConstraint(SpringLayout.EAST, spnQuantity, -5, SpringLayout.EAST, rightPanel);
		rightPanel.add(spnQuantity);
		
		JPanel extrasPanel = new JPanel();
		sl_rightPanel.putConstraint(SpringLayout.WEST, extrasPanel, 0, SpringLayout.EAST, lblImage);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, extrasPanel, 0, SpringLayout.SOUTH, lblImage);
		sl_rightPanel.putConstraint(SpringLayout.EAST, extrasPanel, 0, SpringLayout.EAST, txtName);
		rightPanel.add(extrasPanel);
		
		tblProperties = new JTable();
		extrasPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		
		btnDatasheet = new JButton("Datasheet");
		extrasPanel.add(btnDatasheet);
		
		btnModel = new JButton("Model");
		extrasPanel.add(btnModel);
		
		btnExtras = new JButton("Extras");
		extrasPanel.add(btnExtras);
		
		JScrollPane sclNotes = new JScrollPane();
		sl_rightPanel.putConstraint(SpringLayout.NORTH, sclNotes, 4, SpringLayout.SOUTH, lblQuantity);
		sl_rightPanel.putConstraint(SpringLayout.WEST, sclNotes, 0, SpringLayout.WEST, lblName);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, sclNotes, -4, SpringLayout.NORTH, extrasPanel);
		sl_rightPanel.putConstraint(SpringLayout.EAST, sclNotes, 0, SpringLayout.EAST, txtName);
		rightPanel.add(sclNotes);
		sclNotes.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sl_rightPanel.putConstraint(SpringLayout.NORTH, tblProperties, 5, SpringLayout.SOUTH, lblImage);
		sl_rightPanel.putConstraint(SpringLayout.WEST, tblProperties, 0, SpringLayout.WEST, lblImage);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, tblProperties, -5, SpringLayout.SOUTH, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.EAST, tblProperties, 0, SpringLayout.EAST, txtName);
		
		txtNotes = new JTextArea();
		txtNotes.setText("Notes");
		txtNotes.setToolTipText("Notes");
		txtNotes.setWrapStyleWord(true);
		txtNotes.setTabSize(4);
		txtNotes.setLineWrap(true);
		sl_rightPanel.putConstraint(SpringLayout.NORTH, txtNotes, 4, SpringLayout.SOUTH, lblQuantity);
		sl_rightPanel.putConstraint(SpringLayout.WEST, txtNotes, 0, SpringLayout.WEST, lblName);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, txtNotes, -4, SpringLayout.NORTH, extrasPanel);
		sl_rightPanel.putConstraint(SpringLayout.EAST, txtNotes, 0, SpringLayout.EAST, txtName);
		sclNotes.setViewportView(txtNotes);
		
		JScrollPane sclTable = new JScrollPane();
		sl_rightPanel.putConstraint(SpringLayout.NORTH, sclTable, 8, SpringLayout.SOUTH, lblImage);
		sl_rightPanel.putConstraint(SpringLayout.WEST, sclTable, 0, SpringLayout.WEST, lblImage);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, sclTable, -5, SpringLayout.SOUTH, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.EAST, sclTable, 0, SpringLayout.EAST, txtName);
		rightPanel.add(sclTable);
		
		DefaultTableModel table_model = new DefaultTableModel(new Object[][] {},
				new String[] { "Property", "Value" });
		
		tblProperties.setCellSelectionEnabled(true);
		tblProperties.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblProperties.setModel(table_model);
		tblProperties.setAutoCreateRowSorter(true);
		sclTable.setViewportView(tblProperties);
	}
	
	/**
	 * Event fired whenever the selection of the component tree view changes.
	 * 
	 * @param event Tree selection event.
	 */
	public void componentTreeValueChanged(TreeSelectionEvent event) {
		Object node = event.getPath().getLastPathComponent();
		
		if (node instanceof ComponentTreeNode) {
			// Node is a component.
			this.setCurrentComponent(((ComponentTreeNode)node).getComponent());
		} else {
			this.clearComponentView();
		}
	}
}