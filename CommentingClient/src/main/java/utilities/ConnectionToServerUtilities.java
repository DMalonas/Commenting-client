package utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.util.Base64;

import java.awt.Color;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;



/**
 * This class contains all the requests that the client can do.
 * @author 170011408
 *
 */
public class ConnectionToServerUtilities {

  private static ClientConfig config;
  private static Client client;
  private static WebTarget target;

  /**
   * Configure client.
   * Give the client the location of the server
   */
  public static void setInitialParameters() {
    config = new ClientConfig();
    client = ClientBuilder.newClient(config);
    target = client.target("http://localhost:8080/myphotocommentingapp/");  
  }
  
  /**
   * Receive photo codes from server.
   * @return The photo codes in a String.
   */
  public static String receivePhotoCodesFromServer() {
    setInitialParameters();
  
    /*Define which web service of the web server will be requested and return 
    *the service results as a String*/
    String response = target.path("showphotocodesws").request().accept(MediaType.TEXT_PLAIN)
        .get(String.class).toString();  
    return response;
  }
  
  /**
   * Receive notifications from server.
   * @param username the user name.
   * @return the notifications.
   */
  public static String receiveNotificationsFromServer(String username) {
    setInitialParameters();
    String response = target.path("shownotificationsforuserws/" 
        + username).request().accept(MediaType.TEXT_PLAIN)
        .get(String.class).toString();  
    return response;
  }
  
  /**
   * Receive the comments for a specific photo.
   * @param photoCode the code of the photo.
   * @return the comments in a String
   */
  public static String receiveCommentsOfPhotoFromServer(String photoCode, String username) {
    setInitialParameters();
    String response = target.path("showcommentsofphotows/" + photoCode + "/" + username).request()
        .accept(MediaType.TEXT_PLAIN).get(String.class).toString();
    return response;
  }
  
  /**
   * Request and receive a specific user's comments.
   * @param username the user name
   * @return all the comments of the user.
   */
  public static String receiveCommentsOfUserFromServer(String username) {
    setInitialParameters();
    String response = target.path("showcommentsofuserws/" + username).request()
        .accept(MediaType.TEXT_PLAIN).get(String.class).toString();
    return response;
  }
  
  /**
   * Request and receive all the Comments made under a comment.
   * @param photoCode the photo's code.
   * @param commentCode the comment's code (e.g 1.2.1).
   * @return the comments requested in a String
  */
  public static String receiveCommentsOfCommentFromServer(String photoCode, String commentCode) {
    setInitialParameters();
    String response = target.path("showcommentsofcommentws/" + photoCode
        + "/" + commentCode).request().accept(MediaType.TEXT_PLAIN)
        .get(String.class).toString();
    return response;
  }

  /**
   * This method sends the comment to the server.
   * @param userName The user name.
   * @param onComment The comment chosen by user 
   *     (it is "" if the user chooses to comment on the photo.
   * @param forPhoto The photo for which the comment is destined.
   * @param commentText The actual comment content.
   */
  public static void sendCommentToServer(String userName, String onComment,
      String forPhoto, String commentText) {
    setInitialParameters();
    /*https://stackoverflow.com/questions/42411136/put-string-value-in-jsonarray*/
    JsonArray jArray = new JsonArray();	
    JsonPrimitive stringAsJson = new JsonPrimitive(userName);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(onComment);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(forPhoto);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(commentText);
    jArray.add(stringAsJson);
    /*https://stackoverflow.com/questions/23816356/how-to-create-inputstream-object-from-jsonobject*/
    String str = jArray.toString();
    InputStream is = new ByteArrayInputStream(str.getBytes());
    /* https://stackoverflow.com/questions/20493395/need-jersey-client-api-way-
     * for-posting-a-webrequest-with-json-payload-and-header
     */
    String response = target.path("receivecommentws").request()
        .post(Entity.json(is)).readEntity(String.class);
    if (response.equals("Wrong Parameters")) {
      System.out.println("You provided wrong parameters");
    } else if (response.equals("No such comment")) {
      System.out.println("You provided wrong comment number");
    } else if (response.equals("OK")) {
      System.out.println("Your comment has been successfully submitted");
    } else {
      System.out.println("Error exchanging data with the server");
    }
  }
  
  /**
   * Send a vote to the server.
   * "true" = upVote, "false" = downVote
   * @param userName the user name
   * @param onComment the comment we want to vote for.
   * @param forPhoto the photo under which the comment is.
   * @param vote the type of the vote (up-vote / down-vote).
   */
  public static void sendVoteToServer(String userName, 
      String onComment, String forPhoto, String vote) {
    setInitialParameters();
    /* https://stackoverflow.com/questions/42411136/put-string-value-in-jsonarray */
    JsonArray jArray = new JsonArray();
    JsonPrimitive stringAsJson = new JsonPrimitive(userName);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(onComment);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(forPhoto);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(vote);
    jArray.add(stringAsJson);
    /* https://stackoverflow.com/questions/23816356/how-to-create-inputstream-object-from-jsonobject */
    String str = jArray.toString();
    InputStream is = new ByteArrayInputStream(str.getBytes());
    /* https://stackoverflow.com/questions/20493395/need-jersey-client-api-way-for-posting-a-webrequest-with-json-payload-and-header */
    String response = target.path("receivevotews").request()
        .post(Entity.json(is)).readEntity(String.class);
    if (response.equals("Wrong Parameters")) {
      System.out.println("You provided wrong parameters");
    } else if (response.equals("No such comment")) {
      System.out.println("You provided wrong comment number");
    } else if (response.equals("OK")) {
      System.out.println("Your vote has been successfully submitted");
    } else {
      System.out.println("Error exchanging data with the server");
    }
  }
  
