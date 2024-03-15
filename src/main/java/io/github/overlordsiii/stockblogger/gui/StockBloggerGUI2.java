package io.github.overlordsiii.stockblogger.gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.overlordsiii.stockblogger.api.Article;
import io.github.overlordsiii.stockblogger.api.Stock;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import io.github.overlordsiii.stockblogger.util.GuiUtil;
import io.github.overlordsiii.stockblogger.util.JsonUtils;

public class StockBloggerGUI2 extends JFrame {

	public StockBloggerGUI2(Stock selectedStock, List<Stock> rivals, List<Article> articles) throws MalformedURLException {
		setTitle("StockBlogger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setResizable(false);

		// Create a panel for the entire content
		JPanel contentPanel = new JPanel(new BorderLayout());

		// Create two panels for the split pane
		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel rightPanel = new JPanel();

		// Create the split pane for the main layout
		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);

		// Create two panels for the inner split pane in the left panel
		JPanel topInnerPanel = new JPanel();
		JPanel bottomInnerPanel = new JPanel();
		bottomInnerPanel.setLayout(new BoxLayout(bottomInnerPanel, BoxLayout.Y_AXIS));

		// Create the split pane for the inner layout in the left panel
		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topInnerPanel, bottomInnerPanel);

		// Add the inner split pane to the left panel
		leftPanel.add(leftSplitPane, BorderLayout.CENTER);

		// Create the inner split pane for the right panel
		JSplitPane rightInnerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightPanel.add(rightInnerSplitPane, BorderLayout.CENTER);

		// Create panels for the top and bottom parts of the right inner split pane
		JPanel topGraphPanel = new JPanel(new BorderLayout());
		JPanel bottomArticlePanel = new JPanel();

		// Add components to the right inner split pane
		rightInnerSplitPane.setTopComponent(topGraphPanel);
		rightInnerSplitPane.setBottomComponent(bottomArticlePanel);

		// Add the main split pane to the content panel
		contentPanel.add(mainSplitPane, BorderLayout.CENTER);

		topInnerPanel.add(GuiUtil.stockToPanel(selectedStock, topInnerPanel));

		Box rivalsBox = Box.createVerticalBox();

		for (Stock rival : rivals) {
			rivalsBox.add(GuiUtil.stockToPanel(rival, rivalsBox));
		}

		JScrollPane scrollPane = new JScrollPane(rivalsBox);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		bottomInnerPanel.add(scrollPane);

		// Set the initial divider locations for the split panes
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				Dimension size = getSize();
				int leftPanelWidth = (int) (size.width * 0.15);
				int leftSplitPaneTopHeight = (int) (size.height * 0.37);
				mainSplitPane.setDividerLocation(leftPanelWidth);
				leftSplitPane.setDividerLocation(leftSplitPaneTopHeight);

				// TODO fix resizing of images
				if (e.getComponent() instanceof JLabel jLabel) {
					if (jLabel.getIcon() instanceof ImageIcon imageIcon) {
						imageIcon.setImage(imageIcon.getImage().getScaledInstance((int) (0.27 * size.width), (int) (0.27 * size.width), Image.SCALE_DEFAULT));
					}
				}
			}
		});

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
		StockBloggerGUI2 gui = new StockBloggerGUI2(selectedStock, rivals, articles);
		gui.setVisible(true);
	}
}
