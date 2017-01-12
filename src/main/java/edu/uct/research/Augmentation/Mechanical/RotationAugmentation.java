package edu.uct.research.Augmentation.Mechanical;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.imgscalr.Scalr;

import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.RotateFilter;

import edu.uct.research.Augmentation.Augmentation;
import edu.uct.research.Project.Settings;

public class RotationAugmentation extends Augmentation {

	@Override
	public String getName() {
		return "RotationAugmentation";
	}

	@Override
	public ArrayList<BufferedImage> apply(BufferedImage src) {
		ArrayList<BufferedImage> list = new ArrayList<BufferedImage>();

		int offset = (Settings.transformedLength - Settings.modelLength) / 2;
		
		// Add original down-sized image
		list.add(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength));
		
		list.add(rotate(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength), 30)); 
		list.add(rotate(Scalr.crop(src, offset, offset, Settings.modelLength, Settings.modelLength), -30)); 
		
		return list;
		
	}
	
	private BufferedImage rotate(BufferedImage src, int angle) {
//		RotateFilter mixer = new RotateFilter(angle, false);
//		return mixer.filter(src, null);
		
		
		AffineTransform transform = new AffineTransform();
	    transform.rotate(Math.toRadians(angle), src.getWidth()/2, src.getHeight()/2);
	    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
	    return op.filter(src, null);
		
	}

	
}
