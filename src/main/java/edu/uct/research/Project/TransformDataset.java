package edu.uct.research.Project;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class TransformDataset {

	private static final File originalDataSet = new File("Caltech-101");
	private static final File transformedDataSet = new File("Caltech-101_");
	
	// private static final int classSize = 40;
	
	public static void main(String[] args) throws IOException {
		
		HashMap<String, ArrayList<BufferedImage>> map = new HashMap<String, ArrayList<BufferedImage>>();
		
		for(File dir : originalDataSet.listFiles()) {
			if(!dir.isDirectory()) continue;
			new File(transformedDataSet + "/" + dir.getName()).mkdirs();
			
			System.out.println("Class valid: " + dir.getName());
			
			List<BufferedImage> images = getImages(dir);
			Collections.shuffle(images);
			
			int classSize = getClassSize(dir) - (getClassSize(dir) % Settings.k);
			
			for(int i = 0; i < classSize; i++) {
				ImageIO.write(getTransformedImage(images.get(i)), "png", new File(transformedDataSet + "/" + dir.getName() + "/" + i + ".jpg"));
				
			}
			
		}

	}
	
	private static BufferedImage getTransformedImage(BufferedImage src) {
		BufferedImage dst = new BufferedImage(Settings.transformedLength, Settings.transformedLength, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = dst.createGraphics();
		
		int width = src.getWidth();
		int height = src.getHeight();
		
		if(width > height) {
			BufferedImage temp = Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, Settings.transformedLength, Settings.transformedLength);
			int offset = (Settings.transformedLength - temp.getHeight()) / 2;
			g.drawImage(temp, 0, offset, temp.getWidth(), temp.getHeight(), null);
			
		}
		else {
			BufferedImage temp = Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, Settings.transformedLength, Settings.transformedLength);
			int offset = (Settings.transformedLength - temp.getWidth()) / 2;
			g.drawImage(temp, offset, 0, temp.getWidth(), temp.getHeight(), null);
			
		}
		
		return dst;
		
	}
	
	private static List<BufferedImage> getImages(File file) {
		
		List<BufferedImage> images = new ArrayList<BufferedImage>();
		
		for(File img : file.listFiles()) {
			try {
				images.add(ImageIO.read(img));
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			
		}
		
		return images;
		
	}
	
	private static int getClassSize(File dir) {
		return dir.listFiles().length - 1; // -1 for the DS_STORE

	}
	
	
}
