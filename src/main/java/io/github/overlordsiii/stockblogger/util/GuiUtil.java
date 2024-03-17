package io.github.overlordsiii.stockblogger.util;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import io.github.overlordsiii.stockblogger.api.Article;
import io.github.overlordsiii.stockblogger.api.Stock;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GuiUtil {
	public static void addImage(Container label, String imageUrl, String name) throws MalformedURLException {
		URL url;

		if (imageUrl == null) {
			System.out.println("Image URL was null for " + name);
			url = PropertiesHandler.CONFIG_HOME_DIRECTORY.resolve("not_found.png").toFile().toURI().toURL();
		} else {
			url = URI.create(imageUrl).toURL();
		}


		ImageIcon icon = new ImageIcon(url);

		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		icon.setImage(icon.getImage().getScaledInstance((int) (0.075 * size.width), (int) (0.075 * size.width), Image.SCALE_DEFAULT));

		JLabel imageIcon = new JLabel(icon);

		label.add(imageIcon);
	}

	public static Box stockToPanel(Stock stock, Container parent) throws MalformedURLException {
		Box box = Box.createVerticalBox();

		TitledBorder border = BorderFactory.createTitledBorder(stock.getName());

		border.setTitleFont(new Font("Arial", Font.BOLD, 18));

		box.add(Box.createVerticalStrut(20));

		box.setBorder(border);

		addImage(box, stock.getLogoUrl(), stock.getName());

		box.add(Box.createVerticalStrut(20));


		JLabel label = new JLabel("Price: $" + getPrice(stock.getPrice()));
		label.setFont(new Font("Arial", Font.BOLD, 18));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.add(label);

		return box;
	}

	public static void addSelectedStockIndicator(Stock selectedStock, JPanel topInnerPanel) throws MalformedURLException {
		Box box = Box.createVerticalBox();

		box.add(GuiUtil.stockToPanel(selectedStock, box));

		topInnerPanel.add(box);
	}

	public static void addRivals(JPanel bottomInnerPanel, List<Stock> rivals) throws MalformedURLException {
		Box rivalsBox = Box.createVerticalBox();

		for (Stock rival : rivals) {
			rivalsBox.add(GuiUtil.stockToPanel(rival, rivalsBox));
		}

		JScrollPane scrollPane = new JScrollPane(rivalsBox);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		bottomInnerPanel.add(scrollPane);
	}

	public static void addGraph(JPanel graphPanel, List<Stock> stocks) {
		JPanel graphInitialPanel = new JPanel();
		graphInitialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		graphInitialPanel.setBackground(Color.LIGHT_GRAY);
		graphInitialPanel.setLayout(new BoxLayout(graphInitialPanel, BoxLayout.Y_AXIS));

		JFreeChart chart = createChart(stocks);
		ChartPanel chartPanel = new ChartPanel(chart);

		chartPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
				int length = (int) (0.4 * size.width);
				int height = (int) (0.5 * size.height);
				chartPanel.setSize(length, height);
			}
		});

		JScrollPane graphScroller = new JScrollPane(chartPanel);
		graphInitialPanel.add(chartPanel);
		graphInitialPanel.add(graphScroller);

		graphPanel.add(graphInitialPanel);
	}

	public static JFreeChart createChart(List<Stock> stocks) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		for (Stock stock : stocks) {
			TimeSeries series = new TimeSeries(stock.getName());

			Map<LocalDateTime, Double> data = stock.getHistoricalData();

			// Add data to the series
			for (Map.Entry<LocalDateTime, Double> entry : data.entrySet()) {
				series.add(new Week(Timestamp.valueOf(entry.getKey())), entry.getValue());
			}

			dataset.addSeries(series);
		}

		// Create the chart
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
			"Stock Price Over Time",  // chart title
			"Date",                  // x-axis label
			"Price",                  // y-axis label
			dataset                  // data
		);

		// Customize the x-axis to display only years
		DateAxis xAxis = (DateAxis) chart.getXYPlot().getDomainAxis();
		xAxis.setDateFormatOverride(new SimpleDateFormat("yyyy")); // Set date format to display only year
		xAxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1)); // Set tick unit to one year

		return chart;
	}

	public static void addArticles(JPanel articlePanel, List<Article> articles) {
		articlePanel.setBackground(Color.WHITE);
		articlePanel.setLayout(new BoxLayout(articlePanel, BoxLayout.Y_AXIS));

		JLabel articleLabel = new JLabel("Article Summaries");
		articleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		articleLabel.setFont(new Font("Arial", Font.BOLD, 29));
		articlePanel.add(articleLabel);



		JPanel articleDisplayPanel = new JPanel();
		articleDisplayPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Align components to the left
		articleDisplayPanel.setBackground(Color.GRAY);
		articleDisplayPanel.setLayout(new BoxLayout(articleDisplayPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignme

		JScrollPane scrollPane1 = new JScrollPane(articleDisplayPanel);

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
			articleDisplayPanel.add(articleBox);
		}
		articlePanel.add(scrollPane1);
	}

	public static String getPrice(Double twoDigitPrice) {
		String moneyString = Double.toString(twoDigitPrice);

		if (moneyString.charAt(moneyString.length() - 2) == '.') {
			moneyString += "0";
		}

		return moneyString;
	}
}
