package ij;

import ij.plugin.ContrastEnhancerOpt;
import ij.plugin.RGBStackConverterOpt;
import ij.plugin.filter.*;
import ij.process.*;

public class Debayer_Image implements PlugInFilter {

  ImagePlus imp;
  ImageProcessor ip;
  int width;
  int height;
  static boolean normalize = false;
  static boolean equalize = false;
  static boolean stackHist = false;
  static boolean showColour = false;
  static boolean median = false;
  static boolean gauss = false;
  static int med_radius = 2;
  static int gauss_radius = 2;
  int algorithm;

  public int setup(String arg, ImagePlus imp) {
    IJ.register(Debayer_Image.class);
    if (IJ.versionLessThan("1.32c"))
      return DONE;
    imp.unlock();
    this.imp = imp;
    this.algorithm = Integer.parseInt(arg);
    return DOES_16;
  }

  public void run(ImageProcessor ip) {
    width = imp.getWidth();
    height = imp.getHeight();
    ImageStack rgb = new ImageStack(width, height, imp.getProcessor().getColorModel());
    String[] orders = { "R-G-R-G", "B-G-B-G", "G-R-G-R", "G-B-G-B"
    };
    String[] algorithms = { "Replication", "Bilinear", "Smooth Hue", "Adaptive Smooth Hue"
    };
    String options = "";

    int row_order = 0; // Force "R-G-R-G"
    // showStack = dia.getNextBoolean();
    median = false;
    gauss = false;
    med_radius = (int) 2.0;
    gauss_radius = (int) 2.0;
    normalize = true; // optional
    equalize = false;
    stackHist = false;
    showColour = true;

    options = "saturated=0.5";
    if (normalize)
      options = options + " normalize";
    if (equalize)
      options = options + " equalize";
    options = options + " normalize_all";
    if (stackHist)
      options = options + " use";

    if (algorithm == 0)
      rgb = replicate_decode(row_order);
    else if (algorithm == 1)
      rgb = average_decode(row_order);
    else if (algorithm == 2)
      rgb = smooth_decode(row_order);
    else if (algorithm == 3)
      rgb = adaptive_decode(row_order);
    ImagePlus rgb_imp = imp.createImagePlus();
    rgb_imp.setStack("RGB Stack", rgb);
    rgb_imp.setCalibration(imp.getCalibration());
//		rgb_imp.show();
    WindowManager.setTempCurrentImage(rgb_imp);

    if (median)
      IJ.run("Median...", "radius=" + med_radius + " stack");
    if (gauss)
      IJ.run("Median...", "radius=" + gauss_radius + " stack");
//		if (normalize || equalize) IJ.run("Enhance Contrast", options);

    ContrastEnhancerOpt cont = new ContrastEnhancerOpt();
    cont.setImage(IJ.getImage());
    cont.setSaturated(0.5);
    cont.setEqualize(equalize);
    cont.setNormalize(normalize);
    cont.setStackHist(false);
    cont.setProcessStack(true);
    cont.run("");

//                if (showColour) IJ.run("Convert Stack to RGB");
    RGBStackConverterOpt obj = new RGBStackConverterOpt();
    obj.setImage(IJ.getImage());
    obj.run("");

    imp = IJ.getImage();
//                output.show();

    /*
     * BufferedImage img_buf = output.getBufferedImage(); File outputfile = new
     * File("image.png"); try { ImageIO.write(img_buf, "png", outputfile); } catch
     * (IOException ex) {
     * Logger.getLogger(Debayer_Image.class.getName()).log(Level.SEVERE, null, ex);
     * }
     */
  }

  public ImagePlus getImage() {
    return imp;
  }

  /*
   * public static Object runPlugIn(String commandName, String className, String
   * arg) { if (arg==null) arg = ""; Object thePlugIn=null; try { Class c =
   * Class.forName(className); thePlugIn = c.newInstance(); if (thePlugIn
   * instanceof PlugIn) ((PlugIn)thePlugIn).run(arg); else new
   * PlugInFilterRunner(thePlugIn, commandName, arg); } catch
   * (ClassNotFoundException e) { // if (IJ.getApplet()==null) //
   * log("Plugin or class not found: \"" + className + "\"\n(" + e+")"); } catch
   * (InstantiationException e) { // log("Unable to load plugin (ins)"); } catch
   * (IllegalAccessException e) { //
   * log("Unable to load plugin, possibly \nbecause it is not public."); } //
   * redirectErrorMessages = false; return thePlugIn; }
   */

