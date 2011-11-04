/***************************************************************************************
 * Copyright (C) 2011 by 52 North Initiative for Geospatial Open Source Software GmbH  *
 *                                                                                     *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source *
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org *
 *                                                                                     *
 * This program is free software; you can redistribute and/or modify it under the      *
 * terms of the GNU General Public License version 2 as published by the Free Software *
 * Foundation.                                                                         *
 *                                                                                     *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied WARRANTY *
 * OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public  *
 * License for more details.                                                           *
 *                                                                                     *
 * You should have received a copy of the GNU General Public License along with this   *
 * program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc.,  *
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA, or visit the Free Software *
 * Foundation web page, http://www.fsf.org.                                            *
 **************************************************************************************/
package org.n52.v3d.triturus.t3dutil.operatingsystem;

import org.n52.v3d.triturus.core.T3dNotYetImplException;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;

import javax.swing.ImageIcon;

/**
 * Class holding various helper methods to work with image files.<br /><br />
 * <i>German:</i> Klasse mit verschiedenen Hilfsmethoden f&uuml;r die Arbeit mit Bilddateien. Aktuell enth&auml;lt die
 * Klasse verschiedene Image-I/O-Methoden, zuk�nftige Erweiterungen sind vorgesehen.
 * @author Torsten Heinen
 */
public class ImageTools
{
    /**
     * todo engl. JavaDoc
     * konvertiert ein awt.Image in ein BufferedImage
     *
     * @param image
     * @return Buffered image object
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

	/**
	 * todo engl. JavaDoc
     * produziert ein BufferedImage aus den �bergebenen Werten.<p>
	 * Das Array speichert f�r jeden Pixel die RGB(A) Komponenten in eigenen Speicherb�nken:
     * <pre>
	 * [r0][p0][p1]...
	 * [g0][p0][p1]...
	 * [b0][p0][p1]...
	 * [a0][p0][p1]...
     * </pre>
	 * <tt>imageData.length</tt> muss die Anzahl der Komponenten zur�ckgeben.<p>
     * <i>Bem.: Die Generierung klappt meistens, aber nicht immer -> noch ein wenig buggy.</i><p>
	 * @return Repr�sentation des Daten-Arrays
	 */
    public static BufferedImage createBufferedImage(int width, int height, boolean withAlpha, byte[] imageData) {
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ColorModel colorModel = new ComponentColorModel(
            colorSpace, withAlpha, false,
            Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

        int scanlineStride, pixelStride, dataElements;
        int[] bandoffsets;
        if (withAlpha) {
            scanlineStride = width * 4;
            dataElements = pixelStride = 4;
            bandoffsets = new int[]{0,1,2,3};
        }
        else {
            scanlineStride = width * 3;
            dataElements = pixelStride = 3;
            bandoffsets = new int[]{0,1,2};
        }

        return new BufferedImage(
                colorModel,
                Raster.createWritableRaster(
                        new PixelInterleavedSampleModel(
                                DataBuffer.TYPE_BYTE, //data type
                                width, height,
                                pixelStride,
                                scanlineStride,
                                bandoffsets),
                        new DataBufferByte(
                                imageData,
                                dataElements),
                        null), // Location default 0,0
                false, // isRasterPremultiplied
                new Hashtable()); // hashtable properties ?
    }

    /**
     * @param i
     * @param j
     * @param b
     * @param buffer
     * @return Buffered image object
     */
    public static BufferedImage createBufferedImage(int i, int j, boolean b, ByteBuffer buffer) {
        return createBufferedImage(i, j, b, buffer.array());
    }

    public static BufferedImage createBufferedImage(int width, int height, boolean withAlpha, byte[][] imageData)
    {
        throw new T3dNotYetImplException("wird das ben�tigt?");
    }

    /**
     * todo: Javadoc
     * @param imageLocation
     * @return Buffered image object
     */
    public static final BufferedImage loadImage(String imageLocation)
    {
        BufferedImage bufferedImage = null;
        ClassLoader  fileLoader  = ClassLoader.getSystemClassLoader();
        InputStream  input  = fileLoader.getResourceAsStream(imageLocation);

        try {
            if(input == null)
                input =  new FileInputStream(imageLocation);
            bufferedImage = javax.imageio.ImageIO.read(input);
        }
        catch( Exception e){
            try{
                bufferedImage = javax.imageio.ImageIO.read(new java.io.File(imageLocation));
            }catch( Exception newE){}
        }
        return bufferedImage;
	}
}
