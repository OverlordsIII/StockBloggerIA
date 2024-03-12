package io.github.overlordsiii.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.overlordsiii.api.Article;
import io.github.overlordsiii.api.Stock;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import io.github.overlordsiii.util.GuiUtil;
import io.github.overlordsiii.util.JsonUtils;

public class StockBloggerGUI extends JFrame {



	private StockBloggerGUI(Stock selectedStock, List<Stock> rivals, List<Article> articles) throws MalformedURLException {
		setTitle("StockBlogger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		// Create a panel for the entire content
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS)); // Use BoxLayout for horizontal alignment

		// Create a panel for the sidebar
		JPanel sidebarPanel = new JPanel();
		sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment
		sidebarPanel.setPreferredSize(new Dimension(200, getHeight())); // Set preferred size for sidebar
		sidebarPanel.setMaximumSize(new Dimension(200, getHeight())); // Set maximum size for sidebar
		sidebarPanel.setBackground(Color.LIGHT_GRAY);

		// Create two sections for the sidebar
		JPanel requestedStock = new JPanel();
		requestedStock.setAlignmentX(Component.LEFT_ALIGNMENT); // Align components to the left
		requestedStock.setBackground(Color.LIGHT_GRAY);
		//requestedStock.setPreferredSize(new Dimension(150, requestedStock.getHeight())); // Set preferred size for sidebar
		//requestedStock.setMaximumSize(new Dimension(150, requestedStock.getHeight())); // Set maximum size for sidebar
		requestedStock.setLayout(new BoxLayout(requestedStock, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment

		JLabel stockLabel = new JLabel("Selected Stock: " + selectedStock.getName() + " - $" + GuiUtil.getPrice(selectedStock.getPrice()), SwingConstants.CENTER);
		stockLabel.setFont(new Font("Arial", Font.BOLD, 29));
		stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		requestedStock.add(stockLabel, SwingConstants.CENTER);

		try {
			GuiUtil.addImage(requestedStock, selectedStock.getLogoUrl()); // Replace URL with your image URL
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		JPanel rivalStocks = new JPanel();
		rivalStocks.setAlignmentX(Component.LEFT_ALIGNMENT); // Align components to the left
		rivalStocks.setBackground(Color.GRAY);
	//	rivalStocks.setPreferredSize(new Dimension(150, rivalStocks.getHeight())); // Set preferred size for sidebar
	//	rivalStocks.setMaximumSize(new Dimension(150, rivalStocks.getHeight())); // Set maximum size for sidebar
		rivalStocks.setLayout(new BoxLayout(rivalStocks, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignme

		for (int i = 0; i < rivals.size(); i++) {
			Stock rival = rivals.get(i);

			JPanel rivalStockInitialPanel = new JPanel();
			rivalStockInitialPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align components to the left
			rivalStockInitialPanel.setBackground(Color.GRAY);
			//rivalStockInitialPanel.setPreferredSize(new Dimension(150, rivalStockInitialPanel.getHeight())); // Set preferred size for sidebar
			//rivalStockInitialPanel.setMaximumSize(new Dimension(150, rivalStockInitialPanel.getHeight())); // Set maximum size for sidebar
			rivalStockInitialPanel.setLayout(new BoxLayout(rivalStockInitialPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignme

			JLabel rivalLabel = new JLabel("Rival #" + (i + 1) + ": " + rival.getName() + " - $" + GuiUtil.getPrice(rival.getPrice()), SwingConstants.CENTER);
			rivalLabel.setFont(new Font("Arial", Font.BOLD, 29));
			rivalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			rivalStockInitialPanel.add(rivalLabel, SwingConstants.CENTER);

			try {
				GuiUtil.addImage(rivalStockInitialPanel, rival.getLogoUrl()); // Replace URL with your image URL
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			rivalStocks.add(rivalStockInitialPanel);
		}

		// Add the sections to the sidebar panel
		sidebarPanel.add(requestedStock);
		sidebarPanel.add(Box.createVerticalStrut(20)); // Add some vertical space between sections
		sidebarPanel.add(rivalStocks);

		// Create a panel for the main content
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment
		mainContentPanel.setBackground(Color.WHITE); // Set background color for main content

		// Add components to the main content panel
		JLabel mainLabel = new JLabel("Main Content", SwingConstants.CENTER);
		mainLabel.setFont(new Font("Arial", Font.BOLD, 24));
		mainLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Align component to the center
		mainContentPanel.add(mainLabel);
		mainContentPanel.add(Box.createVerticalGlue()); // Add vertical glue to push components to the top

		// Create a split pane to hold the sidebar and main content
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, mainContentPanel);
		splitPane.setResizeWeight(0.25); // Set the initial size of the sidebar
		splitPane.setAlignmentX(Component.LEFT_ALIGNMENT); // Align component to the left

		// Add the split pane to the content panel
		contentPanel.add(splitPane);

		// Add the content panel to the frame's content pane
		getContentPane().add(contentPanel);
	}


	public static void main(String[] args) throws IOException {
		Path path = PropertiesHandler.CONFIG_HOME_DIRECTORY;

		Files.walk(path, 1).forEach(path1 -> {
			if (!path1.toString().endsWith(".json") || Files.isDirectory(path1)) {
				return;
			}


			try {
				createGui(path1);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void createGui(Path jsonFile) throws IOException {
		JsonObject object = JsonUtils.toJsonObj(Files.readString(jsonFile));

		JsonElement selectedStock = object.get("selectedStock");
		JsonArray rivalsArray = object.get("rivals").getAsJsonArray();
		JsonArray articlesArray = object.get("articles").getAsJsonArray();

		Stock stock = JsonUtils.fromJsonElement(selectedStock, Stock.class);

		List<Stock> rivals = JsonUtils.getObjects(rivalsArray, Stock.class);

		List<Article> articles = JsonUtils.getObjects(articlesArray, Article.class);

		createGui(stock, rivals, articles);
	}

	public static void createGui(Stock selectedStock, List<Stock> rivals, List<Article> articles) throws MalformedURLException {
		StockBloggerGUI gui = new StockBloggerGUI(selectedStock, rivals, articles);
		gui.setVisible(true);
	}

}
