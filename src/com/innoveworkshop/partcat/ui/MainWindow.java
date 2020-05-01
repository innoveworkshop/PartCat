package com.innoveworkshop.partcat.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.innoveworkshop.partcat.PartCatConstants;
import com.innoveworkshop.partcat.PartCatWorkspace;
import com.innoveworkshop.partcat.components.Component;
import com.innoveworkshop.partcat.components.ComponentProperties;
import com.innoveworkshop.partcat.resources.ApplicationResources;
import com.innoveworkshop.partcat.ui.menu.ComponentMousePopupListener;
import com.innoveworkshop.partcat.ui.menu.DatasheetPopupListener;
import com.innoveworkshop.partcat.ui.menu.ImageMousePopupListener;
import com.innoveworkshop.partcat.ui.menu.PropertiesMousePopupListener;

/**
 * Our main window class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainWindow {
	private ApplicationResources res;
	private Preferences prefs;
	private boolean unsavedChanges;
	private boolean ignoreUnsaved;
	
	public MainWindowActions action;
	public PartCatWorkspace workspace;
	public Component currentComponent;
	public PropertiesTableModelListener tblModelListener;
	

	public AboutDialog dlgAbout;
	public JFrame frmPartcat;
	public JTree treeComponents;
	public JTextField txtFilter;
	public JLabel lblImage;
	public JTextField txtName;
	public JSpinner spnQuantity;
	public JTextArea txtNotes;
	public JTable tblProperties;
	public JButton btnDatasheet;
	public JButton btnExtras;

	/**
	 * Creates the main frame.
	 * 
	 * @wbp.parser.constructor
	 */
	public MainWindow() {
		this.ignoreUnsaved = false;
		this.currentComponent = null;
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
		clearComponentTreeAndView();
		setUnsavedChanges(false);
	}
	
	/**
	 * Creates the main window frame with {@link Preferences}.
	 * 
	 * @param res   Application resources.
	 * @param prefs Our {@link Preferences} object.
	 */
	public MainWindow(ApplicationResources res, Preferences prefs) {
		this();
		this.res = res;
		this.prefs = prefs;
		
		frmPartcat.setIconImages(this.res.getApplicationIcons());
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
	 * Event fired whenever the selection of the component tree view changes.
	 * 
	 * @param event Tree selection event.
	 */
	public void componentTreeValueChanged(TreeSelectionEvent event) {
		Object node = event.getPath().getLastPathComponent();
		
		// Check for unsaved changes and go to the previous selection if aborted.
		if (handleUnsavedChanges()) {
			ignoreUnsaved = true;
			treeComponents.setSelectionPath(event.getOldLeadSelectionPath());
			return;
		}
		
		ignoreUnsaved = false;
		if (node instanceof ComponentTreeNode) {
			// Node is a component.
			setCurrentComponent(((ComponentTreeNode)node).getComponent());
		} else {
			// Nope, not a component, so clear out.
			clearComponentView();
		}
	}
	
	/**
	 * Populates the tree view using a components list iterator and a filtering
	 * {@link String}.
	 * 
	 * @param iter   Components list iterator.
	 * @param filter A filtering string to be applied when populating.
	 * 
	 * @see {@link Component#componentIterator}
	 */
	public void populateComponentsTree(ListIterator<Component> iter, String filter) {
		// Create the tree root node.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Components");
		
		// Go through components adding them to the tree.
		while (iter.hasNext()) {
			Component comp = iter.next();
			
			// Check if we should apply any filtering.
			if (!filter.isEmpty()) {
				if (!comp.getName().toLowerCase().contains(filter.toLowerCase()))
					continue;
			}
			
			// Check if the component has been deleted before adding it.
			if (!comp.isDeleted())
				root.add(new ComponentTreeNode(comp));
		}
		
		// Set the tree model.
		treeComponents.setModel(new DefaultTreeModel(root));
	}
	

	
	/**
	 * Populates the tree view using a components list iterator.
	 * 
	 * @param iter   Components list iterator.
	 * @see {@link Component#componentIterator}
	 */
	public void populateComponentsTree(ListIterator<Component> iter) {
		populateComponentsTree(iter, "");
	}
	
	/**
	 * Applies filtering to the component tree view via a filtering string.
	 * 
	 * @param filter A filtering string.
	 */
	public void applyTreeFiltering(String filter) {
		populateComponentsTree(workspace.componentIterator(), filter);
	}
	
	/**
	 * Syncs the component changes in the UI to the {@link Component} object.
	 */
	public void syncComponentChanges() {
		currentComponent.setQuantity((Integer)spnQuantity.getValue());
		currentComponent.setNotes(txtNotes.getText());
		
		// Properties.
		DefaultTableModel model = (DefaultTableModel)tblProperties.getModel();
		ComponentProperties prop = currentComponent.getProperties();
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
		btnDatasheet.setEnabled(true);
		btnExtras.setEnabled(true);
		
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
		
		// Table.
		clearPropertiesTable();
		tblProperties.setEnabled(false);
		
		// Buttons.
		btnDatasheet.setEnabled(false);
		btnExtras.setEnabled(false);
		
		// Component.
		currentComponent = null;
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
		tblModelListener = new PropertiesTableModelListener(this);
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
		currentComponent = component;
		
		showComponent(currentComponent);
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
	 * Sets the last opened workspace preference variable.
	 * 
	 * @param workspace Last opened PartCat workspace.
	 */
	public void setLastOpenedWorkspace(PartCatWorkspace workspace) {
		prefs.put(PartCatConstants.LAST_OPENED_WORKSPACE_KEY,
				workspace.getPath().toString());
	}
	
	/**
	 * Clears the last opened workspace preferences variable.
	 */
	public void clearLastOpenedWorkspace() {
		prefs.remove(PartCatConstants.LAST_OPENED_WORKSPACE_KEY);
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
		return this.unsavedChanges && !this.ignoreUnsaved;
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
		
		dlgAbout = new AboutDialog();
		
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
				action.saveComponent(currentComponent);
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
		
		JMenuItem mntmNewWorkspace = new JMenuItem("New Workspace...");
		mntmNewWorkspace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (handleUnsavedChanges())
					return;
				
				action.createWorkspace();
			}
		});
		mnFile.add(mntmNewWorkspace);
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
				clearLastOpenedWorkspace();
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
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dlgAbout.setVisible(true);
			}
		});
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
		treeComponents.addMouseListener(new ComponentMousePopupListener(this, treeComponents));
		treeComponents.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent event) {
				if (!ignoreUnsaved) {
					componentTreeValueChanged(event);
				} else {
					ignoreUnsaved = false;
				}
			}
		});
		sl_leftPanel.putConstraint(SpringLayout.NORTH, treeComponents, 5, SpringLayout.NORTH, leftPanel);
		sl_leftPanel.putConstraint(SpringLayout.WEST, treeComponents, 5, SpringLayout.WEST, leftPanel);
		sl_leftPanel.putConstraint(SpringLayout.EAST, treeComponents, -5, SpringLayout.EAST, leftPanel);
		sclTree.setViewportView(treeComponents);
		
		txtFilter = new JTextField();
		sl_leftPanel.putConstraint(SpringLayout.SOUTH, txtFilter, -5, SpringLayout.SOUTH, leftPanel);
		sl_leftPanel.putConstraint(SpringLayout.SOUTH, sclTree, -5, SpringLayout.NORTH, txtFilter);
		sl_leftPanel.putConstraint(SpringLayout.WEST, txtFilter, 0, SpringLayout.WEST, sclTree);
		sl_leftPanel.putConstraint(SpringLayout.EAST, txtFilter, 0, SpringLayout.EAST, sclTree);
		leftPanel.add(txtFilter);
		txtFilter.setToolTipText("Filter");
		txtFilter.setColumns(10);
		txtFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				applyTreeFiltering(txtFilter.getText());
			}

			public void insertUpdate(DocumentEvent e) {
				applyTreeFiltering(txtFilter.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				applyTreeFiltering(txtFilter.getText());
			}
		});
		
		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		SpringLayout sl_rightPanel = new SpringLayout();
		rightPanel.setLayout(sl_rightPanel);
		
		lblImage = new JLabel("Image");
		lblImage.addMouseListener(new ImageMousePopupListener(this));
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
		btnDatasheet.setEnabled(false);
		btnDatasheet.addMouseListener(new DatasheetPopupListener(this));
		extrasPanel.add(btnDatasheet);
		
		btnExtras = new JButton("Extras");
		btnExtras.setEnabled(false);
		btnExtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentComponent != null)
					  action.openComponentFolder();
			}
		});
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
		sclTable.addMouseListener(new PropertiesMousePopupListener(this, tblProperties, false));
		tblProperties.addMouseListener(new PropertiesMousePopupListener(this, tblProperties, true));
		
		tblProperties.setCellSelectionEnabled(true);
		tblProperties.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblProperties.setModel(table_model);
		tblProperties.setAutoCreateRowSorter(true);
		sclTable.setViewportView(tblProperties);
	}
}
