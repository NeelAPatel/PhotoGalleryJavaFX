package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import objects.Photo;
import objects.User;
import objects.Album;



public class AddPhotoController {
	// ================================================ FIELDS =================================================
	// -- Information
	@FXML private TextField tfPhotoPath;
	@FXML private Button  btnAddPhoto;
	@FXML private Label lblFileName, lblFileDate;
	@FXML ImageView imgView;
	
	// -- Current Selected Photo
	private File selectedFile = null;
	private Photo selectedPhoto; // A placeholder from the PhotoController to carry back the selected Photo
	private Album currAlbum;
	private String currAlbumName;
	private Photo temp; //used for manipulation of selected photo within the window
	
	// -- Window Controls
	@FXML private Button btnOpen, btnCancel;
	
	// -- GLOBALS
	private Stage currStage;
	private User currUser;
	// ============================================== METHODS ==========================================================
	
	/**
	 * Starts the Add Photo window
	 * @param addPhotoStage the current stage
	 * @param albumName current album's name
	 * @param user current user
	 */
	public void start(Stage addPhotoStage, String albumName,User user) {
		//Set initial values
		currStage = addPhotoStage;
		currStage.setTitle("Add new Photo");
		this.currUser = user;
		//initializes imgView and sets album name for later use
		imgView.setViewport(new Rectangle2D(150, 150, 0, 0));
		imgView.setFitWidth(150);
		imgView.setFitHeight(150);
		currAlbumName = albumName;
		
		
		
		//controls buttons
		btnVisibility();
	}
	
	@FXML
	private void btnPress (ActionEvent e) {
		Button btn = (Button) e.getSource();
		if (btn == btnOpen) {
			openFileChooser(); //Open file chooser, select image, and return
		} else if (btn == btnAddPhoto) {
			addPhotoToAlbumProcess(); //Closes window and sends back selected image
		} else if (btn == btnCancel) {
			cancelWindowProcess(); //Closes window with no action taken
		}
		
	}

	/**
	 * Opens FileChooser and returns a selected image back to the user.
	 */
	private void openFileChooser() {

		FileChooser fileChooser = new FileChooser();
        
        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
          
        //Show open file dialog
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {          
	        try {
	        	//Gets the file from FileChooser and sets it to temp
	        	
	        	
	        	//System.out.println("CurrUser TagTypeArr size: "+ currUser.getAllTagTypes().size());
	        	temp = new Photo(selectedFile, currAlbumName,currUser.getAllTagTypes());
	        	//System.out.println("TempPhoto TagTypeArr size: "+ temp.getPhotoTags().size());
	        	//temp.setTransferAttributes(selectedFile, currAlbumName);
	        	
	        	//Displays information
	        	tfPhotoPath.setText(temp.getFilePath());
				lblFileDate.setText(temp.getDate());
				lblFileName.setText(temp.getName());
				
				//ImageView
	            BufferedImage bufferedImage = ImageIO.read(selectedFile);
	            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
	            imgView.setImage(image);
	            btnAddPhoto.setDisable(false);
	            //imgView.setImage(new Image(getClass().getResourceAsStream("/stockimg/albumStock.png")));
	            btnVisibility();
	        } catch (IOException e) {
	        	exceptionPrint(e);
	        }
        }
		
        
        btnVisibility();
        
	}

	/**
	 * Transfers data from the selected photo to the placeholder Photo object, and closes the window
	 */
	private void addPhotoToAlbumProcess() {
		
		if (!isDuplicatePhoto(temp)) {
			selectedPhoto.setTransferAttributes(temp.getPhotoFile(), temp.getCaption(),temp.getAlbumName(), temp.getPhotoTags());
			currStage.close();
		}
		else
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error - Duplicate Photo");
			alert.setHeaderText("Duplicate Photo!");
			alert.setContentText("The album already contains a photo of the same Path and FileName.");

			alert.showAndWait();
			openFileChooser();
		}
	}
	
	/**
	 * Closes the window
	 */
	private void cancelWindowProcess() {
		currStage.close();
	}
	
	// ============================ HELPER METHODS ================================================
	
	/**
	 * Controls the AddPhoto button's functionality
	 */
	private void btnVisibility() {
		if (lblFileName.getText().equals("")) {
			btnAddPhoto.setDisable(true);
		}
	}

	/**
	 * Sets the placeholder with the Photo object reference from the PhotosPageController
	 * @param newPhoto a reference to a Photo object
	 * @param currAlbum a reference to the current Album
	 */
	public void setSelectedPhotoPlaceholder(Photo newPhoto, Album currAlbum) {
		this.selectedPhoto = newPhoto;
		this.currAlbum = currAlbum;
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

	/**
	 * Checks if its a duplicate photo DOES NOT WORK CORRECTLY. YOU NEED TO CHECK THE CONTENTS OF THE FILE, NOT THE FILE PATH. 
	 * @param temp
	 * @return
	 */
	private boolean isDuplicatePhoto(Photo temp) {
		for (Photo p : currAlbum.getAllPhotos()) {
			if (p.getFilePath().equals(temp.getFilePath())) {
				return true;
			}
		}
		return false;
	}
	

}
