package controller;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Album;
import objects.Photo;
import objects.User;

public class MoveToController {
	private ObservableList<String> obsList;
	private Stage currStage;
	
	@FXML private Button btnAdd;
	@FXML private Button btnRemove;
	private User currUser;
	@FXML private ListView<String> moveList;
	private Photo selectedPhoto;
	private Album currAlbum;
	private boolean delete;
	
	/**
	 * Initializes the move to controller
	 * @param moveToStage the current stage
	 * @param selectedPhoto the photo that will be moved
	 * @param currAlbum the current album where the selected photo is located
	 * @param currUser the current user
	 * @param delete checks if it will be deleted from the current album or not
	 */
	public void start(Stage moveToStage, Photo selectedPhoto, Album currAlbum, User currUser, boolean delete) {
		this.delete=delete;
		currStage=moveToStage;
		this.currAlbum = currAlbum;
		this.selectedPhoto=selectedPhoto;
		this.currUser = currUser;
		if (delete)
			moveToStage.setTitle("Move Photo");
		else
			moveToStage.setTitle("Copy Photo");
		ArrayList<String> albumNames = new ArrayList<String>();
		for (Album a: currUser.getAllAlbums()) {
			albumNames.add(a.getAlbumName());
		}
		obsList=FXCollections.observableList(albumNames);
		
		
		
		//obsList.addAll(currUser.getAllAlbums());
		moveList.setItems(obsList);
		  if(obsList.size()>0) {
	        	moveList.getSelectionModel().select(0);
	        }
		moveList.requestFocus();

	}

	/**
	 * Handles button press events
	 * @param e button press actionevent
	 */
	@FXML
	private void btnPress(ActionEvent e) {
		Button btn = (Button) e.getSource();
		if (btn == btnAdd) {
			moveProcess();
		}else if (btn == btnRemove) {
			cancelProcess();
		}
	}
	
	/**
	 * Based on your selection, the photo will be moved that respective album and close
	 */
	public void moveProcess() {
		boolean add=true;
		//Remove old photo
	
		
		//Selected Values
		String selectedAlbumName = moveList.getSelectionModel().getSelectedItem();
		
		//Create new photo with selected attributes
		Photo newPhoto=new Photo();
		newPhoto.setTransferAttributes(selectedPhoto.getPhotoFile(),selectedAlbumName ,selectedPhoto.getCaption(), selectedPhoto.getPhotoTags());
		int index= moveList.getSelectionModel().getSelectedIndex();
		Album addToAlbum= currUser.getAlbum(index);
		//String temp = addToAlbum.getAlbumName(index);
		selectedPhoto.getName();
		//System.out.println("photo name" + 	selectedPhoto.getName());
		for(int i =0; i<currUser.getAllAlbums().get(index).getAllPhotos().size();i++) {
			if(addToAlbum.getAllPhotos().get(i).getName().compareTo(selectedPhoto.getName())==0) {
				add=false;
			}
		}
		if(add){
			currUser.getAllAlbums().get(index).addToAlbum(newPhoto);
			if(delete) {
				currAlbum.getAllPhotos().remove(selectedPhoto);
			}
		}else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error - Duplicate");
			alert.setHeaderText("Duplicate found");
			alert.setContentText("A duplicate was found in the target location. The process could not be completed.");

			alert.showAndWait();
		}
		currStage.close(); // Closes the window
	}
	
	/**
	 * Cancels the actions and closes the window
	 */
	private void cancelProcess() {
		currStage.close();
	}

	/**
	 * Overrides the default onclose request
	 * @param eventHandler handler for closing event
	 */
	public void setOnCloseRequest(EventHandler<WindowEvent> eventHandler) {
		
		
		
		
	}

}
