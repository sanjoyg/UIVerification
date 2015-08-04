package org.sanjoy.uitest.imaging;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


public class ImageVerifier {

	public static int 		GREY_SCALE_MARK_COLOR = -1;
	public static String 	PNG_FORMAT = "png";

	private static int 		X_SEARCH_OFFSET = 10;
	private static int 		Y_SEARCH_OFFSET = 10;

	private static float dash1[] = { 3.0f };
	private static BasicStroke dashed = new BasicStroke(2.0f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

	private List<Rectangle> _regions = null;
	private boolean 		_include = true;
	private ArrayList<Rectangle> _diffRects = new ArrayList<Rectangle>();

	private ImageVerifierResult _result = new ImageVerifierResult();

	public ImageVerifierResult getResult() {
		return _result;
	}

	public void setRegions(List<Rectangle> _regions) {
		this._regions = _regions;
	}

	public void addRegion(Rectangle rect) {
		if (this._regions == null)
			this._regions = new ArrayList<Rectangle>();
		this._regions.add(rect);
	}

	public void setInclude(boolean _include) {
		this._include = _include;
	}

	public String makeDifferenceImageFileName(String fileName) {
		return fileName + "diff.png";
	}

	public void makeDifferenceImage(BufferedImage img1, BufferedImage img2, String diffFileName) {
		int width1 = img1.getWidth(); // Change - getWidth() and getHeight() for BufferedImage
	    int width2 = img2.getWidth(); // take no arguments
	    int height1 = img1.getHeight();
	    int height2 = img2.getHeight();

	    BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);

	    int diff;
	    int result; // Stores output pixel
	    for (int i = 0; i < height1; i++) {
	        for (int j = 0; j < width1; j++) {
	            int rgb1 = img1.getRGB(j, i);
	            int rgb2 =0;
	            if (j < width2 && i < height2) {
	            	rgb2 = img2.getRGB(j, i);
	            }

	            int r1 = (rgb1 >> 16) & 0xff;
	            int g1 = (rgb1 >> 8) & 0xff;
	            int b1 = (rgb1) & 0xff;
	            int r2 = (rgb2 >> 16) & 0xff;
	            int g2 = (rgb2 >> 8) & 0xff;
	            int b2 = (rgb2) & 0xff;

	            diff = Math.abs(r1 - r2); // Change
	            diff += Math.abs(g1 - g2);
	            diff += Math.abs(b1 - b2);
	            diff /= 3;
	            result = (ImageVerifierConfig.isRetainOrgImage() ? result = rgb2 : (diff << 16) | (diff << 8) | diff);

	            if (diff != 0) {
	            	result = (diff << 16) | (diff << 8) | diff;
	            	if (diff != 0 && ImageVerifierConfig.getDiffMakrColor() != GREY_SCALE_MARK_COLOR) {
	            		result = ImageVerifierConfig.getDiffMakrColor();
	            	}
	            	buildOrFindRectangle(new Point(j,i));
	            }
	            outImg.setRGB(j, i, result); // Set result
	        }
	    }

	   adjustRectangles();

	    if (ImageVerifierConfig.isDrawDiffRects()) {
	    	outImg = drawDiffRectangles(outImg);
	    }

	    try {
	    	ImageIO.write(outImg, PNG_FORMAT, new File(diffFileName));
	    } catch (Exception ex) {
	    	System.err.println("Warning: Failed to write diff image file : " + diffFileName);
	    }
    }

	private BufferedImage keepRegionsOnly(BufferedImage img) {
		if (_regions == null) {
			return img;
		}

		int width = img.getWidth();
		int height = img.getHeight();

		boolean pointBounded = true;

		for (int i = 0; i < height; i++) {
	        for (int j = 0; j < width; j++) {
	            int rgb = img.getRGB(j, i);

	            pointBounded = isPointBounded(j, i);

	            if (_include) {
	            	rgb = (pointBounded ? rgb : 0xFFFFFF);
	            } else {
	            	rgb = (pointBounded ? 0xFFFFFF : rgb);
	            }
	            img.setRGB(j, i, rgb);
	        }
		}
		return img;
	}