  /**
   * Send rating to the server.
   * @param userName user name
   * @param forPhoto the photo we want to rate
   * @param rating the rating (number of stars and rating comment included).
   */
  public static void sendRatingToServer(String userName, String forPhoto, String rating) {
    setInitialParameters();
    JsonArray jArray = new JsonArray();
    JsonPrimitive stringAsJson = new JsonPrimitive(userName);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(forPhoto);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(rating);
    jArray.add(stringAsJson);
    String str = jArray.toString();
    InputStream is = new ByteArrayInputStream(str.getBytes());
    String response = target.path("receiveratingws").request().
        post(Entity.json(is)).readEntity(String.class);
    if (response.equals("Wrong Parameters")) {
      System.out.println("You provided wrong parameters");
    } else if (response.equals("You have already rated this photo")) {
      System.out.println("You have already rated this photo");
    } else if (response.equals("OK")) {
      System.out.println("Your rating has been successfully submitted");
    } else {
      System.out.println("Error exchanging data with the server");
    }
  }
  
  public static String receiveRatingsOfPhoto(String photoCode) {
    setInitialParameters();
    String response = target.path("showratingsofphotows/" + photoCode).request().accept(MediaType.TEXT_PLAIN)
        .get(String.class).toString();
    return response;
  }
  
  
  /**
   * Send user name and password to the server for authentication.
   * @param username the username.
   * @param password the password.
   * @return
   */
  public static boolean sendCredentialsToServer(String username, String password) {
    setInitialParameters();
    /* https://stackoverflow.com/questions/42411136/put-string-value-in-jsonarray */
    JsonArray jArray = new JsonArray();	
    JsonPrimitive stringAsJson = new JsonPrimitive(username);
    jArray.add(stringAsJson);
    stringAsJson = new JsonPrimitive(password);
    jArray.add(stringAsJson);
    /* https://stackoverflow.com/questions/23816356/how-to-create-inputstream-object-from-jsonobject */
    String str = jArray.toString();
    String response;
    try {
      InputStream is = new ByteArrayInputStream(str.getBytes());
      response = target.path("authenticateuserws").request()
          .post(Entity.json(is)).readEntity(String.class);
      if (response.equals("Invalid credentials")) {
        System.out.println("You provided wrong username or password.");
        return false;
      } else if (response.equals("OK")) {
        System.out.println("Login Successful.");
        return true;
      }
    } catch (Exception e) {
      System.out.println("Server is down");/* https://stackoverflow.com/questions/20493395/need-jersey-client-api-way-for-posting-a-webrequest-with-json-payload-and-header */
      System.exit(1);
    }
    //wrong json format.
    System.out.println("Server data exchange error");
    return false;
    
  }  
  
  /**
   * Check if username is admin or not.
   * @param username the approved username (aleady connnected).
   * @return ADMIN if user is admin, NOT-ADMIN if they are not.
   */
  public static String receiveAdminStatusFromServer(String username) {
    setInitialParameters();
    String response = target.path("authenticateadminws/" + username).request()
        .accept(MediaType.TEXT_PLAIN).get(String.class).toString();
    return response;
  }
  
  public static void removeComment(String insertedCommentCode, String photoCode) {
    setInitialParameters();
    String response = target.path("removecommentfromphotows/" + insertedCommentCode + "/" + photoCode).request().accept(MediaType.TEXT_PLAIN)
        .get(String.class).toString();  
    if(!response.equals("OK")) {
    	System.out.println("Wrong input");
    } else {
    	System.out.println("Comment " + insertedCommentCode + " has been successfully removed");
    }
  }

  
  
  /**
   * 
   * @param img
   */
  public static String sendImageToServer(BufferedImage img, String username, String photoName) {
    setInitialParameters();
    JsonArray jArray = new JsonArray();
    String response = null;
    byte[] imageInByte = null;
    String imageInString = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      String format = "jpg";
      ImageIO.write(img, format, baos);
      baos.flush();
      imageInByte = baos.toByteArray(); 
      baos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    InputStream is = new ByteArrayInputStream(imageInByte);
    response = target.path("receiveimagews/" + username + "/" + photoName).request()
        .post(Entity.json(is)).readEntity(String.class);
    if (response.equals("OK")) {
      return "OK";
    } else if (response.equals("PHOTO-NAME-TAKEN")){
      return "PHOTO-NAME-TAKEN";
    } else {
      return "NOK";
    }
  }
  
  
  public static void sendDescriptionToServer(String username, String photoName, String description) {
	  setInitialParameters();
	    /* https://stackoverflow.com/questions/42411136/put-string-value-in-jsonarray */
	    JsonArray jArray = new JsonArray();	
	    JsonPrimitive stringAsJson = new JsonPrimitive(username);
	    jArray.add(stringAsJson);
	    stringAsJson = new JsonPrimitive(photoName);
	    jArray.add(stringAsJson);        
	    stringAsJson = new JsonPrimitive(description);
	    jArray.add(stringAsJson);    
	    /* https://stackoverflow.com/questions/23816356/how-to-create-inputstream-object-from-jsonobject */
	    String str = jArray.toString();
	    String response;
	    try {
	      InputStream is = new ByteArrayInputStream(str.getBytes());
	      response = target.path("receivephotodescriptionws").request()
	          .post(Entity.json(is)).readEntity(String.class);
	      if (response.equals("OK")) {
	        System.out.println("Description submitted");
	      }
	    } catch (Exception e) {
	      System.out.println("Server is down");/* https://stackoverflow.com/questions/20493395/need-jersey-client-api-way-for-posting-a-webrequest-with-json-payload-and-header */
	      System.exit(1);
	    } 
  }

}


