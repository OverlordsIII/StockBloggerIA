package io.github.overlordsiii.stockblogger.gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

public class StockBloggerGUI extends JFrame {

	public StockBloggerGUI(Stock selectedStock, List<Stock> rivals, List<Article> articles) throws MalformedURLException {
		setTitle("StockBlogger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		//setResizable(false);

		JPanel contentPanel = new JPanel(new BorderLayout());

		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel rightPanel = new JPanel(new BorderLayout());

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);

		JPanel topInnerPanel = new JPanel();
		JPanel bottomInnerPanel = new JPanel();
		bottomInnerPanel.setLayout(new BoxLayout(bottomInnerPanel, BoxLayout.Y_AXIS));

		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topInnerPanel, bottomInnerPanel);

		leftPanel.add(leftSplitPane, BorderLayout.CENTER);

		JPanel graphPanel = new JPanel();
		JPanel articlePanel = new JPanel();
		articlePanel.setLayout(new BoxLayout(articlePanel, BoxLayout.Y_AXIS));

		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphPanel, articlePanel);

		rightPanel.add(rightSplitPane);

		contentPanel.add(mainSplitPane, BorderLayout.CENTER);

		GuiUtil.addSelectedStockIndicator(selectedStock, topInnerPanel);

		GuiUtil.addRivals(bottomInnerPanel, rivals);

		GuiUtil.addArticles(articlePanel, articles);

		List<Stock> allStocks = new ArrayList<>();
		allStocks.add(selectedStock);
		allStocks.addAll(rivals);

		GuiUtil.addGraph(graphPanel, allStocks);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension size = getSize();
				int leftPanelWidth = (int) (size.width * 0.12);
				int leftSplitPaneTopHeight = (int) (size.height * 0.23);
				mainSplitPane.setDividerLocation(leftPanelWidth);
				leftSplitPane.setDividerLocation(leftSplitPaneTopHeight);
				rightSplitPane.setDividerLocation(((int) (size.height * 0.55)));
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
		StockBloggerGUI gui = new StockBloggerGUI(selectedStock, rivals, articles);
		gui.setVisible(true);
	}
}
