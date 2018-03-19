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

    ImagePlus newImage;

    public void setImage(ImagePlus image) {
        this.newImage = image;
    }

    @Override
    public void run(String arg) {
        ImagePlus imp = newImage;
        if (imp == null) {
            imp = IJ.getImage();
        }
//		if (!IJ.isMacro()) keep = super.staticKeep;
        CompositeImage cimg = imp.isComposite() ? (CompositeImage) imp : null;
        int size = imp.getStackSize();
        if ((size < 2 || size > 3) && cimg == null) {
            IJ.error("A 2 or 3 image stack, or a HyperStack, required");
            return;
        }
        int type = imp.getType();
        if (cimg == null && !(type == ImagePlus.GRAY8 || type == ImagePlus.GRAY16)) {
            IJ.error("8-bit or 16-bit grayscale stack required");
            return;
        }
        if (!imp.lock()) {
            return;
        }
        Undo.reset();
        String title = imp.getTitle() + " (RGB)";
        if (cimg != null) {
            super.compositeToRGB(cimg, title);
        } else if (type == ImagePlus.GRAY16) {
            sixteenBitsToRGB(imp);
        } else {
            ImagePlus imp2 = imp.createImagePlus();
            imp2.setStack(title, imp.getStack());
            ImageConverter ic = new ImageConverter(imp2);
            ic.convertRGBStackToRGB();
            imp2.show();
        }
        imp.unlock();
    }

    @Override
    void sixteenBitsToRGB(ImagePlus imp) {
        Roi roi = imp.getRoi();
        int width, height;
        Rectangle r;
        if (roi != null) {
            r = roi.getBounds();
            width = r.width;
            height = r.height;
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
        ImagePlus imp2 = imp.createImagePlus();
        imp2.setStack(imp.getTitle() + " (RGB)", stack2);
        ImageConverter ic = new ImageConverter(imp2);
        ic.convertRGBStackToRGB();
        WindowManager.setTempCurrentImage(imp2);
//		imp2.show();
    }
}
