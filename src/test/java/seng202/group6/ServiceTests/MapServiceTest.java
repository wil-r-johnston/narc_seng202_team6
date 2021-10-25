package seng202.group6.ServiceTests;
/*
import seng202.group6.Services.MapService;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;


public class MapServiceTest extends TestCase{

    public void saveImageTest() throws IOException {
        MapService mapService = new MapService();
        String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&maptype=roadmap&markers=color:blue%7Clabel:S%7C40.702147,-74.015794&markers=color:green%7Clabel:G%7C40.711614,-74.012318&markers=color:red%7Clabel:C%7C40.718217,-73.998284&key=AIzaSyBZgxE6A5nvnM7aYqg49wDdK_SPKXqdLiE";
        String destinationFile = "mapimages/image.jpg";
        mapService.saveImage(imageUrl, destinationFile);

        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        byte[] f = Files.readAllBytes(Path.of(destinationFile));

        /*for (int i = 0; i < b.length; i++) {
            assertEquals(,1);
        }
    }

}*/
