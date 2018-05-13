package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Album;
import objects.Photo;
import objects.User;

public class LoginController {
	// ================================================ FIELDS =================================================
	
	private Stage currStage;
	// -- FXML
	@FXML private TextField tfUsername, tfPassField;
	@FXML private PasswordField passField;
	@FXML private Button btnLogin, btnShowHide;
	@FXML private Label lblErrorMsg;
	@FXML private AnchorPane paneBtnLogin;

	// -- Users
	private ArrayList<User> userList;
	private int totalUsers, currUserCount;
	// ========================================================== METHODS =======================================================

	/**
	 * Initializes and sets up the login page.
	 * @param mainStage JavaFX Container for the Login view.
	 * @param resetApp a variable to check if program needs to be reset to default settings
	 */
	public void start(Stage mainStage, boolean resetApp) {
		//set up
		currStage = mainStage;
		mainStage.setTitle("Login");
		lblErrorMsg.setVisible(false);
		userList = new ArrayList<User>();
		btnLogin.requestFocus();
		
		//REMOVE LATER
		//tfUsername.setText("stock");
		//passField.setText("stock");
		
		
		tfPassField.setVisible(false);
		tfPassField.setText(passField.getText());
		//file management
		

		if (resetApp)
			exportDefault(); // adds admin and reg user to file

		
		importAppStats("appStats.txt"); // Total Users | Current User index
		
		
		importUsersList();

		// Enter button for Login
		passField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER) {
					loginProcess();
				}
			}
		});

	}

	/**
	 * Handles button press events according to its source, and runs it's process.
	 * @param e ActionEvent
	 */
	@FXML
	public void btnPress(ActionEvent e) {
		Button btn = (Button) e.getSource();
		if (btn == btnLogin) 
			loginProcess();
		else if (btn == btnShowHide) {
			showHideProcess();
		}
	}
	
	/**
	 * Checks if the user is valid.
	 */
	private void loginProcess() {
		// Check if login is valid

		String myUsername = tfUsername.getText();
		
		String myPassword = null;
		if (tfPassField.isVisible()) {
			myPassword = tfPassField.getText();
		}
		else if (passField.isVisible()) {
			myPassword = passField.getText();
		}
		
		
		if (myUsername.equals("admin") && myPassword.equals("admin")) {
			lblErrorMsg.setVisible(false);
			//openAlbumPane(1, validUser);
			
			openManageUsers();
		} else {
			lblErrorMsg.setVisible(false);
			User validUser = null;
			for (User u : userList) {
				if (myUsername.equals(u.getUsername())) {
					if (myPassword.equals(u.getPassword())) {
						validUser = u;
					} else {
						validUser = null;
						break;
					}
				}
			}
			
			
			if (validUser == null) {
				lblErrorMsg.setVisible(true);
			}
			else
			{
				openAlbumPane(validUser);
			}
			
		}
		
		
		
		
		

		

	}

	/**
	 * Controls the visibility of password in the field
	 */
	private void showHideProcess() {
		if (passField.isVisible()) {
			//show password by hiding
			tfPassField.setText(passField.getText());
			passField.setVisible(false);
			tfPassField.setVisible(true);
			btnShowHide.setText("H");
		}
		else 
		{
			//hide password
			passField.setText(tfPassField.getText());
			passField.setVisible(true);
			tfPassField.setVisible(false);
			btnShowHide.setText("S");
		}
	}
	
	/**
	 * Opens the manage users window
	 */
	private void openManageUsers() {
			try {
				FXMLLoader mngULoader = new FXMLLoader();
				mngULoader.setLocation(getClass().getResource("/fxml/ManageUsers.fxml"));
				AnchorPane root = (AnchorPane) mngULoader.load();
				
				Stage mngUStage = new Stage();
				mngUStage.setTitle("Manage all users");
				mngUStage.initModality(Modality.WINDOW_MODAL);
				mngUStage.initOwner(currStage);
				mngUStage.setResizable(false);
				Scene scene = new Scene(root);
				mngUStage.setScene(scene);
				
				String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
				//String uniqueCSS = getClass().getResource("/css/ManageUsersCSS.css").toExternalForm();
				scene.getStylesheets().addAll(globalCSS);
				//scene.getStylesheets().add(getClass().getResource("/css/ManageUsersCSS.css").toExternalForm());

				ManageUsersController mngUController = mngULoader.getController();
					mngUController.setDialogStage(mngUStage);
					mngUController.setUserList(userList, totalUsers, currUserCount);
					mngUController.start(mngUStage);
					mngUStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				         @Override
				         public void handle(WindowEvent event) {
				             Platform.runLater(new Runnable() {

				                 @Override
				                 public void run() {
				                     ////System.out.println("Manage Users Window Closed by clicking (X)");
				                     // ask if you want to log out and such
				                     //System.exit(0);
				                 }
				             });
				         }
				     });
				mngUStage.showAndWait();
				
			} catch (IOException e) {
				exceptionPrint(e);
			}
		}
		
	/**
	 * Opens the Album view of the user that has logged in.
	 * @param currUser User that has logged in successfully.
	 */
	private void openAlbumPane(User currUser) {
		try {

			FXMLLoader albumLoader = new FXMLLoader();
			albumLoader.setLocation(getClass().getResource("/fxml/AlbumPage.fxml"));
			AnchorPane root = (AnchorPane) albumLoader.load();

			Stage albumStage = new Stage();
			albumStage.setTitle("Albums");
			albumStage.initModality(Modality.WINDOW_MODAL);
			albumStage.initOwner(currStage);
			albumStage.setResizable(false);
			Scene scene = new Scene(root);
			albumStage.setScene(scene);

			String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
			String uniqueCSS = getClass().getResource("/css/AlbumPageCSS.css").toExternalForm();
			scene.getStylesheets().addAll(globalCSS,uniqueCSS);
			//scene.getStylesheets().add(getClass().getResource("/css/AlbumPageCSS.css").toExternalForm());

			AlbumController albumController = albumLoader.getController();
			currUser.logout = false;
			albumController.start(albumStage,currUser, userList, totalUsers, currUserCount );
			albumStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

		         @Override
		         public void handle(WindowEvent event) {
		             Platform.runLater(new Runnable() {

		                 @Override
		                 public void run() {
		                     ////System.out.println("Album Window Closed by clicking (X)");
		                     // ask if you want to log out and such
		                     //System.exit(0);
		                 }
		             });
		         }
		     });
			currStage.hide();
			albumStage.showAndWait();
			currStage.show();
			


		} catch (IOException e) {
			exceptionPrint(e);
		}
	}

