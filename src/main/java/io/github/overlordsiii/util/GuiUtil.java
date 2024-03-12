package io.github.overlordsiii.util;

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GuiUtil {
	public static void addImage(JPanel label, String imageUrl) throws MalformedURLException {
		ImageIcon icon = new ImageIcon(new URL(imageUrl));
		label.add(new JLabel(icon), Component.LEFT_ALIGNMENT);
	}

	public static String getPrice(Double twoDigitPrice) {
		String moneyString = Double.toString(twoDigitPrice);

		if (moneyString.charAt(moneyString.length() - 2) == '.') {
			moneyString += "0";
		}

		return moneyString;
	}
}