	private boolean isPointBounded(int x, int y) {
		boolean contains = false;
		for (Rectangle rect : _regions) {
			if (rect.contains(new Point(x,y))) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	public ImageVerifierResult verify(String baseImage, String compareImage) {
		BufferedImage img1, img2;

		try {
			img1 = keepRegionsOnly(ImageIO.read(new File(baseImage)));
			img2 = keepRegionsOnly(ImageIO.read(new File(compareImage)));
		} catch (IOException e) {
			throw new RuntimeException("Failed to read image file " + baseImage + " or " + compareImage + " : " + e.getMessage());
		}


		_result.setImageArea(img1.getHeight() * img1.getWidth());

		Raster r1 = img1.getData();
		Raster r2 = img2.getData();

		DataBuffer db1 = r1.getDataBuffer();
		DataBuffer db2 = r2.getDataBuffer();

		boolean isSame = true;

		int size1 = db1.getSize();
		int size2 = db2.getSize();

		for (int i = 0; i < size1; i++ ) {
			if (i >= size2 || (db1.getElem(i) != db2.getElem(i))) {
				isSame = false;
				break;
			}
		}

		if (!isSame) {
			String diffFileName = makeDifferenceImageFileName(compareImage);
			makeDifferenceImage(img1, img2, diffFileName);
			_result.setDiffImage(diffFileName);
		}

		drawDiffRectsInCompareImage(compareImage);

		_result.setBaseImage(baseImage);
		_result.setCompareToImage(compareImage);
		_result.setDiffRects(_diffRects);

		return _result;
	}

	private void buildOrFindRectangle(Point point) {
		//Look 180 degrees around for any point offset px bound
		int left = point.x - X_SEARCH_OFFSET;
		int top = point.y - Y_SEARCH_OFFSET;

		if (left <0) left = 0;
		if (top < 0) top = 0;

		int right= point.x + X_SEARCH_OFFSET;
		int bottom = point.y;
		Rectangle boundingRectangle = null;
		for (int i=top; i<= bottom; i++) {
			for (int j=left; j<right; j++) {
				for (Rectangle rectangle : _diffRects) {
					if (rectangle.contains(j, i)) {
						boundingRectangle = rectangle;
						break;
					}
				}
			}
		}
		// If no bounding rectangle found create one
		if (boundingRectangle == null) {
			boundingRectangle = new Rectangle(point.x,point.y,1,1);
			_diffRects.add(boundingRectangle);
		}
		boundingRectangle.add(point);
	}

	private void adjustRectangles() {
		ArrayList<Rectangle> adjustedList = new ArrayList<Rectangle>();
		for (Rectangle firstLoop : _diffRects) {
			boolean add = true;
			for (Rectangle secondLoop : _diffRects) {
				if (secondLoop.contains(firstLoop) && !secondLoop.equals(firstLoop))
					add = false;
			}
			if (add) adjustedList.add(firstLoop);
		}

		boolean noChanges = false;
		while (!noChanges) {
			noChanges = true;
			int outerIndex , innerIndex;
			outerIndex = innerIndex = 0;
			Rectangle merged = null;
			for (int i=0;i <adjustedList.size(); i++) {
				Rectangle rect = adjustedList.get(i);
				for (int j=0; j<adjustedList.size(); j++) {
					Rectangle compare = adjustedList.get(j);
					if (rect.intersects(compare) && !rect.equals(compare)) {
						outerIndex = i;
						innerIndex =j;
						merged = rect.union(compare);
						noChanges = false;
						break;
					}
				}

				if (!noChanges) {
					if (outerIndex > innerIndex) {
						adjustedList.remove(innerIndex);
						adjustedList.remove(outerIndex-1);
					} else {
						adjustedList.remove(outerIndex);
						adjustedList.remove(innerIndex-1);
					}
					adjustedList.add(merged);
					break;
				}
			}
		}
		_diffRects = adjustedList;
	}

	private BufferedImage drawDiffRectangles(BufferedImage img) {
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();

		Graphics2D g2 = img.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(dashed);
		g2.setPaint(Color.MAGENTA);

		int x,y,width,height;
		for (Rectangle rectangle : _diffRects) {
			x = rectangle.x - 1;
			y = rectangle.y - 1;
			width=rectangle.width + 2;
			height=rectangle.height + 2;
			x = ( x < 0 ? 0 : x );
			y = ( y < 0 ? 0 : y );
			width = ( rectangle.width + 2 > imgWidth ? imgWidth : rectangle.width + 2 );
			height = ( rectangle.height + 2 >imgHeight ? imgHeight : rectangle.height + 2 );
			g2.drawRect(x, y, width, height);
		}
		return img;
	}

	private void drawDiffRectsInCompareImage(String compareFile) {
		try {
			BufferedImage img = ImageIO.read(new File(compareFile));
			drawDiffRectangles(img);
			ImageIO.write(img, PNG_FORMAT, new File(compareFile));
		} catch (IOException e) {
			System.err.println("Warning: Failed to draw diff rectangles on compare file.");
		}
	}
}