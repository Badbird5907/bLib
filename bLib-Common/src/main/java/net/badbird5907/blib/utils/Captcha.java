package net.badbird5907.blib.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.Color.black;
import static java.awt.Color.white;
import static java.awt.Font.BOLD;
import static java.awt.geom.AffineTransform.getRotateInstance;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.*;
import static java.util.stream.IntStream.range;

public class Captcha {
	private String captchaString;

	public Captcha(String code) {
		this.captchaString = code;
	}

	public BufferedImage getCaptchaImage() {
		try {
			Color textColor = black;
			Color circleColor = new Color(190, 160, 150);
			Font textFont = new Font("Verdana", BOLD, 20);
			int charsToPrint = 6;
			int width = 160;
			int height = 50;
			int circlesToDraw = 25;
			float horizMargin = 10.0f;
			double rotationRange = 0.7;
			BufferedImage bufferedImage = new BufferedImage(width, height, TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
			g.setColor(white);
			g.fillRect(0, 0, width, height);
			// lets make some noisey circles
			g.setColor(circleColor);
			range(0, circlesToDraw).map(i -> (int) ((random() * height) / 2.0)).forEach(L -> {
				int X = (int) ((random() * width) - L);
				int Y = (int) ((random() * height) - L);
				g.draw3DRect(X, Y, L * 2, L * 2, true);
			});
			g.setColor(textColor);
			g.setFont(textFont);
			FontMetrics fontMetrics = g.getFontMetrics();
			int maxAdvance = fontMetrics.getMaxAdvance();
			int fontHeight = fontMetrics.getHeight();
			// i removed 1 and l and i because there are confusing to users...
			// Z, z, and N also get confusing when rotated
			// this should ideally be done for every language...
			// 0, O and o removed because there are confusing to users...
			// i like controlling the characters though because it helps prevent confusion
			String eligibleChars = "ABCDEFGHJKLMNPQRSTUVWXYabcdefghjkmnpqrstuvwxy23456789";
			char[] chars = eligibleChars.toCharArray();
			float spaceForLetters = -horizMargin * 2 + width;
			float spacePerChar = spaceForLetters / (charsToPrint - 1.0f);
			StringBuilder finalString = new StringBuilder();
			for (int i = 0; i < charsToPrint; i++) {
				double randomValue = random();
				int randomIndex = (int) round(randomValue * (chars.length - 1));
				char characterToShow = chars[randomIndex];
				finalString.append(characterToShow);
				// this is a separate canvas used for the character so that
				// we can rotate it independently
				int charWidth = fontMetrics.charWidth(characterToShow);
				int charDim = max(maxAdvance, fontHeight);
				int halfCharDim = charDim / 2;
				BufferedImage charImage = new BufferedImage(charDim, charDim, TYPE_INT_ARGB);
				Graphics2D charGraphics = charImage.createGraphics();
				charGraphics.translate(halfCharDim, halfCharDim);
				double angle = (random() - 0.5) * rotationRange;
				charGraphics.transform(getRotateInstance(angle));
				charGraphics.translate(-halfCharDim, -halfCharDim);
				charGraphics.setColor(textColor);
				charGraphics.setFont(textFont);
				int charX = (int) (0.5 * charDim - 0.5 * charWidth);
				charGraphics.drawString("" + characterToShow, charX, (charDim - fontMetrics.getAscent()) / 2 + fontMetrics.getAscent());
				float x = (horizMargin + (spacePerChar * i)) - (charDim / 2.0f);
				int y = (height - charDim) / 2;
				g.drawImage(charImage, (int) x, y, charDim, charDim, null, null);
				charGraphics.dispose();
			}
			g.setColor(black);
			g.drawRect(0, 0, width - 1, height - 1);
			g.dispose();
			captchaString = finalString.toString();
			return bufferedImage;
		} catch (Exception ioe) {
			throw new RuntimeException("Unable to build image", ioe);
		}
	}

	// Function to return the Captcha string
	public String getCaptchaString() {
		return captchaString;
	}

}
