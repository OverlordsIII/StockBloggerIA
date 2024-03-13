package io.github.overlordsiii.stockblogger.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.overlordsiii.stockblogger.api.Article;
import io.github.overlordsiii.stockblogger.api.Stock;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import io.github.overlordsiii.stockblogger.util.GuiUtil;
import io.github.overlordsiii.stockblogger.util.JsonUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class StockBloggerGUI extends JFrame {



	private StockBloggerGUI(Stock selectedStock, List<Stock> rivals, List<Article> articles) throws MalformedURLException {
		setTitle("StockBlogger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// Create a panel for the entire content
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS)); // Use BoxLayout for horizontal alignment

		// Create a panel for the sidebar
		JPanel sidebarPanel = new JPanel();
		sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment
		sidebarPanel.setPreferredSize(new Dimension((int) (screenSize.getWidth() / 13), screenSize.height / 2)); // Set preferred size for sidebar
		sidebarPanel.setMaximumSize(new Dimension((int) (screenSize.getWidth() / 13), screenSize.height / 2)); // Set maximum size for sidebar
		sidebarPanel.setBackground(Color.GREEN);

		// Create two sections for the sidebar
		JPanel requestedStock = new JPanel();
		requestedStock.setAlignmentX(Component.LEFT_ALIGNMENT); // Align components to the left
		requestedStock.setBackground(Color.YELLOW);
		//requestedStock.setPreferredSize(new Dimension(150, requestedStock.getHeight())); // Set preferred size for sidebar
		//requestedStock.setMaximumSize(new Dimension(150, requestedStock.getHeight())); // Set maximum size for sidebar
		requestedStock.setLayout(new BoxLayout(requestedStock, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment
		requestedStock.setPreferredSize(new Dimension(sidebarPanel.getWidth(), requestedStock.getHeight()));

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
		rivalStocks.setBackground(Color.RED);
		rivalStocks.setPreferredSize(new Dimension(sidebarPanel.getWidth(), requestedStock.getHeight()));
	//	rivalStocks.setPreferredSize(new Dimension(150, rivalStocks.getHeight())); // Set preferred size for sidebar
	//	rivalStocks.setMaximumSize(new Dimension(150, rivalStocks.getHeight())); // Set maximum size for sidebar
		rivalStocks.setLayout(new BoxLayout(rivalStocks, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignme

		for (int i = 0; i < rivals.size(); i++) {
			Stock rival = rivals.get(i);

			JPanel rivalStockInitialPanel = new JPanel();
			rivalStockInitialPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align components to the left
			rivalStockInitialPanel.setBackground(Color.BLUE);
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
// Create a panel for the main content
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment
		mainContentPanel.setBackground(Color.GRAY); // Set background color for main content

		// Create top panel for graph
		JPanel graphPanel = new JPanel();
		graphPanel.setBackground(Color.LIGHT_GRAY);
		graphPanel.setPreferredSize(new Dimension((screenSize.width / 10), 2*(screenSize.height / 3) ));


		JPanel graphInitialPanel = new JPanel();
		graphInitialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		graphInitialPanel.setBackground(Color.LIGHT_GRAY);
		graphInitialPanel.setLayout(new BoxLayout(graphInitialPanel, BoxLayout.Y_AXIS));

		JLabel graphLabel = new JLabel("Graph Placeholder");
		graphLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		graphLabel.setFont(new Font("Arial", Font.BOLD, 29));
		graphInitialPanel.add(graphLabel);

		JFreeChart chart = createChart(selectedStock.getHistoricalData());
		ChartPanel chartPanel = new ChartPanel(chart);
		graphInitialPanel.add(chartPanel);

		graphPanel.add(graphInitialPanel);

		// Create lower panel for article summaries
		JPanel articlePanel = new JPanel();
		articlePanel.setBackground(Color.WHITE);
		articlePanel.setLayout(new BoxLayout(articlePanel, BoxLayout.Y_AXIS));
		articlePanel.setPreferredSize(new Dimension((screenSize.width / 10), screenSize.height / 3));
		JLabel articleLabel = new JLabel("Article Summaries");
		articleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		articleLabel.setFont(new Font("Arial", Font.BOLD, 29));
		articlePanel.add(articleLabel);



		JPanel rivalStockInitialPanel = new JPanel();
		rivalStockInitialPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Align components to the left
		rivalStockInitialPanel.setBackground(Color.GRAY);
		//rivalStockInitialPanel.setPreferredSize(new Dimension(150, rivalStockInitialPanel.getHeight())); // Set preferred size for sidebar
		//rivalStockInitialPanel.setMaximumSize(new Dimension(150, rivalStockInitialPanel.getHeight())); // Set maximum size for sidebar
		rivalStockInitialPanel.setLayout(new BoxLayout(rivalStockInitialPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignme

		JScrollPane scrollPane1 = new JScrollPane(rivalStockInitialPanel);

		for (Article article : articles) {
			// Create a Box container for each article
			Box articleBox = Box.createVerticalBox();

			// Create a JLabel for the article title
			JLabel titleLabel = new JLabel(article.getTitle());
			titleLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Example font and size
			titleLabel.setForeground(Color.BLUE.darker());
			titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			titleLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						Desktop.getDesktop().browse(article.getUrl());
					} catch (IOException ex) {
						System.out.println("Error when clicking on link \"" + article.getUrl() + "\"!");
						ex.printStackTrace();
					}
				}
			});
			articleBox.add(titleLabel);

			// Add bullet points using JTextArea
			JTextArea bulletPointsArea = new JTextArea();
			bulletPointsArea.setEditable(false); // Make it non-editable
			bulletPointsArea.setLineWrap(true);
			bulletPointsArea.setWrapStyleWord(true);

			// Add each bullet point to the JTextArea
			for (String bulletPoint : article.getSummarizedBulletPoints()) {
				bulletPointsArea.append(" " + bulletPoint + "\n"); // Using "•" as a bullet symbol
			}

			// Create a scroll pane for the bullet points area
			JScrollPane scrollPane = new JScrollPane(bulletPointsArea);
			scrollPane.setPreferredSize(new Dimension(300, 150)); // Example preferred size

			// Add the scroll pane to the article box
			articleBox.add(scrollPane);

			// Add some vertical space between articles
			articleBox.add(Box.createVerticalStrut(10));

			// Add the Box container to the articlePanel
			rivalStockInitialPanel.add(articleBox);
		}
		articlePanel.add(scrollPane1);

		// Add components to the main content panel
		mainContentPanel.add(graphPanel);
		mainContentPanel.add(articlePanel);

		// Create a split pane to hold the sidebar and main content
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, mainContentPanel);
		splitPane.setResizeWeight(0.25); // Set the initial size of the sidebar
		splitPane.setAlignmentX(Component.LEFT_ALIGNMENT); // Align component to the left

		// Add the split pane to the content panel
		contentPanel.add(splitPane);

		// Add the content panel to the frame's content pane
		getContentPane().add(contentPanel);
	}

	private JFreeChart createChart(Map<Integer, Double> data) {
		XYSeries series = new XYSeries("Stock Price");

		// Add data to the series
		for (Map.Entry<Integer, Double> entry : data.entrySet()) {
			series.add(entry.getKey(), entry.getValue());
		}

		XYSeriesCollection dataset = new XYSeriesCollection(series);

		// Create the chart
		JFreeChart chart = ChartFactory.createXYLineChart(
			"Stock Price Over Time",  // chart title
			"Weeks",                  // x-axis label
			"Price",                  // y-axis label
			dataset                  // data
		);

		return chart;
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
