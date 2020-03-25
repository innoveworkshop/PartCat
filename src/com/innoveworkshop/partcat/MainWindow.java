package com.innoveworkshop.partcat;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

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
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Our main window class.
 *
 * @author Nathan Campos <nathan@innoveworkshop.com>
 */
public class MainWindow {
	private JFrame frmPartcat;
	private JTextField txtSearch;
	private JTextField txtName;
	private JTable tblProperties;

	/**
	 * Creates the main frame.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPartcat = new JFrame();
		frmPartcat.setTitle("PartCat");
		frmPartcat.setBounds(100, 100, 645, 384);
		frmPartcat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmPartcat.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
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
		
		JTree treeComponents = new JTree();
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
		
		JSpinner spnQuantity = new JSpinner();
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
		
		JButton btnDatasheet = new JButton("Datasheet");
		extrasPanel.add(btnDatasheet);
		
		JButton btnModel = new JButton("Model");
		extrasPanel.add(btnModel);
		
		JButton btnExtras = new JButton("Extras");
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
		
		JTextArea txtNotes = new JTextArea();
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
		
		tblProperties.setCellSelectionEnabled(true);
		tblProperties.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblProperties.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Key", "Value"
			}
		) {
			Class[] columnTypes = new Class[] {
				Object.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		sclTable.setViewportView(tblProperties);
	}

	/**
	 * Shows the main window frame.
	 */
	public void show() {
		frmPartcat.setVisible(true);
	}
}
