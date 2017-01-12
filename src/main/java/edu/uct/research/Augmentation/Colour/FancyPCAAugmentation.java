package edu.uct.research.Augmentation.Colour;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import org.imgscalr.Scalr;

import edu.uct.research.Augmentation.Augmentation;
import edu.uct.research.Project.Settings;
import smile.projection.PCA;

public class FancyPCAAugmentation extends Augmentation {

	private static final Random random = new Random();

	@Override
	public String getName() {
		return "FancyPCAAugmentation";
	}
	
	@Override
	public ArrayList<BufferedImage> apply(BufferedImage src) {
		
		int scale = 10000000/2;
		PCA pca = new PCA(getData(src));

		double[] eigen = new double[3];
		eigen[0] = pca.getVariance()[0] * getRandom(0, 0.1f);
		eigen[1] = pca.getVariance()[1] * getRandom(0, 0.1f);
		eigen[2] = pca.getVariance()[2] * getRandom(0, 0.1f);
		eigen[0] /= scale;
		eigen[1] /= scale;
		eigen[2] /= scale;
		
		int[] add = new int[3];
		add[0] = (int) (pca.getLoadings()[0][0] * eigen[0] + pca.getLoadings()[0][1] * eigen[1] + pca.getLoadings()[0][2] * eigen[2]);
		add[1] = (int) (pca.getLoadings()[1][0] * eigen[0] + pca.getLoadings()[1][1] * eigen[1] + pca.getLoadings()[1][2] * eigen[2]);
		add[2] = (int) (pca.getLoadings()[2][0] * eigen[0] + pca.getLoadings()[2][1] * eigen[1] + pca.getLoadings()[2][2] * eigen[2]);
		
		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();

		int offset = (Settings.transformedLength - Settings.modelLength) / 2;
		
		// Add original down-sized image
		list.add(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength));
		// Add flip-version
		list.add(enhance(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength), add)); 
		
		return list;

	}

	private BufferedImage enhance(BufferedImage src, int[] vec) {
		int width = src.getWidth();
		int height = src.getHeight();
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				Color colorData = new Color(src.getRGB(i, j));
				int red = clamp(colorData.getRed() + vec[0]);
				int green = clamp(colorData.getGreen() + vec[1]);
				int blue = clamp(colorData.getBlue() + vec[2]);
				
				Color finalColorData = new Color(red, green, blue);
				img.setRGB(i, j, finalColorData.getRGB());
				
			}
		}
		
		return img;
		
	}
	
	private int clamp(int value) {
		if(value < 0) return 0;
		if(value > 255) return 255;
		return value;
		
	}
	
	private float getRandom(float mean, float std) {
		return (float) (random.nextGaussian() * std + mean);

	}

	// Return channel data of image
	private double[][] getData(BufferedImage img) {

		int width = img.getWidth();
		int height = img.getHeight();

		double[][] rgb = new double[width * height][3];

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				Color colorData = new Color(img.getRGB(i, j));
				rgb[j * width + i][0] = colorData.getRed();
				rgb[j * width + i][1] = colorData.getGreen();
				rgb[j * width + i][2] = colorData.getBlue();

			}
		}

		return rgb;

	}
	

}
