package ij.plugin;

import ij.*;
import ij.gui.Roi;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.Rectangle;

/**
 *
 * @author Cesar Coelho
 */
public class RGBStackConverterOpt extends RGBStackConverter {

    ImagePlus image;

    public void setImage(ImagePlus image) {
        this.image = image;
    }

    @Override
    public void run(String arg) {
        if (image == null) {
            image = IJ.getImage();
        }
        //		if (!IJ.isMacro()) keep = super.staticKeep;
        CompositeImage cimg = image.isComposite() ? (CompositeImage) image : null;
        int size = image.getStackSize();
        if ((size < 2 || size > 3) && cimg == null) {
            IJ.error("A 2 or 3 image stack, or a HyperStack, required");
            return;
        }
        int type = image.getType();
        if (cimg == null && !(type == ImagePlus.GRAY8 || type == ImagePlus.GRAY16)) {
            IJ.error("8-bit or 16-bit grayscale stack required");
            return;
        }
        if (!image.lock()) {
            return;
        }
        Undo.reset();
        String title = image.getTitle() + " (RGB)";
        if (cimg != null) {
            super.compositeToRGB(cimg, title);
        } else if (type == ImagePlus.GRAY16) {
            sixteenBitsToRGB(image);
        } else {
            ImagePlus imp2 = image.createImagePlus();
            imp2.setStack(title, image.getStack());
            ImageConverter ic = new ImageConverter(imp2);
            ic.convertRGBStackToRGB();
            imp2.show();
        }
        image.unlock();
    }

    @Override
    void sixteenBitsToRGB(ImagePlus imp) {
        Roi roi = imp.getRoi();
        Rectangle r;
        if (roi != null) {
            r = roi.getBounds();
        } else {
            r = new Rectangle(0, 0, imp.getWidth(), imp.getHeight());
        }
        ImageProcessor ip;
        ImageStack stack1 = imp.getStack();
        ImageStack stack2 = new ImageStack(r.width, r.height);
        for (int i = 1; i <= stack1.getSize(); i++) {
            ip = stack1.getProcessor(i);
            ip.setRoi(r);
            ImageProcessor ip2 = ip.crop();
            ip2 = ip2.convertToByte(true);
            stack2.addSlice(null, ip2);
        }
        imp.setStack(imp.getTitle() + " (RGB)", stack2);
        ImageConverter ic = new ImageConverter(imp);
        ic.convertRGBStackToRGB();
    }
}
