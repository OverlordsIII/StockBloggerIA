package io.github.overlordsiii.stockblogger.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import io.github.overlordsiii.stockblogger.api.Stock;
import io.github.overlordsiii.stockblogger.config.PropertiesHandler;

public class GuiUtil {
	public static void addImage(Container label, String imageUrl, String name) throws MalformedURLException {
		URL url;

		if (imageUrl == null) {
			System.out.println("Image URL was null for " + name);
			url = PropertiesHandler.CONFIG_HOME_DIRECTORY.resolve("not_found.png").toFile().toURI().toURL();
		} else {
			url = new URL(imageUrl);
		}


		ImageIcon icon = new ImageIcon(url);

		JLabel imageIcon = new JLabel(icon);

		label.add(imageIcon);
	}

	public static Box stockToPanel(Stock stock, Container parent) throws MalformedURLException {
		Box box = Box.createVerticalBox();

		TitledBorder border = BorderFactory.createTitledBorder(stock.getName());

		border.setTitleFont(new Font("Arial", Font.BOLD, 14));

		box.add(Box.createVerticalStrut(20));

		box.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				border.setTitleFont(new Font("Arial", Font.BOLD, 18));
			}
		});

		box.setBorder(border);

		addImage(box, stock.getLogoUrl(), stock.getName());

		box.add(Box.createVerticalStrut(20));


		JLabel label = new JLabel("Price: $" + getPrice(stock.getPrice()));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.add(label);

		return box;
	}

	public static String getPrice(Double twoDigitPrice) {
		String moneyString = Double.toString(twoDigitPrice);

		if (moneyString.charAt(moneyString.length() - 2) == '.') {
			moneyString += "0";
		}

		return moneyString;
	}
}
