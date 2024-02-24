package io.github.overlordsiii.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class StockBloggerGUI extends JFrame {



	public StockBloggerGUI() {
		setTitle("StockBlogger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		JPanel sidebarPanel = new JPanel();
		sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment

		// Create two sections for the sidebar
		JPanel requestedStock = new JPanel();
		requestedStock.setBackground(Color.LIGHT_GRAY);
		requestedStock.setPreferredSize(new Dimension(200, 50)); // Set preferred size for first section

		JLabel stockLabel = new JLabel("Target Stock: XYZ - $100.00", SwingConstants.CENTER);
		stockLabel.setFont(new Font("Arial", Font.BOLD, 24));
		requestedStock.add(stockLabel, BorderLayout.CENTER);


		JPanel rivalStocks = new JPanel();
		rivalStocks.setBackground(Color.GRAY);
		rivalStocks.setPreferredSize(new Dimension(200, 550)); // Set preferred size for second section

		// Add the sections to the sidebar panel
		sidebarPanel.add(requestedStock);
		sidebarPanel.add(rivalStocks);

		// Create a panel for the main content
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setBackground(Color.WHITE); // Set background color for main content

		// Add components to the main content panel
		JLabel mainLabel = new JLabel("Main Content");
		mainContentPanel.add(mainLabel);

		// Create a split pane to hold the sidebar and main content
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, mainContentPanel);
		splitPane.setResizeWeight(0.25); // Set the initial size of the sidebar

		// Add the split pane to the JFrame's content pane
		getContentPane().add(splitPane);
	}

	public static void main(String[] args) {
		// Create and display the JFrame
		SwingUtilities.invokeLater(() -> {
			StockBloggerGUI frame = new StockBloggerGUI();
			frame.setVisible(true);
		});
	}
}
