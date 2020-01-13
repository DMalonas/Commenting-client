package application;

import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import utilities.ConnectionToServerUtilities;

/**
 * This class contains the functionality of the client.
 * @author 170011408
 *
 */
public class ClientFunctionalityClass {
  private Scanner readInput;
  private String username;
  private boolean admin;
 
  /**
   * The ClientFunctionalityClass constructor. 
   * We get the userName and password before
   * proceeding. There is a hard-coded 
   * pseudo-database in an utility 
   * class in the server side with 
   * default users.
   * Upon success the different 
   * choice options are printed
   * in the console.
   */
  public ClientFunctionalityClass() {
    readInput = new Scanner(System.in);
    username = getCredentials();
    admin = getAdminStatus();
    showOptionsLevel0();
  }

  /**
   * Get username and password from the client.
   * There is a hard-coded 
   * pseudo-database in an utility 
   * class in the server side with 
   * default users.
   * @return String, The username (we dont need the password beyond authentication).
   */
  public String getCredentials() {
    String userNameLocal;
    String passwordLocal;
    for (;;) {
      try {
        System.out.print("\nEnter user name: ");
        userNameLocal = this.readInput.nextLine();
        System.out.print("\nEnter password: ");
        passwordLocal = this.readInput.nextLine();
        if (ConnectionToServerUtilities.sendCredentialsToServer(userNameLocal, passwordLocal)) {
          /* after I am authenticated I do not care about the password anymore. */
          return userNameLocal;
        }
      } catch (NoSuchElementException e) {
        System.out.println("\nAn empty line is not a proper input");
        System.exit(1);
      }
    }
  }
  

  /**
   * Check if user is admin or not.
   * @return true if it is admin.
   */
  public boolean getAdminStatus() {
    String usernameLocal = this.username;
    if (ConnectionToServerUtilities.receiveAdminStatusFromServer(usernameLocal).equals("ADMIN")) {
      return true;
    }
    return false;
  }

  
  /**
   * This method prints the available photos to comment and is concerned
   * with how to create a client for consuming a server's web services.
   */
  public void showOptionsLevel0() {
    
    try {
      String response = ConnectionToServerUtilities.receiveNotificationsFromServer(username);
      if (response.equals("")) {
        System.out.println("\nHi " + username + ". You have no new notifications");
      } else {
        System.out.println("\nHi " + username + ". You have the following notifications");
        System.out.println(response);
      }
      System.out.println("Available Photos: ");
      response = ConnectionToServerUtilities.receivePhotoCodesFromServer();
      System.out.print(response);
      System.out.print("Choose a photo by inserting its code, or \"u\""
          + " for checking a specific user's comments, or \"q\" for quiting: ");
    } catch (ProcessingException e) {
      System.out.println("Connection denied or the server is inactive");
      System.exit(-1);
    }
    try {
      String inputString = readInput.nextLine();
      if (inputString.equals("q")) {
        System.out.println("Quitting...");
        System.exit(0);
      } else if (inputString.equals("u")) {
        System.out.print("Please enter the username of the user: ");
        String username = readInput.nextLine();
        showCommentsOfUser(username);
      } else {    
        showCommentsOfPhoto(inputString);
      }
    } catch (NotFoundException e) {
      System.out.print("The key \"Enter\" is not an acceptable input\n");
      showOptionsLevel0();
    } catch (NoSuchElementException e) {
      System.out.println("\nAn empty line is not a proper input");
      System.exit(1);
    }
  }
  
  /**
   * Show the comments under a specific photo.
   * @param photoCode The code of the photo.
   */
  public void showCommentsOfPhoto(String photoCode) {
    /*Call web service. User chosen a photo and the WS 
    * will bring them the comments
    *  of the photo. Input parameter is the photo code.*/
    String response = ConnectionToServerUtilities
        .receiveCommentsOfPhotoFromServer(photoCode, username);
    if (response.equals("No comments")) {
      System.out.println("There are no comments on this photo so far.");
      showOptionsLevel1(photoCode);
    } else if (response.equals("Wrong input")) {
      System.out.println("You provided wrong input, no such photo exists.");
      showOptionsLevel0();
    } else {
      System.out.println("Below you may find the comments of the photo:\n");
      System.out.println(response);
      showOptionsLevel1(photoCode);
    }
    
  }
  
