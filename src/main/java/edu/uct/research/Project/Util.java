package edu.uct.research.Project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class Util {
	
	public static class IO {
		
		public static MultiLayerNetwork readModel(File file) {
			try {
				FileInputStream fis = new FileInputStream(file);
				return ModelSerializer.restoreMultiLayerNetwork(fis);

			} 
			catch (IOException e) {
				e.printStackTrace();

			}

			return null;

		}

		public static void writeModel(MultiLayerNetwork model, File file) {
			file.getParentFile().mkdirs();
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				ModelSerializer.writeModel(model, fos, true);
				
			} 
			catch (IOException e) {
				e.printStackTrace();

			}

		}
		
		public static void writeFile(String filePrefix, String fileSuffix, String content) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(filePrefix + "." + fileSuffix, true));
				bw.write(content);
				bw.newLine();
				bw.flush();
			} 
			catch (IOException ioe) {
				ioe.printStackTrace();
			} 
			finally {
				if(bw != null) try { bw.close(); } catch (IOException e) { }
			}

		}
		
		
	}
	
	public static class Math {
		
		public static double round(double value, int places) {
			try {
				if(places < 0) throw new IllegalArgumentException();
			    BigDecimal bd = new BigDecimal(value);
			    bd = bd.setScale(places, RoundingMode.HALF_UP);
			    return bd.doubleValue();
			    
			}
			catch(Exception e) {
				return Double.NaN;
				
			}
		    
		}
		
		public static int getRandomNumberFrom(int min, int max) {
	        Random foo = new Random();
	        int randomNumber = foo.nextInt((max + 1) - min) + min;

	        return randomNumber;

	    }
		
	}
	
	
}
