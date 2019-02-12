/**
 *
 * <h1>Image transformer</h1>
 *
 * Given a JSON structure that contains base-64 encoded image data
 * and some other useful stuff (more on that in a minute), returns
 * an updated image that has a date stamp, a message, and the 
 * Coderland logo superimposed on the original image. 
 *
 * To build the code, start with "mvn clean compile package" followed 
 * by the name of the jar file. For example: 
 *   java -jar target/imageOverlay-0.0.1-SNAPSHOT.jar
 *
 * @author Doug Tidwell, doug.tidwell@redhat.com
 * @version 1.0.0
 */

package com.example.imageOverlay;

import com.example.imageOverlay.Image;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.text.SimpleDateFormat;
import java.util.Base64; 
import java.util.Date;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@SpringBootApplication
public class ImageOverlayApplication {

  @CrossOrigin(origins = "*")
  @RestController
  class ImageOverlayController {

  /**
   * This handles POST calls to the /overlayImage endpoint. JSON in,
   * JSON out. Notice that the code uses the Jackson JSON utilities...
   * the signature of the method is that it it receives an Image object
   * and returns an Image object. Jackson automatically handles the
   * conversion to and from JSON.
   *
   * @param JSON that represents an Image object. 
   * The JSON structure contains the following fields: 
   * <ul>
   * <li>imageData - The base-64 encoded image data</li>
   * <li>imageType - Either "JPG" or "PNG" - not case sensitive</li>
   * <li>greeting - The text to write on the image</li>
   * <li>language - The language for the date stamp</li>
   * <li>location - The country for the locale</li>
   * <li>dateFormatString - The date format string. Default is "MMMM d, yyyy"</li>
   * </ul>
   * @return An Image object serialized as JSON. 
   * @exception Throws IOException if anything goes wrong with the files
   *
  */

      @CrossOrigin(origins = "*")
      @RequestMapping(path = "/overlayImage", method = RequestMethod.OPTIONS)
      public void methodName() {
          System.out.println("OPTIONS!");
      } 
      
    @PostMapping(path = "/overlayImage", consumes = "application/json",
                 produces = "application/json")
    public Image incomingImage(@RequestBody Image image)
      throws IOException {
      
      String imageData = image.getImageData(),
             imageType = image.getImageType(),
             greeting = image.getGreeting(),
             language = image.getLanguage(),
             location = image.getLocation(), 
             dateFormatString = image.getDateFormatString(), 
             overlaidImageData = "";

      // Leaving this here for the pod log in the OpenShift console
      System.out.println("Transforming the image");

      // Decode the image data and read it with the ImageIO class
      BufferedImage baseImage =
        ImageIO.read(Base64.getDecoder().
                     wrap(new StringBufferInputStream(imageData)));

      // If this is a PNG, we've got an alpha channel. Anything else,
      // we assume there's no transparency. 
      int imageTypeCode = imageType.equalsIgnoreCase("png") ?
        BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

      // Create a new image
      BufferedImage targetImage =
        new BufferedImage(baseImage.getWidth(), baseImage.getHeight(),
                          imageTypeCode);
        
      // Set up the canvas and the alpha channel
      Graphics2D canvas = (Graphics2D) targetImage.getGraphics();
      canvas.drawImage(baseImage, 0, 0, null);
      AlphaComposite alphaChannel =
        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
      canvas.setComposite(alphaChannel);

      // Get the Coderland logo overlay image. The location of the
      // image is based on the classpath, which in turn is based on
      // the structure of the JAR file. 
      BufferedImage logoImage =
        ImageIO.read(ImageOverlayApplication.class.
                     getResourceAsStream("/static/images/overlay.png"));
      int centerX = 0;
      int centerY = (baseImage.getHeight() - (int) logoImage.getHeight()) / 2;
      canvas.drawImage(logoImage, centerX, centerY, null);

      /* Commented out this block entirely...the Overpass font isn't installed
         in the containerized version of this application, so there's no point 
         in looking for it. 

         Feel free to send us a PR if you figure out how to update the 
         Dockerfile to install Overpass. 

      // Now see what fonts are installed on this system. We're looking
      // for Overpass, of course.
      String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                       .getAvailableFontFamilyNames();
      boolean overpassIsInstalled = false;
      for (String nextFont : fonts) {
        if (nextFont.equalsIgnoreCase("Overpass")) {
          overpassIsInstalled = true;
          break;
        }
      }

      // Use Overpass if it's installed
      if (overpassIsInstalled)
        canvas.setFont(new Font("Overpass", Font.BOLD, 24));
        else */

      // For extra credit: Start with a really large font size, get the
      // font metrics for the requested string, then shrink the font size
      // until the string is the right size to fit into the image. 
      canvas.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));

      // With the font set, figure out how much space the specified greeting
      // will take.
      FontMetrics fontMetrics = canvas.getFontMetrics();
      Rectangle2D rect = fontMetrics.getStringBounds(greeting, canvas);
      
      // Calculate the center of the text and draw the text on the image
      centerX = (baseImage.getWidth() - (int) rect.getWidth()) / 2;
      centerY = 100;
      // Create a drop shadow effect; draw black text as a shadow, then draw
      // white text on top of it. 
      canvas.setColor(Color.BLACK);
      canvas.drawString(greeting, centerX + 2, centerY + 2);
      canvas.setColor(Color.WHITE);
      canvas.drawString(greeting, centerX, centerY);
      
      // Now build the date string in the date format from the Image object.
      SimpleDateFormat sdf =
        new SimpleDateFormat(dateFormatString, new Locale(language, location));
      String dateString = sdf.format(new Date());

      // With the date string created, figure out its dimensions and
      // draw it on the image. 
      rect = fontMetrics.getStringBounds(dateString, canvas);
      centerX = (baseImage.getWidth() - (int) rect.getWidth()) / 2;
      centerY = baseImage.getHeight() - 100;
      // Create a drop shadow effect
      canvas.setColor(Color.BLACK);
      canvas.drawString(dateString, centerX + 2, centerY + 2);
      canvas.setColor(Color.WHITE);
      canvas.drawString(dateString, centerX, centerY);

      // Now it's time to create the image in memory and encode
      // it to base64
      ByteArrayOutputStream overlaidImage = new ByteArrayOutputStream();
      ImageIO.write(targetImage, imageType, overlaidImage);
      overlaidImageData = (Base64.getEncoder().
                 encodeToString(overlaidImage.toByteArray()));

      // Clean up
      canvas.dispose();

      // Finally, create an object from the overlaid image. This is 
      // automatically converted to JSON by the Jackson library.
      Image updatedImage = new Image(overlaidImageData, "JPG", greeting,
                                     language, location, dateFormatString);
      return updatedImage;
    }
  }
  
  public static void main(String[] args) throws IOException {
    SpringApplication.run(ImageOverlayApplication.class, args);
  }
}
