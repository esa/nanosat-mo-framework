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

    public void setImage(final ImagePlus image) {
        this.newImage = image;
    }

    public void setEqualize(final boolean equalize) {
        this.equalize = equalize;
    }

    public void setSaturated(final double d) {
        this.saturated = d;
    }

    public void setNormalize(final boolean normalize) {
        this.normalize = normalize;
    }

    public void setStackHist(final boolean b) {
        this.useStackHistogram = b;
    }

    public void setProcessStack(final boolean b) {
        this.processStack = b;
    }

    @Override
    public void run(final String arg) {
        final ImagePlus imp = newImage;
        stackSize = imp.getStackSize();
        imp.trimProcessor();

        final Roi roi = imp.getRoi();
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
            final ImageProcessor ip = imp.getProcessor();
            ip.setMinAndMax(0, ip.getBitDepth() == 32 ? 1.0 : ip.maxValue());
        }
        imp.updateAndDraw();
    }

}
