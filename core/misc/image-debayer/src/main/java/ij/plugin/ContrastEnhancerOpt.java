package ij.plugin;

import ij.*;
import ij.gui.Roi;
import ij.process.ImageProcessor;

/**
 *
 * @author Cesar Coelho
 */
public class ContrastEnhancerOpt extends ContrastEnhancer {

    ImagePlus newImage;

    public void setImage(ImagePlus image) {
        this.newImage = image;
    }

    public void setEqualize(boolean equalize) {
        this.equalize = equalize;
    }

    public void setSaturated(double d) {
        this.saturated = d;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public void setStackHist(boolean b) {
        this.useStackHistogram = b;
    }

    public void setProcessStack(boolean b) {
        this.processStack = b;
    }

    @Override
    public void run(String arg) {
        ImagePlus imp = newImage;
        stackSize = imp.getStackSize();
        imp.trimProcessor();
/*
        if (!showDialog(imp)) {
            return;
        }
*/
        Roi roi = imp.getRoi();
        if (roi != null) {
            roi.endPaste();
        }
        if (stackSize == 1) {
            Undo.setup(Undo.TRANSFORM, imp);
        } else {
            Undo.reset();
        }
        if (equalize) {
            equalize(imp);
        } else {
            stretchHistogram(imp, saturated);
        }
        if (normalize) {
            ImageProcessor ip = imp.getProcessor();
            ip.setMinAndMax(0, ip.getBitDepth() == 32 ? 1.0 : ip.maxValue());
        }
        imp.updateAndDraw();
    }

}
