package edu.uct.research.Augmentation.Colour;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.imgscalr.Scalr;

import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.GrayscaleFilter;
import com.jhlabs.image.InvertFilter;

import edu.uct.research.Augmentation.Augmentation;
import edu.uct.research.Project.Settings;

public class EdgeEnhancerAugmentation extends Augmentation {

	@Override
	public String getName() {
		return "EdgeEnhancerAugmentation";

	}

	@Override
	public ArrayList<BufferedImage> apply(BufferedImage src) {
		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();

		int offset = (Settings.transformedLength - Settings.modelLength) / 2;
		
		list.add(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength));
		list.add(enhance(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength))); 

		return list;

	}

	private BufferedImage enhance(BufferedImage src) {		
		EdgeFilter m1 = new EdgeFilter();
		GrayscaleFilter m2 = new GrayscaleFilter();
		InvertFilter m3 = new InvertFilter();

		BufferedImage dst = m1.filter(src, null);
		dst = m2.filter(dst, null);
		dst = m3.filter(dst, null);
		
		return blend(src, dst, 0.6f);

	}

	public BufferedImage blend (BufferedImage img1, BufferedImage img2, double weight) {

		BufferedImage bi3 = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bi3.createGraphics();
		g2d.drawImage(img1, null, 0, 0);
		g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.4f));
		g2d.drawImage(img2, null, 0, 0);
		g2d.dispose();

		return bi3;
		
	}


}
