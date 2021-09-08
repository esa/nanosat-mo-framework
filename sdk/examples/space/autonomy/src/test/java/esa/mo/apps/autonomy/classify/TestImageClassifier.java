package esa.mo.apps.autonomy.classify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestImageClassifier {

    @BeforeClass
    public static void setup() {
        System.setProperty("classifier.cloud.model", "src/main/external-resources/classifier-models/cloud_model.h5");
        System.setProperty("classifier.interesting.model", "src/main/external-resources/classifier-models/interesting_model.h5");
        ImageClassifier.init();
    }

    @Test
    public void testClassifyClear() {
        String imagePath = "src/test/resources/images/clear.jpg";
        CloudyClassification classification = ImageClassifier.classifyClouds(imagePath);
        assertEquals(CloudyClassification.CLEAR, classification);
    }

    @Test
    public void testClassifyCloudy() {
        String imagePath = "src/test/resources/images/cloudy.jpg";
        CloudyClassification classification = ImageClassifier.classifyClouds(imagePath);
        assertEquals(CloudyClassification.CLOUDY, classification);
    }

    @Test
    public void testClassifyInterest() {
        String imagePath = "src/test/resources/images/clear.jpg";
        List<InterestClassification> interests = ImageClassifier.classifyInterest(imagePath);
        assertEquals(2, interests.size());
        assertTrue(interests.contains(InterestClassification.HUMAN_MADE));
        assertTrue(interests.contains(InterestClassification.LAND));
    }
}