  ImageStack replicate_decode(int row_order) { // Replication algorithm
    ip = imp.getProcessor();
    width = imp.getWidth();
    height = imp.getHeight();
    int one = 0;
    ImageStack rgb = new ImageStack(width, height, imp.getProcessor().getColorModel());
    ImageProcessor r = new ShortProcessor(width, height);
    ImageProcessor g = new ShortProcessor(width, height);
    ImageProcessor b = new ShortProcessor(width, height);
    // Short[] pixels = ip.getPixels();

    if (row_order == 0 || row_order == 1) {
      for (int y = 0; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          one = ip.getPixel(x, y);
          b.putPixel(x, y, one);
          b.putPixel(x + 1, y, one);
          b.putPixel(x, y + 1, one);
          b.putPixel(x + 1, y + 1, one);
        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          one = ip.getPixel(x, y);
          r.putPixel(x, y, one);
          r.putPixel(x + 1, y, one);
          r.putPixel(x, y + 1, one);
          r.putPixel(x + 1, y + 1, one);
        }
      }

      for (int y = 0; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          one = ip.getPixel(x, y);
          g.putPixel(x, y, one);
          g.putPixel(x + 1, y, one);
        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          one = ip.getPixel(x, y);
          g.putPixel(x, y, one);
          g.putPixel(x + 1, y, one);
        }
      }

