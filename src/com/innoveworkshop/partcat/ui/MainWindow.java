package com.innoveworkshop.partcat.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.partcat.components.Component;
import com.innoveworkshop.partcat.components.ComponentProperties;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;

/**
 * Our main window class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainWindow {
	private boolean unsavedChanges;
	
	public MainWindowActions action;
	public PartCatWorkspace workspace;
	public Component current_component;
	public PropertiesTableModelListener tblModelListener;
	
	public JFrame frmPartcat;
	public JTree treeComponents;
	public JTextField txtSearch;
	public JLabel lblImage;
	public JTextField txtName;
	public JSpinner spnQuantity;
	public JTextArea txtNotes;
	public JTable tblProperties;
	public JButton btnDatasheet;
	public JButton btnModel;
	public JButton btnExtras;

	/**
	 * Creates the main frame.
	 * 
	 * @wbp.parser.constructor
	 */
	public MainWindow() {
		this.current_component = null;
		this.action = new MainWindowActions(this);
		
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
		initializeUIControls();
		clearComponentView();
		setUnsavedChanges(false);
	}

	/**
	 * Creates the main frame with a {@link PartCatWorkspace} already set.
	 * 
	 * @param workspace Opened PartCat workspace.
	 */
	public MainWindow(PartCatWorkspace workspace) {
		this();
		setWorkspace(workspace);
	}
	
	/**
	 * Populates the tree view using a components list iterator.
	 * 
	 * @param iter Components list iterator.
	 * @see {@link Component.componentIterator()}
	 */
	public void populateComponentsTree(ListIterator<Component> iter) {
		// Create the tree root node.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Components");
		
		// Go through components adding them to the tree.
		while (iter.hasNext()) {
			Component comp = iter.next();
			
			// Check if the component has been deleted before adding it.
			if (!comp.isDeleted())
				root.add(new ComponentTreeNode(comp));
		}
		
		// Set the tree model.
		treeComponents.setModel(new DefaultTreeModel(root));
	}
	
	/**
	 * Syncs the component changes in the UI to the {@link Component} object.
	 */
	public void syncComponentChanges() {
		current_component.setQuantity((Integer)spnQuantity.getValue());
		current_component.setNotes(txtNotes.getText());
		
		// Properties.
		DefaultTableModel model = (DefaultTableModel)tblProperties.getModel();
		ComponentProperties prop = current_component.getProperties();
		prop.clear();
		for (int row = 0; row < model.getRowCount(); row++) {
			prop.put((String)model.getValueAt(row, 0), (String)model.getValueAt(row, 1));
		}
	}
	
	/**
	 * Populates the view/edit area with data from a given {@link Component}.
	 * 
	 * @param component Component to be shown/edited.
	 */
	protected void showComponent(Component component) {
		// Re-enable everything.
		txtName.setEnabled(false);
		spnQuantity.setEnabled(true);
		txtNotes.setEnabled(true);
		tblProperties.setEnabled(true);
		
		// Set image.
		setComponentImageLabel(component);
		
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
		setPropertiesTableContents(component.getProperties());
		
		// Clear dirtiness.
		setUnsavedChanges(false);
	}
	
	/**
	 * Clears the component view area.
	 */
	protected void clearComponentView() {
		// Image controls.
		setComponentImageLabel(null);
		
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
		clearPropertiesTable();
		tblProperties.setEnabled(false);
		
		// Component.
		current_component = null;
		setUnsavedChanges(false);
	}
	
	/**
	 * Clears the properties table.
	 */
	public void clearPropertiesTable() {
		((DefaultTableModel)tblProperties.getModel()).setRowCount(0);
	}
	
	/**
	 * Sets the contents of the properties table using a {@link HashMap} of
	 * {@link String}s.
	 * 
	 * @param map HashMap with strings to populate the table with.
	 */
	protected void setPropertiesTableContents(HashMap<String, String> map) {
		// Get the table model and clear it.
		DefaultTableModel model = (DefaultTableModel)tblProperties.getModel();
		model.removeTableModelListener(tblModelListener);
		this.clearPropertiesTable();
		
		// Go through the HashMap.
		for (Map.Entry<String, String> entry : map.entrySet()) {
			model.addRow(new Object[] { entry.getKey(), entry.getValue() });
		}
		
		// Add event listener.
		tblModelListener = new PropertiesTableModelListener();
		model.addTableModelListener(tblModelListener);
	}
	
	/**
	 * Clears all the content from the component tree and its view.
	 */
	public void clearComponentTreeAndView() {
		treeComponents.setModel(null);
		clearComponentView();
	}
	
	/**
	 * Sets the current shown component in the view.
	 * 
	 * @param component {@link Component} to be shown/edited.
	 */
	public void setCurrentComponent(Component component) {
		current_component = component;
		
		showComponent(current_component);
		setUnsavedChanges(false);
	}
	
	/**
	 * Sets the component image label accordingly.
	 * 
	 * @param component {@link Component} to be used to set the image from. This
	 *                  can be null if you don't have any.
	 */
	public void setComponentImageLabel(Component component) {
		if (component == null) {
			lblImage.setText("No Image");
			lblImage.setIcon(null);
			return;
		}
		
		if (component.getImage().getIcon() == null) {
			lblImage.setText("No Image");
			lblImage.setIcon(null);
		} else {
			lblImage.setText("");
			lblImage.setIcon(component.getImage().getIcon(lblImage.getSize(), true));
		}
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
	 * Takes care of showing a dialog for unsaved changes and what should be
	 * done with the user decision.
	 * 
	 * @return True if the operation should be aborted because the user wants to
	 *         save stuff first.
	 */
	public boolean handleUnsavedChanges() {
		if (!hasUnsavedChanges())
			return false;
		
		int option = JOptionPane.showConfirmDialog(frmPartcat,
				"You have unsaved changes. Do you wish to continue and DISCARD them?",
				"Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
		
		return option != JOptionPane.YES_OPTION;
	}
	
	/**
	 * Checks if we have unsaved changes pending saving.
	 * 
	 * @return True if we do have unsaved changes to commit.
	 */
	public boolean hasUnsavedChanges() {
		return this.unsavedChanges;
	}
	
	/**
	 * Sets the unsaved changes flag.
	 * 
	 * @param dirty Do we have unsaved changes?
	 */
	public void setUnsavedChanges(boolean dirty) {
		this.unsavedChanges = dirty;
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
	@SuppressWarnings("deprecation")
	private void initializeUIControls() {
		frmPartcat = new JFrame();
		frmPartcat.setTitle("PartCat");
		frmPartcat.setBounds(100, 100, 645, 384);
		frmPartcat.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmPartcat.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				if (handleUnsavedChanges())
					return;
				
				action.closeWorkspace();
				frmPartcat.dispose();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		frmPartcat.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (handleUnsavedChanges())
					return;
				
				action.closeWindow();
			}
		});
		
		JMenuItem mntmNewComponent = new JMenuItem("New Component");
		mntmNewComponent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (handleUnsavedChanges())
					return;
				
				action.newComponent();
			}
		});
		mntmNewComponent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mnFile.add(mntmNewComponent);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action.saveComponent(current_component);
			}
		});
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As...");
		mntmSaveAs.setEnabled(false);
		mnFile.add(mntmSaveAs);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmOpenWorkspace = new JMenuItem("Open Workspace...");
		mntmOpenWorkspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (handleUnsavedChanges())
					return;
				
				action.openWorkspace();
			}
		});
		mntmOpenWorkspace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		mnFile.add(mntmOpenWorkspace);
		
		JMenuItem mntmRefreshWorkspace = new JMenuItem("Refresh Workspace");
		mntmRefreshWorkspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (handleUnsavedChanges())
					return;
				
				action.refreshWorkspace();
			}
		});
		mntmRefreshWorkspace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
		mnFile.add(mntmRefreshWorkspace);
		
		JMenuItem mntmCloseWorkspace = new JMenuItem("Close Workspace");
		mntmCloseWorkspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (handleUnsavedChanges())
					return;
				
				action.closeWorkspace();
			}
		});
		mntmCloseWorkspace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
		mnFile.add(mntmCloseWorkspace);
		
		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mnFile.add(mntmQuit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About...");
		mntmAbout.setEnabled(false);
		mnHelp.add(mntmAbout);
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
		treeComponents.addMouseListener(new ComponentMousePopupListener(treeComponents));
		treeComponents.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				if (handleUnsavedChanges())
					return;
				
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
		
		lblImage = new JLabel("Image");
		lblImage.addMouseListener(new ImageMousePopupListener(lblImage));
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
		txtName.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setUnsavedChanges(true);
			}

			public void insertUpdate(DocumentEvent e) {
				setUnsavedChanges(true);
			}

			public void removeUpdate(DocumentEvent e) {
				setUnsavedChanges(true);
			}
		});
		
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
		spnQuantity.getEditor().getComponent(0);
		JFormattedTextField qntField = (JFormattedTextField)spnQuantity.getEditor().getComponent(0);
		DefaultFormatter qntFormatter = (DefaultFormatter)qntField.getFormatter();
	    qntFormatter.setCommitsOnValidEdit(true);
		spnQuantity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setUnsavedChanges(true);
			}
		});
		
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
		txtNotes.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				setUnsavedChanges(true);
			}

			public void insertUpdate(DocumentEvent e) {
				setUnsavedChanges(true);
			}

			public void removeUpdate(DocumentEvent e) {
				setUnsavedChanges(true);
			}
		});
		
		JScrollPane sclTable = new JScrollPane();
		sl_rightPanel.putConstraint(SpringLayout.NORTH, sclTable, 8, SpringLayout.SOUTH, lblImage);
		sl_rightPanel.putConstraint(SpringLayout.WEST, sclTable, 0, SpringLayout.WEST, lblImage);
		sl_rightPanel.putConstraint(SpringLayout.SOUTH, sclTable, -5, SpringLayout.SOUTH, rightPanel);
		sl_rightPanel.putConstraint(SpringLayout.EAST, sclTable, 0, SpringLayout.EAST, txtName);
		rightPanel.add(sclTable);
		
		DefaultTableModel table_model = new DefaultTableModel(new Object[][] {},
				new String[] { "Property", "Value" });
		sclTable.addMouseListener(new PropertiesMousePopupListener(tblProperties, false));
		tblProperties.addMouseListener(new PropertiesMousePopupListener(tblProperties, true));
		
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
		
		// TODO: Go back to the last selection if the user decides not to discard changes in the unsaved dialog.
		
		if (node instanceof ComponentTreeNode) {
			// Node is a component.
			setCurrentComponent(((ComponentTreeNode)node).getComponent());
		} else {
			clearComponentView();
		}
	}
	
	/**
	 * A mouse adapter class to handle the properties table popup menu.
	 */
	class PropertiesMousePopupListener extends MouseAdapter {
		private int row;
		private boolean isOutsideTable;
		public JTable tblTable;
		public JPopupMenu popupMenu;
		
		/**
		 * Creates the popup menu for the properties table.
		 * 
		 * @param table      Properties table.
		 * @param showDelete Show the delete menu item?
		 */
		public PropertiesMousePopupListener(JTable table, boolean showDelete) {
			JMenuItem menuItem;
			
			// Set the current state of things.
			row = -1;
			tblTable = table;
			isOutsideTable = !showDelete;
			popupMenu = new JPopupMenu();
			
			// Add property item.
			menuItem = new JMenuItem("Add");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addTableRow();
				}
			});
			popupMenu.add(menuItem);
			
			// Remove property item.
			if (showDelete) {
				menuItem = new JMenuItem("Remove");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int option = JOptionPane.showConfirmDialog(frmPartcat,
								"Are you sure you want to delete this row?",
								"Delete Parameter", JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
						
						if (option == JOptionPane.YES_OPTION)
							removeTableRow();
					}
				});
				popupMenu.add(menuItem);
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && (current_component != null)) {
				row = tblTable.rowAtPoint(e.getPoint());
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && (current_component != null)) {
				row = tblTable.rowAtPoint(e.getPoint());
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if ((e.getClickCount() == 2) && isOutsideTable
					&& (current_component != null)) {
				addTableRow();
			}
		}
		
		/**
		 * Adds a new blank row to the table.
		 */
		public void addTableRow() {
			DefaultTableModel model = (DefaultTableModel)tblTable.getModel();
			model.addRow(new Object[] { "", "" });
			setUnsavedChanges(true);
		}
		
		/**
		 * Removes a specific row from the table.
		 */
		public void removeTableRow() {
			DefaultTableModel model = (DefaultTableModel)tblTable.getModel();
			model.removeRow(row);
			setUnsavedChanges(true);
		}
	}
	
	/**
	 * A mouse adapter class to handle the component list popup menu.
	 */
	class ComponentMousePopupListener extends MouseAdapter {
		public JTree treeView;
		public JPopupMenu popupMenu;
		public JMenuItem mitmDelete;
		public Component selComponent;
		
		/**
		 * Creates the popup menu for the component list.
		 * 
		 * @param treeView Components tree view.
		 */
		public ComponentMousePopupListener(JTree treeView) {
			// Set the current state of things.
			this.treeView = treeView;
			popupMenu = new JPopupMenu();
			selComponent = null;
			
			// New component item.
			JMenuItem menuItem = new JMenuItem("New");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (handleUnsavedChanges())
						return;
					
					action.newComponent();
				}
			});
			popupMenu.add(menuItem);
			
			// Delete component item.
			mitmDelete = new JMenuItem("Delete");
			mitmDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (handleUnsavedChanges())
						return;
					
					int option = JOptionPane.showConfirmDialog(frmPartcat,
							"Are you sure you want to delete " + selComponent.getName() + "?",
							"Delete Component", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					
					if (option == JOptionPane.YES_OPTION) {
						try {
							selComponent.delete();
						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frmPartcat,
									"Something went wrong while trying to delete " + selComponent.getName(),
									"Deletion Error", JOptionPane.ERROR_MESSAGE);
						}
						
						// Clear everything and reload the tree view.
						clearComponentTreeAndView();
						populateComponentsTree(workspace.componentIterator());
					}
				}
			});
			popupMenu.add(mitmDelete);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && workspace.isOpen()) {
				enableDeleteMenu(getComponentFromPosition(e.getX(), e.getY()));
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && workspace.isOpen()) {
				enableDeleteMenu(getComponentFromPosition(e.getX(), e.getY()));
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		/**
		 * Retrieves the component object from a tree view by using the mouse
		 * position.
		 * 
		 * @param  x Mouse X position.
		 * @param  y Mouse Y position.
		 * @return   True if we are hovering a component.
		 */
		public boolean getComponentFromPosition(int x, int y) {
			TreePath treePath = treeView.getPathForLocation(x, y);
			if (treePath == null)
				return false;
			
			Object node = treePath.getLastPathComponent();
			if (node instanceof ComponentTreeNode) {
				selComponent = ((ComponentTreeNode)node).getComponent();
				return true;
			}
			
			selComponent = null;
			return false;
		}
		
		/**
		 * Manages if we should enable or disable the delete item.
		 * 
		 * @param enable Should we enable the menu item?
		 */
		private void enableDeleteMenu(boolean enable) {
			mitmDelete.setEnabled(enable);
		}
	}
	
	/**
	 * A mouse adapter class to handle the image popup menu.
	 */
	class ImageMousePopupListener extends MouseAdapter {
		public JLabel label;
		public JPopupMenu popupMenu;
		
		/**
		 * Creates the popup menu for the component image label.
		 * 
		 * @param label Component image {@link JLabel}.
		 */
		public ImageMousePopupListener(JLabel label) {
			JMenuItem menuItem;
			
			// Set the current state of things.
			this.label = label;
			popupMenu = new JPopupMenu();
			
			// Add image item.
			menuItem = new JMenuItem("Add");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					action.selectComponentImage(current_component);
				}
			});
			popupMenu.add(menuItem);
			
			// Remove image item.
			menuItem = new JMenuItem("Remove");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					current_component.removeImage();
					setComponentImageLabel(current_component);
					setUnsavedChanges(true);
				}
			});
			popupMenu.add(menuItem);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger() && (current_component != null)) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger() && (current_component != null)) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if ((e.getClickCount() == 2) && (current_component != null)) {
				action.selectComponentImage(current_component);
			}
		}
	}
	
	/**
	 * A simple class to handle all the properties table change events.
	 */
	class PropertiesTableModelListener implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			// Get the changed row contents.
			DefaultTableModel model = (DefaultTableModel)e.getSource();
			
			if (model.getRowCount() > 0) {
				String row_key = (String)model.getValueAt(e.getFirstRow(), 0);
			
				if (row_key.equals("Package")) {
					// We are dealing with a package change.
					syncComponentChanges();
					current_component.reloadImage();
					setComponentImageLabel(current_component);
				}
			}
		}
	}
}