// ================================================================= HELPERS =================================	

	/**
	 * Exports the program data out into the database for retrieval during the next session.
	 */
	public void exportProgramData() {
			
		//App Data
		
		try {
			
			FileWriter fw = new FileWriter("appStats.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			int max = 0;
			for (User u: userList) {
				if (u.getId() >= max)
					max = u.getId();
			}
			
			totalUsers = max;
			currUserCount = max;
			
			
			
			String x = totalUsers+"\n";
			String y = currUserCount+"\n";
			bw.write(x);
			bw.write(y);
	
			bw.close();
			fw.close();			
		}
		catch(Exception e){
			exceptionPrint(e);
		}
		////System.out.println("AppData written");
		
		
		//Users List
		
		try {
			File usersFile = new File("usersList.txt");
			FileOutputStream userOut = new FileOutputStream(usersFile);
			ObjectOutputStream oout = new ObjectOutputStream(userOut);
			for (User u : userList) {
				oout.writeObject(u);
			}
			oout.close();
			////System.out.println( written");
	
			//ObjectInputStream ois = new ObjectInputStream(new FileInputStream("usersList.txt"));
			// //System.out.println("" + ois.readObject());
			//ois.close();
	
		} catch (Exception e) {
			exceptionPrint(e);
		}
		////System.out.println("UserData exported on program close");
			
	}
	
	/**
	 * Exports the default state of the app.
	 */
	private void exportDefault() {
		User neel = new User(1, "neel", "patel");
		User stock = new User(2, "stock", "stock");
		stock.addNewAlbum(new Album("stock"));
		
		
		stock.getAlbum(0).addToAlbum(new Photo(new File("StockAccount/A++.jpg"), "stock", stock.getAllTagTypes()));
		stock.getAlbum(0).getPhoto(0).setCaption("First Photo");
		stock.getAlbum(0).getPhoto(0).getPhotoTags().get(0).addTag("Asian Grandpa");
		stock.getAlbum(0).getPhoto(0).getPhotoTags().get(0).addTag("Meme");
		stock.getAlbum(0).getPhoto(0).getPhotoTags().get(0).addTag("AA");
		stock.getAlbum(0).getPhoto(0).getPhotoTags().get(0).addTag("BB");
        stock.getAlbum(0).addToAlbum(new Photo(new File("StockAccount/NoIdeaWhy.jpg"), "stock", stock.getAllTagTypes()));
        stock.getAlbum(0).getPhoto(1).getPhotoTags().get(0).addTag("Programmer");
        stock.getAlbum(0).getPhoto(1).getPhotoTags().get(1).addTag("Desk");
		stock.getAlbum(0).getPhoto(1).getPhotoTags().get(0).addTag("BB");
		stock.getAlbum(0).getPhoto(1).setCaption("Second Photo");
        stock.getAlbum(0).addToAlbum(new Photo(new File("StockAccount/OneDoesNotSimply.jpg"), "stock", stock.getAllTagTypes()));
        stock.getAlbum(0).getPhoto(2).getPhotoTags().get(0).addTag("LOTR Guy");
        stock.getAlbum(0).getPhoto(2).getPhotoTags().get(1).addTag("LOTR Guy");
        stock.getAlbum(0).getPhoto(2).getPhotoTags().get(0).addTag("AA");
        stock.getAlbum(0).getPhoto(2).setCaption("Third Photo");
        stock.getAlbum(0).addToAlbum(new Photo(new File("StockAccount/SoftwareProject.jpg"), "stock", stock.getAllTagTypes()));
        stock.getAlbum(0).getPhoto(3).getPhotoTags().get(0).addTag("Everyone in 213");
        stock.getAlbum(0).getPhoto(3).getPhotoTags().get(0).addTag("AA");
        stock.getAlbum(0).getPhoto(3).setCaption("Fourth Photo");
        stock.getAlbum(0).addToAlbum(new Photo(new File("StockAccount/WhatIReallyDo.jpg"), "stock", stock.getAllTagTypes()));
        stock.getAlbum(0).getPhoto(4).getPhotoTags().get(0).addTag("That's Neel.");
        stock.getAlbum(0).getPhoto(4).getPhotoTags().get(0).addTag("AA");
        stock.getAlbum(0).getPhoto(4).getPhotoTags().get(0).addTag("BB");
        stock.getAlbum(0).getPhoto(4).setCaption("Fifth Photo");
		
		try {
			FileOutputStream out = new FileOutputStream("usersList.txt");
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(neel);
			oout.writeObject(stock);
			oout.close();
			////System.out.println("Users Written");
		} catch (Exception e) {
			exceptionPrint(e);
		}
		try {
			
			FileWriter fw = new FileWriter("appStats.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			totalUsers = 2;
			currUserCount = 2;
			
			
			
			String x = totalUsers+"\n";
			String y = currUserCount+"\n";
			bw.write(x);
			bw.write(y);

			bw.close();
			fw.close();			
		}
		catch(Exception e){
			exceptionPrint(e);
		}
		
	}

	/**
	 * Imports app statistics into the program from the database.
	 * @param fileName Name of the text file that contains preliminary statistics of the program.
	 */
	public void importAppStats(String fileName) {
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			totalUsers = Integer.parseInt(bufferedReader.readLine());
			currUserCount = Integer.parseInt(bufferedReader.readLine());

			////System.out.println("total users: " + totalUsers);
			////System.out.println("currUserCount: " + currUserCount);
			bufferedReader.close();
		} catch (IOException e) {
			exceptionPrint(e);
		}

	}

	/**
	 * Imports list of users from the database.
	 */
	public void importUsersList() {

		
		if (totalUsers > 0) {
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("usersList.txt"));
			for (int i = 1; i <= totalUsers; i++) {

				User user = (User) ois.readObject();
				// //System.out.println(user);
				user.logout = false;
				userList.add(user);
			}
			ois.close();
			
			////System.out.println(userList.get(0).getUsername());
		} catch (ClassNotFoundException e) {

			exceptionPrint(e);
		} catch (FileNotFoundException e) {

			exceptionPrint(e);
		} catch (IOException e) {

			exceptionPrint(e);
		}
		}
	}
	/**
	 * Prints the exception in a dialog box
	 * @param e Exception
	 */
	private void exceptionPrint(Exception e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");
		alert.setHeaderText("An Exception has occured");
		alert.setContentText("IOException");

		Exception ex = e;

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
}