  /**
   * Show the comments under a specific comment.
   * @param photoCode The code of the photo.
   * @param commentCode The code of the comment.
   */
  public void showCommentsOfComment(String photoCode, String commentCode) {
    String response = ConnectionToServerUtilities
        .receiveCommentsOfCommentFromServer(photoCode, commentCode);
    if (response.equals("")) {
      System.out.println("You provided wrong input, no such comment exists.");
      showOptionsLevel0();
    } else {
      System.out.println("Below you may find the comments of the photo:\n");
      System.out.println(response);
      showOptionsLevel1(photoCode);
    }
  }
  
  /**
   * Show all the comments of a user.
   * @param username The user name.
   */
  public void showCommentsOfUser(String username) {
    /*Call web service. User chosen a photo and the WS 
    * will bring them the comments
    *  of the photo. Input parameter is the photo code.*/
    String response = ConnectionToServerUtilities.receiveCommentsOfUserFromServer(username);
    if (response.equals("No comments")) {
      System.out.println("There are no comments by this user so far.");
      showOptionsLevel0();
    } else if (response.equals("Wrong input")) {
      System.out.println("You provided wrong input, no such username exists.");
      showOptionsLevel0();
    } else {
      System.out.println("Below you may find the comments by this user:\n");
      System.out.println(response);
      showOptionsLevel0();
    }
  }

  /**
   * Show the console options menu of commenting choices 
   * after a photo is selected.
   * @param photoCode the code of a photo.
   */
  public void showOptionsLevel1(String photoCode) {
    System.out.println("Press 1 to go back");
    System.out.println("Press 2 to comment on the photo");
    System.out.println("Press 3 to comment on another comment");
    System.out.println("Press 4 to up/down vote on another comment");
    System.out.println("Press 5 to see replies to a comment");
    if (admin) {
      System.out.println("Press 6 to remove comment.");
    }
    System.out.print("Please select your option: ");
    try {
      String input = readInput.nextLine();
      if (admin && input.equals("6")) {
        System.out.println("Please specify the comment you wish to remove. Use the dotted format,"
            + " like 2.1.3, as presented above within the parentheses");
        String insertedCommentCode = readInput.nextLine();
        ConnectionToServerUtilities.removeComment(insertedCommentCode, photoCode);
        showCommentsOfPhoto(photoCode);
      } else if (input.equals("5")) {
        System.out.println("Please specify the comment you wish to isolate. Use the dotted format,"
            + " like 2.1.3, as presented above within the parentheses");
        String insertedCommentCode = readInput.nextLine();
        showCommentsOfComment(photoCode, insertedCommentCode);
      } else if (input.equals("1")) {
        showOptionsLevel0();
      } else if (input.equals("2")) {
        String commentBody = insertComment();
        ConnectionToServerUtilities.sendCommentToServer(username, "", photoCode, commentBody);
        showCommentsOfPhoto(photoCode);
      } else if (input.equals("3")) {
        System.out.println("Please specify the comment "
            + "you wish to comment on. Use the dotted format,"
            + " like 2.1.3, as presented above within the parentheses");
        String insertedCommentCode = readInput.nextLine();
        String commentBody = insertComment();
        ConnectionToServerUtilities.sendCommentToServer(username, insertedCommentCode, photoCode,
             commentBody);
        showCommentsOfPhoto(photoCode);
      } else if (input.equals("4")) {
        System.out.println("Please specify the comment you wish to up/down vote."
             + " Use the dotted format,"
             + " like 2.1.3, as presented above within the parentheses");
        String insertedCommentCode = readInput.nextLine();
        String vote = insertVote();
        ConnectionToServerUtilities.sendVoteToServer(username,
            insertedCommentCode, photoCode, vote);
        showCommentsOfPhoto(photoCode);
      } else {
        System.out.println("You provided an incorrect option");
      }
    } catch (NotFoundException e) {
      System.out.print("The key \"Enter\" is not an acceptable input\n");
      showOptionsLevel1(photoCode);
    } catch (NoSuchElementException e) {
      System.out.println("\nAn empty line is not a proper input");
      System.exit(1);
    }
    showOptionsLevel1(photoCode);
  }


  /**
   * Read input from the user (the comment).
   * @return String, the comment.
   */
  public String insertComment() {
    System.out.println("Please insert your comment:");
    String commentText = readInput.nextLine();
    return commentText;
  }
  
  /**
   * Read input from the user (the vote).
   * @return String, the vote.
   */
  public String insertVote() {
    System.out.println("Please press d for downvoting or anythin else for upvoting");
    String vote = readInput.nextLine();
    if (vote.equals("d")) {
      return "false";
    }
    return "true"; // Default: up vote
  }
}