      if (row_order == 0) {
        rgb.addSlice("red", b);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", r);
      } else if (row_order == 1) {
        rgb.addSlice("red", r);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", b);
      }
    }

    else if (row_order == 2 || row_order == 3) {
      for (int y = 1; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          one = ip.getPixel(x, y);
          b.putPixel(x, y, one);
          b.putPixel(x + 1, y, one);
          b.putPixel(x, y + 1, one);
          b.putPixel(x + 1, y + 1, one);
        }
      }

      for (int y = 0; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          one = ip.getPixel(x, y);
          r.putPixel(x, y, one);
          r.putPixel(x + 1, y, one);
          r.putPixel(x, y + 1, one);
          r.putPixel(x + 1, y + 1, one);
        }
      }

      for (int y = 0; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          one = ip.getPixel(x, y);
          g.putPixel(x, y, one);
          g.putPixel(x + 1, y, one);
        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          one = ip.getPixel(x, y);
          g.putPixel(x, y, one);
          g.putPixel(x + 1, y, one);
        }
      }

      if (row_order == 2) {
        rgb.addSlice("red", b);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", r);
      } else if (row_order == 3) {
        rgb.addSlice("red", r);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", b);
      }
    }

    return rgb;

  }

  ImageStack average_decode(int row_order) { // Bilinear algorithm
    ip = imp.getProcessor();
    width = imp.getWidth();
    height = imp.getHeight();
    int one = 0;
    int two = 0;
    int three = 0;
    int four = 0;
    ImageStack rgb = new ImageStack(width, height, imp.getProcessor().getColorModel());
    ImageProcessor r = new ShortProcessor(width, height);
    ImageProcessor g = new ShortProcessor(width, height);
    ImageProcessor b = new ShortProcessor(width, height);
    // Short[] pixels = ip.getPixels();

    if (row_order == 0 || row_order == 1) {
      for (int y = 0; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          one = ip.getPixel(x, y);
          two = ip.getPixel(x + 2, y);
          three = ip.getPixel(x, y + 2);
          four = ip.getPixel(x + 2, y + 2);

          b.putPixel(x, y, one);
          b.putPixel(x + 1, y, (one + two) / 2);
          b.putPixel(x, y + 1, (one + three) / 2);
          b.putPixel(x + 1, y + 1, (one + two + three + four) / 4);
        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          one = ip.getPixel(x, y);
          two = ip.getPixel(x + 2, y);
          three = ip.getPixel(x, y + 2);
          four = ip.getPixel(x + 2, y + 2);

          r.putPixel(x, y, one);
          r.putPixel(x + 1, y, (one + two) / 2);
          r.putPixel(x, y + 1, (one + three) / 2);
          r.putPixel(x + 1, y + 1, (one + two + three + four) / 4);
        }
      }

      for (int y = 0; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          one = ip.getPixel(x, y);
          two = ip.getPixel(x + 2, y);
          three = ip.getPixel(x + 1, y + 1);
          four = ip.getPixel(x + 1, y - 1);

          g.putPixel(x, y, one);
          g.putPixel(x + 1, y, (one + two + three + four) / 4);
        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          one = ip.getPixel(x, y);
          two = ip.getPixel(x + 2, y);
          three = ip.getPixel(x + 1, y + 1);
          four = ip.getPixel(x + 1, y - 1);

          g.putPixel(x, y, one);
          g.putPixel(x + 1, y, (one + two + three + four) / 4);
        }
      }

      if (row_order == 0) {
        rgb.addSlice("red", b);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", r);
      } else if (row_order == 1) {
        rgb.addSlice("red", r);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", b);
      }
    }

    else if (row_order == 2 || row_order == 3) {
      for (int y = 1; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          one = ip.getPixel(x, y);
          two = ip.getPixel(x + 2, y);
          three = ip.getPixel(x, y + 2);
          four = ip.getPixel(x + 2, y + 2);

          b.putPixel(x, y, one);
          b.putPixel(x + 1, y, (one + two) / 2);
          b.putPixel(x, y + 1, (one + three) / 2);
          b.putPixel(x + 1, y + 1, (one + two + three + four) / 4);
        }
      }

      for (int y = 0; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          one = ip.getPixel(x, y);
          two = ip.getPixel(x + 2, y);
          three = ip.getPixel(x, y + 2);
          four = ip.getPixel(x + 2, y + 2);

          r.putPixel(x, y, one);
          r.putPixel(x + 1, y, (one + two) / 2);
          r.putPixel(x, y + 1, (one + three) / 2);
          r.putPixel(x + 1, y + 1, (one + two + three + four) / 4);
        }
      }

      for (int y = 0; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          one = ip.getPixel(x, y);
          two = ip.getPixel(x + 2, y);
          three = ip.getPixel(x + 1, y + 1);
          four = ip.getPixel(x + 1, y - 1);

          g.putPixel(x, y, one);
          g.putPixel(x + 1, y, (one + two + three + four) / 4);
        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          one = ip.getPixel(x, y);
          two = ip.getPixel(x + 2, y);
          three = ip.getPixel(x + 1, y + 1);
          four = ip.getPixel(x + 1, y - 1);

          g.putPixel(x, y, one);
          g.putPixel(x + 1, y, (one + two + three + four) / 4);
        }
      }

      if (row_order == 2) {
        rgb.addSlice("red", b);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", r);
      } else if (row_order == 3) {
        rgb.addSlice("red", r);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", b);
      }
    }

    return rgb;

  }

  ImageStack smooth_decode(int row_order) { // Smooth Hue algorithm
    ip = imp.getProcessor();
    width = imp.getWidth();
    height = imp.getHeight();
    double G1 = 0;
    double G2 = 0;
    double G3 = 0;
    double G4 = 0;
    double G5 = 0;
    double G6 = 0;
    double G7 = 0;
    double G8 = 0;
    double G9 = 0;
    double B1 = 0;
    double B2 = 0;
    double B3 = 0;
    double B4 = 0;
    double R1 = 0;
    double R2 = 0;
    double R3 = 0;
    double R4 = 0;
    ImageStack rgb = new ImageStack(width, height, imp.getProcessor().getColorModel());
    ImageProcessor r = new ShortProcessor(width, height);
    ImageProcessor g = new ShortProcessor(width, height);
    ImageProcessor b = new ShortProcessor(width, height);
    // Short[] pixels = ip.getPixels();

    if (row_order == 0 || row_order == 1) {
      // Solve for green pixels first
      for (int y = 0; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          G1 = ip.getPixel(x, y);
          G2 = ip.getPixel(x + 2, y);
          G3 = ip.getPixel(x + 1, y + 1);
          G4 = ip.getPixel(x + 1, y - 1);

          g.putPixel(x, y, (int) G1);
          if (y == 0)
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3) / 3));
          else
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3 + G4) / 4));
          if (x == 1)
            g.putPixel(x - 1, y, (int) ((G1 + G4 + ip.getPixel(x - 1, y + 1)) / 3));
        }
      }

      for (int x = 0; x < width; x += 2) {
        for (int y = 1; y < height; y += 2) {

          G1 = ip.getPixel(x, y);
          G2 = ip.getPixel(x + 2, y);
          G3 = ip.getPixel(x + 1, y + 1);
          G4 = ip.getPixel(x + 1, y - 1);

          g.putPixel(x, y, (int) G1);
          if (x == 0)
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3) / 3));
          else
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3 + G4) / 4));
        }
      }

      g.putPixel(0, 0, (int) ((ip.getPixel(0, 1) + ip.getPixel(1, 0)) / 2));

      for (int y = 0; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          B1 = ip.getPixel(x, y);
          B2 = ip.getPixel(x + 2, y);
          B3 = ip.getPixel(x, y + 2);
          B4 = ip.getPixel(x + 2, y + 2);
          G1 = g.getPixel(x, y);
          G2 = g.getPixel(x + 2, y);
          G3 = g.getPixel(x, y + 2);
          G4 = g.getPixel(x + 2, y + 2);
          G5 = g.getPixel(x + 1, y);
          G6 = g.getPixel(x, y + 1);
          G9 = g.getPixel(x + 1, y + 1);
          if (G1 == 0)
            G1 = 1;
          if (G2 == 0)
            G2 = 1;
          if (G3 == 0)
            G3 = 1;
          if (G4 == 0)
            G4 = 1;

          b.putPixel(x, y, (int) (B1));
          b.putPixel(x + 1, y, (int) ((G5 / 2 * ((B1 / G1) + (B2 / G2)))));
          b.putPixel(x, y + 1, (int) ((G6 / 2 * ((B1 / G1) + (B3 / G3)))));
          b.putPixel(x + 1, y + 1,
              (int) ((G9 / 4 * ((B1 / G1) + (B3 / G3) + (B2 / G2) + (B4 / G4)))));

        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          R1 = ip.getPixel(x, y);
          R2 = ip.getPixel(x + 2, y);
          R3 = ip.getPixel(x, y + 2);
          R4 = ip.getPixel(x + 2, y + 2);
          G1 = g.getPixel(x, y);
          G2 = g.getPixel(x + 2, y);
          G3 = g.getPixel(x, y + 2);
          G4 = g.getPixel(x + 2, y + 2);
          G5 = g.getPixel(x + 1, y);
          G6 = g.getPixel(x, y + 1);
          G9 = g.getPixel(x + 1, y + 1);
          if (G1 == 0)
            G1 = 1;
          if (G2 == 0)
            G2 = 1;
          if (G3 == 0)
            G3 = 1;
          if (G4 == 0)
            G4 = 1;

          r.putPixel(x, y, (int) (R1));
          r.putPixel(x + 1, y, (int) ((G5 / 2 * ((R1 / G1) + (R2 / G2)))));
          r.putPixel(x, y + 1, (int) ((G6 / 2 * ((R1 / G1) + (R3 / G3)))));
          r.putPixel(x + 1, y + 1,
              (int) ((G9 / 4 * ((R1 / G1) + (R3 / G3) + (R2 / G2) + (R4 / G4)))));
        }
      }

      if (row_order == 0) {
        rgb.addSlice("red", b);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", r);
      } else if (row_order == 1) {
        rgb.addSlice("red", r);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", b);
      }
    }

    else if (row_order == 2 || row_order == 3) {

      for (int y = 0; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          G1 = ip.getPixel(x, y);
          G2 = ip.getPixel(x + 2, y);
          G3 = ip.getPixel(x + 1, y + 1);
          G4 = ip.getPixel(x + 1, y - 1);

          g.putPixel(x, y, (int) G1);
          if (y == 0)
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3) / 3));
          else
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3 + G4) / 4));
          if (x == 1)
            g.putPixel(x - 1, y, (int) ((G1 + G4 + ip.getPixel(x - 1, y + 1)) / 3));
        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          G1 = ip.getPixel(x, y);
          G2 = ip.getPixel(x + 2, y);
          G3 = ip.getPixel(x + 1, y + 1);
          G4 = ip.getPixel(x + 1, y - 1);

          g.putPixel(x, y, (int) G1);
          if (x == 0)
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3) / 3));
          else
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3 + G4) / 4));
        }
      }

      g.putPixel(0, 0, (int) ((ip.getPixel(0, 1) + ip.getPixel(1, 0)) / 2));

      for (int y = 1; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          B1 = ip.getPixel(x, y);
          B2 = ip.getPixel(x + 2, y);
          B3 = ip.getPixel(x, y + 2);
          B4 = ip.getPixel(x + 2, y + 2);
          G1 = g.getPixel(x, y);
          G2 = g.getPixel(x + 2, y);
          G3 = g.getPixel(x, y + 2);
          G4 = g.getPixel(x + 2, y + 2);
          G5 = g.getPixel(x + 1, y);
          G6 = g.getPixel(x, y + 1);
          G9 = g.getPixel(x + 1, y + 1);
          if (G1 == 0)
            G1 = 1;
          if (G2 == 0)
            G2 = 1;
          if (G3 == 0)
            G3 = 1;
          if (G4 == 0)
            G4 = 1;

          b.putPixel(x, y, (int) (B1));
          b.putPixel(x + 1, y, (int) ((G5 / 2 * ((B1 / G1) + (B2 / G2)))));
          b.putPixel(x, y + 1, (int) ((G6 / 2 * ((B1 / G1) + (B3 / G3)))));
          b.putPixel(x + 1, y + 1,
              (int) ((G9 / 4 * ((B1 / G1) + (B3 / G3) + (B2 / G2) + (B4 / G4)))));

        }
      }

      for (int y = 0; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          R1 = ip.getPixel(x, y);
          R2 = ip.getPixel(x + 2, y);
          R3 = ip.getPixel(x, y + 2);
          R4 = ip.getPixel(x + 2, y + 2);
          G1 = g.getPixel(x, y);
          G2 = g.getPixel(x + 2, y);
          G3 = g.getPixel(x, y + 2);
          G4 = g.getPixel(x + 2, y + 2);
          G5 = g.getPixel(x + 1, y);
          G6 = g.getPixel(x, y + 1);
          G9 = g.getPixel(x + 1, y + 1);
          if (G1 == 0)
            G1 = 1;
          if (G2 == 0)
            G2 = 1;
          if (G3 == 0)
            G3 = 1;
          if (G4 == 0)
            G4 = 1;

          r.putPixel(x, y, (int) (R1));
          r.putPixel(x + 1, y, (int) ((G5 / 2 * ((R1 / G1) + (R2 / G2)))));
          r.putPixel(x, y + 1, (int) ((G6 / 2 * ((R1 / G1) + (R3 / G3)))));
          r.putPixel(x + 1, y + 1,
              (int) ((G9 / 4 * ((R1 / G1) + (R3 / G3) + (R2 / G2) + (R4 / G4)))));
        }
      }

      if (row_order == 2) {
        rgb.addSlice("red", b);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", r);
      } else if (row_order == 3) {
        rgb.addSlice("red", r);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", b);
      }
    }

    return rgb;

  }

  ImageStack adaptive_decode(int row_order) { // Adaptive Smooth Hue algorithm (Edge detecting)
    ip = imp.getProcessor();
    width = imp.getWidth();
    height = imp.getHeight();
    double G1 = 0;
    double G2 = 0;
    double G3 = 0;
    double G4 = 0;
    double G5 = 0;
    double G6 = 0;
    double G7 = 0;
    double G8 = 0;
    double G9 = 0;
    double B1 = 0;
    double B2 = 0;
    double B3 = 0;
    double B4 = 0;
    double B5 = 0;
    double R1 = 0;
    double R2 = 0;
    double R3 = 0;
    double R4 = 0;
    double R5 = 0;
    double N = 0;
    double S = 0;
    double E = 0;
    double W = 0;
    ImageStack rgb = new ImageStack(width, height, imp.getProcessor().getColorModel());
    ImageProcessor r = new ShortProcessor(width, height);
    ImageProcessor g = new ShortProcessor(width, height);
    ImageProcessor b = new ShortProcessor(width, height);
    // Short[] pixels = ip.getPixels();

    if (row_order == 0 || row_order == 1) {
      // Solve for green pixels first
      for (int y = 0; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          G1 = ip.getPixel(x, y);
          G2 = ip.getPixel(x + 2, y);
          G3 = ip.getPixel(x + 1, y + 1);
          G4 = ip.getPixel(x + 1, y - 1);
          R1 = ip.getPixel(x - 1, y);
          R2 = ip.getPixel(x + 3, y);
          R3 = ip.getPixel(x + 1, y + 2);
          R4 = ip.getPixel(x + 1, y - 2);
          R5 = ip.getPixel(x + 1, y + 1);

          N = Math.abs(R4 - R5) * 2 + Math.abs(G4 - G3);
          S = Math.abs(R5 - R3) * 2 + Math.abs(G4 - G3);
          E = Math.abs(R5 - R2) * 2 + Math.abs(G1 - G2);
          W = Math.abs(R1 - R5) * 2 + Math.abs(G1 - G2);

          if (N < S && N < E && N < W) {
            g.putPixel(x + 1, y, (int) ((G4 * 3 + R5 + G3 - R4) / 4));
          }

          else if (S < N && S < E && S < W) {
            g.putPixel(x + 1, y, (int) ((G3 * 3 + R5 + G4 - R3) / 4));
          }

          else if (W < N && W < E && W < S) {
            g.putPixel(x + 1, y, (int) ((G1 * 3 + R5 + G2 - R1) / 4));
          }

          else if (E < N && E < S && E < W) {
            g.putPixel(x + 1, y, (int) ((G2 * 3 + R5 + G1 - R2) / 4));
          }

          g.putPixel(x, y, (int) G1);

          if (y == 0)
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3) / 3));
          else
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3 + G4) / 4));
          if (x == 1)
            g.putPixel(x - 1, y, (int) ((G1 + G4 + ip.getPixel(x - 1, y + 1)) / 3));
        }
      }

      for (int x = 0; x < width; x += 2) {
        for (int y = 1; y < height; y += 2) {
          G1 = ip.getPixel(x, y);
          G2 = ip.getPixel(x + 2, y);
          G3 = ip.getPixel(x + 1, y + 1);
          G4 = ip.getPixel(x + 1, y - 1);
          R1 = ip.getPixel(x - 1, y);
          R2 = ip.getPixel(x + 3, y);
          R3 = ip.getPixel(x + 1, y + 2);
          R4 = ip.getPixel(x + 1, y - 2);
          R5 = ip.getPixel(x + 1, y + 1);

          N = Math.abs(R4 - R5) * 2 + Math.abs(G4 - G3);
          S = Math.abs(R5 - R3) * 2 + Math.abs(G4 - G3);
          E = Math.abs(R5 - R2) * 2 + Math.abs(G1 - G2);
          W = Math.abs(R1 - R5) * 2 + Math.abs(G1 - G2);

          if (N < S && N < E && N < W) {
            g.putPixel(x + 1, y, (int) ((G4 * 3 + R5 + G3 - R4) / 4));
          }

          else if (S < N && S < E && S < W) {
            g.putPixel(x + 1, y, (int) ((G3 * 3 + R5 + G4 - R3) / 4));
          }

          else if (W < N && W < E && W < S) {
            g.putPixel(x + 1, y, (int) ((G1 * 3 + R5 + G2 - R1) / 4));
          }

          else if (E < N && E < S && E < W) {
            g.putPixel(x + 1, y, (int) ((G2 * 3 + R5 + G1 - R2) / 4));
          }

          g.putPixel(x, y, (int) G1);
          if (x == 0)
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3) / 3));
          else
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3 + G4) / 4));
        }
      }

      g.putPixel(0, 0, (int) ((ip.getPixel(0, 1) + ip.getPixel(1, 0)) / 2));

      for (int y = 0; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          B1 = ip.getPixel(x, y);
          B2 = ip.getPixel(x + 2, y);
          B3 = ip.getPixel(x, y + 2);
          B4 = ip.getPixel(x + 2, y + 2);
          G1 = g.getPixel(x, y);
          G2 = g.getPixel(x + 2, y);
          G3 = g.getPixel(x, y + 2);
          G4 = g.getPixel(x + 2, y + 2);
          G5 = g.getPixel(x + 1, y);
          G6 = g.getPixel(x, y + 1);
          G9 = g.getPixel(x + 1, y + 1);
          if (G1 == 0)
            G1 = 1;
          if (G2 == 0)
            G2 = 1;
          if (G3 == 0)
            G3 = 1;
          if (G4 == 0)
            G4 = 1;

          b.putPixel(x, y, (int) (B1));
          b.putPixel(x + 1, y, (int) ((G5 / 2 * ((B1 / G1) + (B2 / G2)))));
          b.putPixel(x, y + 1, (int) ((G6 / 2 * ((B1 / G1) + (B3 / G3)))));
          b.putPixel(x + 1, y + 1,
              (int) ((G9 / 4 * ((B1 / G1) + (B3 / G3) + (B2 / G2) + (B4 / G4)))));

        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          R1 = ip.getPixel(x, y);
          R2 = ip.getPixel(x + 2, y);
          R3 = ip.getPixel(x, y + 2);
          R4 = ip.getPixel(x + 2, y + 2);
          G1 = g.getPixel(x, y);
          G2 = g.getPixel(x + 2, y);
          G3 = g.getPixel(x, y + 2);
          G4 = g.getPixel(x + 2, y + 2);
          G5 = g.getPixel(x + 1, y);
          G6 = g.getPixel(x, y + 1);
          G9 = g.getPixel(x + 1, y + 1);
          if (G1 == 0)
            G1 = 1;
          if (G2 == 0)
            G2 = 1;
          if (G3 == 0)
            G3 = 1;
          if (G4 == 0)
            G4 = 1;

          r.putPixel(x, y, (int) (R1));
          r.putPixel(x + 1, y, (int) ((G5 / 2 * ((R1 / G1) + (R2 / G2)))));
          r.putPixel(x, y + 1, (int) ((G6 / 2 * ((R1 / G1) + (R3 / G3)))));
          r.putPixel(x + 1, y + 1,
              (int) ((G9 / 4 * ((R1 / G1) + (R3 / G3) + (R2 / G2) + (R4 / G4)))));
        }
      }

      if (row_order == 0) {
        rgb.addSlice("red", b);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", r);
      } else if (row_order == 1) {
        rgb.addSlice("red", r);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", b);
      }
    }

    else if (row_order == 2 || row_order == 3) {

      for (int y = 0; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          G1 = ip.getPixel(x, y);
          G2 = ip.getPixel(x + 2, y);
          G3 = ip.getPixel(x + 1, y + 1);
          G4 = ip.getPixel(x + 1, y - 1);
          R1 = ip.getPixel(x - 1, y);
          R2 = ip.getPixel(x + 3, y);
          R3 = ip.getPixel(x + 1, y + 2);
          R4 = ip.getPixel(x + 1, y - 2);
          R5 = ip.getPixel(x + 1, y + 1);

          N = Math.abs(R4 - R5) * 2 + Math.abs(G4 - G3);
          S = Math.abs(R5 - R3) * 2 + Math.abs(G4 - G3);
          E = Math.abs(R5 - R2) * 2 + Math.abs(G1 - G2);
          W = Math.abs(R1 - R5) * 2 + Math.abs(G1 - G2);

          if (N < S && N < E && N < W) {
            g.putPixel(x + 1, y, (int) ((G4 * 3 + R5 + G3 - R4) / 4));
          }

          else if (S < N && S < E && S < W) {
            g.putPixel(x + 1, y, (int) ((G3 * 3 + R5 + G4 - R3) / 4));
          }

          else if (W < N && W < E && W < S) {
            g.putPixel(x + 1, y, (int) ((G1 * 3 + R5 + G2 - R1) / 4));
          }

          else if (E < N && E < S && E < W) {
            g.putPixel(x + 1, y, (int) ((G2 * 3 + R5 + G1 - R2) / 4));
          }

          g.putPixel(x, y, (int) G1);
          if (y == 0)
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3) / 3));
          else
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3 + G4) / 4));
          if (x == 1)
            g.putPixel(x - 1, y, (int) ((G1 + G4 + ip.getPixel(x - 1, y + 1)) / 3));
        }
      }

      for (int y = 1; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          G1 = ip.getPixel(x, y);
          G2 = ip.getPixel(x + 2, y);
          G3 = ip.getPixel(x + 1, y + 1);
          G4 = ip.getPixel(x + 1, y - 1);
          R1 = ip.getPixel(x - 1, y);
          R2 = ip.getPixel(x + 3, y);
          R3 = ip.getPixel(x + 1, y + 2);
          R4 = ip.getPixel(x + 1, y - 2);
          R5 = ip.getPixel(x + 1, y + 1);

          N = Math.abs(R4 - R5) * 2 + Math.abs(G4 - G3);
          S = Math.abs(R5 - R3) * 2 + Math.abs(G4 - G3);
          E = Math.abs(R5 - R2) * 2 + Math.abs(G1 - G2);
          W = Math.abs(R1 - R5) * 2 + Math.abs(G1 - G2);

          if (N < S && N < E && N < W) {
            g.putPixel(x + 1, y, (int) ((G4 * 3 + R5 + G3 - R4) / 4));
          }

          else if (S < N && S < E && S < W) {
            g.putPixel(x + 1, y, (int) ((G3 * 3 + R5 + G4 - R3) / 4));
          }

          else if (W < N && W < E && W < S) {
            g.putPixel(x + 1, y, (int) ((G1 * 3 + R5 + G2 - R1) / 4));
          }

          else if (E < N && E < S && E < W) {
            g.putPixel(x + 1, y, (int) ((G2 * 3 + R5 + G1 - R2) / 4));
          }

          g.putPixel(x, y, (int) G1);
          if (x == 0)
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3) / 3));
          else
            g.putPixel(x + 1, y, (int) ((G1 + G2 + G3 + G4) / 4));
        }
      }

      g.putPixel(0, 0, (int) ((ip.getPixel(0, 1) + ip.getPixel(1, 0)) / 2));

      for (int y = 1; y < height; y += 2) {
        for (int x = 0; x < width; x += 2) {
          B1 = ip.getPixel(x, y);
          B2 = ip.getPixel(x + 2, y);
          B3 = ip.getPixel(x, y + 2);
          B4 = ip.getPixel(x + 2, y + 2);
          G1 = g.getPixel(x, y);
          G2 = g.getPixel(x + 2, y);
          G3 = g.getPixel(x, y + 2);
          G4 = g.getPixel(x + 2, y + 2);
          G5 = g.getPixel(x + 1, y);
          G6 = g.getPixel(x, y + 1);
          G9 = g.getPixel(x + 1, y + 1);
          if (G1 == 0)
            G1 = 1;
          if (G2 == 0)
            G2 = 1;
          if (G3 == 0)
            G3 = 1;
          if (G4 == 0)
            G4 = 1;

          b.putPixel(x, y, (int) (B1));
          b.putPixel(x + 1, y, (int) ((G5 / 2 * ((B1 / G1) + (B2 / G2)))));
          b.putPixel(x, y + 1, (int) ((G6 / 2 * ((B1 / G1) + (B3 / G3)))));
          b.putPixel(x + 1, y + 1,
              (int) ((G9 / 4 * ((B1 / G1) + (B3 / G3) + (B2 / G2) + (B4 / G4)))));

        }
      }

      for (int y = 0; y < height; y += 2) {
        for (int x = 1; x < width; x += 2) {
          R1 = ip.getPixel(x, y);
          R2 = ip.getPixel(x + 2, y);
          R3 = ip.getPixel(x, y + 2);
          R4 = ip.getPixel(x + 2, y + 2);
          G1 = g.getPixel(x, y);
          G2 = g.getPixel(x + 2, y);
          G3 = g.getPixel(x, y + 2);
          G4 = g.getPixel(x + 2, y + 2);
          G5 = g.getPixel(x + 1, y);
          G6 = g.getPixel(x, y + 1);
          G9 = g.getPixel(x + 1, y + 1);
          if (G1 == 0)
            G1 = 1;
          if (G2 == 0)
            G2 = 1;
          if (G3 == 0)
            G3 = 1;
          if (G4 == 0)
            G4 = 1;

          r.putPixel(x, y, (int) (R1));
          r.putPixel(x + 1, y, (int) ((G5 / 2 * ((R1 / G1) + (R2 / G2)))));
          r.putPixel(x, y + 1, (int) ((G6 / 2 * ((R1 / G1) + (R3 / G3)))));
          r.putPixel(x + 1, y + 1,
              (int) ((G9 / 4 * ((R1 / G1) + (R3 / G3) + (R2 / G2) + (R4 / G4)))));
        }
      }

      if (row_order == 2) {
        rgb.addSlice("red", b);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", r);
      } else if (row_order == 3) {
        rgb.addSlice("red", r);
        rgb.addSlice("green", g);
        rgb.addSlice("blue", b);
      }
    }

    return rgb;

  }

}
