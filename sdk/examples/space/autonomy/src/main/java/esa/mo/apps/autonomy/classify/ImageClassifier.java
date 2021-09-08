package esa.mo.apps.autonomy.classify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

/**
 * ImageClassifier is used for cloudiness and interest classification
 */
public class ImageClassifier {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(ImageClassifier.class.getName());

    private static  final int IMAGE_SIZE = 128; // Downsizing height/width

    private static final String DEFAULT_CLOUD_MODEL_FILE = "classifier-models" + File.separator + "cloud_model.h5";
    private static final String DEFAULT_INTERESTING_MODEL_FILE = "classifier-models" + File.separator + "interesting_model.h5";

    private static String cloudModelPath = null;
    private static String interstingModelPath = null;

    private ImageClassifier() {}

    /**
     * Reads configuration properties and checks for required classification model files
     */
    public static void init() {
        cloudModelPath = System.getProperty("classifier.cloud.model", DEFAULT_CLOUD_MODEL_FILE);
        File cloudModelFile = new File(cloudModelPath);
        if (!cloudModelFile.exists()) {
            LOGGER.log(Level.SEVERE, "Cloud classifier model not found from " + cloudModelFile.getAbsolutePath());
            System.exit(1);
        } else {
            LOGGER.info("Cloud classifier model loaded from " + cloudModelFile.getAbsolutePath());
        }
        interstingModelPath = System.getProperty("classifier.interesting.model", DEFAULT_INTERESTING_MODEL_FILE);
        File interestingModelFile = new File(interstingModelPath);
        if (!interestingModelFile.exists()) {
            LOGGER.log(Level.SEVERE, "Interesting classifier model not found from " + interestingModelFile.getAbsolutePath());
            System.exit(1);
        } else {
            LOGGER.info("Interesting classifier model loaded from " + interestingModelFile.getAbsolutePath());
        }
    }

    /**
     * Finds whether the given image is clear or cloudy.
     * @param imagePath path for input image
     * @return the result of the classification. An error enum is returned in case of exception during classification.
     */
    public static CloudyClassification classifyClouds(String imagePath) {
        File image = new File(imagePath);
        LOGGER.info("Classifying clear image from " + image.getAbsolutePath());

        try {
            Instant start = Instant.now();

            byte[] imageBytes = Files.readAllBytes(image.toPath());

            // create the keras network model by importing the model with the h5 file
            MultiLayerNetwork cloudModel = KerasModelImport.importKerasSequentialModelAndWeights(cloudModelPath);

            // use the nativeImageLoader to downsize the image and convert to numerical matrix
            NativeImageLoader loader = new NativeImageLoader(IMAGE_SIZE, IMAGE_SIZE, 3);

            // get the byte input stream
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            // read image as a numpy matrix
            INDArray imageMatrix = loader.asMatrix(inputStream);
            // values need to be scaled between 0 and 1
            DataNormalization scalar = new ImagePreProcessingScaler(0, 1);
            // then call that scalar on the image dataset
            scalar.transform(imageMatrix);

            // pass through neural net and store it in output array
            INDArray output = cloudModel.output(imageMatrix);

            Instant finish = Instant.now();
            long classificationTime = Duration.between(start, finish).toMillis();

            // if classification is above 0.5 image is clear
            boolean clear = output.getDouble(0) >= 0.5;

            LOGGER.info(String.format("Classification result %s (clear=%s) in %sms", output, clear, classificationTime));

            return clear ? CloudyClassification.CLEAR : CloudyClassification.CLOUDY;
        } catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        return CloudyClassification.ERROR;
    }

    /**
     * Finds whether the given image contains interests of: Volcano, Reef, Human-made, Land or Water.
     * @param imagePath path for input image
     * @return the list of interest enums. A list of a single error enum is return in case of exception during classification.
     */
    public static List<InterestClassification> classifyInterest(String imagePath) {
        File image = new File(imagePath);
        LOGGER.info("Classifying interesting image from " + image.getAbsolutePath());

        List<InterestClassification> interests = new ArrayList<>();

        try {
            Instant start = Instant.now();

            byte[] imageBytes = Files.readAllBytes(image.toPath());

            // create the keras network model by importing the model with the h5 file
            MultiLayerNetwork interestingModel = KerasModelImport.importKerasSequentialModelAndWeights(interstingModelPath);

            //Use the nativeImageLoader to downsize the image and convert to numerical matrix
            NativeImageLoader loader = new NativeImageLoader(IMAGE_SIZE, IMAGE_SIZE, 3);

            // get the byte input stream
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            // read image as a numpy matrix
            INDArray imageMatrix = loader.asMatrix(inputStream);
            // values need to be scaled between 0 and 1
            DataNormalization scalar = new ImagePreProcessingScaler(0, 1);
            // then call that scalar on the image dataset
            scalar.transform(imageMatrix);

            // pass through neural net and store it in output array
            INDArray output = interestingModel.output(imageMatrix);

            Instant finish = Instant.now();
            long classificationTime = Duration.between(start, finish).toMillis();

            InterestClassification[] types = {
                InterestClassification.VOLCANO,
                InterestClassification.REEF,
                InterestClassification.HUMAN_MADE,
                InterestClassification.LAND,
                InterestClassification.WATER
            };
            StringBuilder stringBuilder = new StringBuilder();
            for (int index = 0; index < types.length; index++) {
                InterestClassification interest = types[index];
                double result = output.getDouble(index);
                boolean classification = result >= 0.5;
                if (classification) {
                    interests.add(interest);
                }
                stringBuilder.append(String.format("%s: %s (%s)\n", interest, result, classification));
            }

            LOGGER.info(String.format("Classification result\n%sin %sms", stringBuilder.toString(), classificationTime));

            return interests;
        } catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        interests.add(InterestClassification.ERROR);

        return interests;
    }
}
