package controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Album;
import objects.User;

public class AlbumController {

	// ================================================ FIELDS =================================================
	// -- FXML
	@FXML private Button btnAdd, btnDelete, btnOpenAlbum, btnRenameAlbum, btnUser, btnTags, btnShare, btnSearch;
	@FXML private FlowPane flow;
	@FXML private ScrollPane scrPane;
	@FXML private AnchorPane paneBtnUser;


	// -- Stage
	private Stage currStage;
	
	// -- UserInformation
	private User currUser;
	private ArrayList<User> userList;
	private int totalUsers, currUserCount;
	int totalAlbumsAdded = 0;
	// Selected Album
	private Album selectedAlbum;
	@FXML private AnchorPane selPane, prevSelPane;
	
	
	/* DO NOT DELETE - how to access dynamic tile's elements
	 Label X = (Label) selPane.getChildren().get(index);
	 index = ...
	 0: Album name
	 1: # of Images
	 2: From text
	 3: To Text
	 4: From date
	 5: To date
	 ImageView img = (ImageView) selPane.getChildren().get(6);
	 */
	
	// ========================================================== METHODS =======================================================
	
	/**
	 * Initializes the Album window
	 * @param mainStage the current Stage
	 * @param currUser the current user
	 * @param userList an array of users
	 * @param totalUsers the total number of users
	 * @param currUserCount the highest count of users
	 */
	public void start(Stage mainStage, User currUser, ArrayList<User> userList, int totalUsers, int currUserCount) {
		mainStage.setTitle("Photos - Albums");
		currStage = mainStage;
		this.currUser = currUser;
		this.userList = userList;
		this.totalUsers = totalUsers;
		this.currUserCount = currUserCount;
		
		btnUser.setPrefWidth(125);
		btnUser.setMinWidth(125);
		btnUser.setMaxWidth(125);
		
		
		scrPane.requestFocus();
		
		initializeGraphics();
		restoreAlbumData();
		buttonVisibility();
	}
	
	/**
	 * Handles all button events
	 * @param e ActionEvent for button press
	 */
	@FXML // 4/6
	public void btnPress(ActionEvent e) {
		Button btn = (Button) e.getSource();

		if (btn == btnAdd) {
			addProcess(); // WORKS - need to check for duplicates
		} else if (btn == btnDelete) {
			deleteProcess(); // WORKS
		} else if (btn == btnRenameAlbum) {
			renameProcess(); // WORKS
		} else if (btn == btnUser ) {
			userButtonProcess();
		} else if (btn == btnOpenAlbum) {
			openAlbumProcess(); 
		}  else if (btn == btnTags) {
			tagsListProcess();
    	} else if (btn == btnShare) {
    		shareAlbumProcess();
    	}else if (btn == btnSearch) {
    		searchProcess();
    	} 
	}

	/**
	 * Adds a new Album
	 */
	private void addProcess() {
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add Album");
		dialog.setHeaderText("Add a new album to your library");
		dialog.setContentText("Album Name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		
		
		if (result.isPresent()) {
			String newAlbumName = result.get().trim();
			
			if (!isDuplicateAlbum(newAlbumName)) {
				
				if (result.get().equals("")) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error - Blank Album Name");
					alert.setHeaderText("Name not found");
					alert.setContentText("A blank Album name was found. Please try again.");

					alert.showAndWait();
					addProcess();
				}
				else {
					Album x = new Album(result.get());
					createTile(x);
					// Create Album Object
					currUser.addNewAlbum(new Album(result.get()));
				}
			}
			else
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error - Duplicate Album");
				alert.setHeaderText("Duplicate Album!");
				alert.setContentText("The album name you have entered already exists. Please try again.");

				alert.showAndWait();
				addProcess();
			}
		}
		else{
			//cancel or closed
		}
		buttonVisibility();
	}
	
	/**
	 * Deletes an Album
	 */
	private void deleteProcess() {
		//Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete the " + currUser.getAlbum(flow.getChildren().indexOf(selPane)).getAlbumName() + " album?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
	
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete the " + selectedAlbum.getAlbumName() + " album?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();
		
		if (alert.getResult() == ButtonType.YES) {
			currUser.getAllAlbums().remove(flow.getChildren().indexOf(selPane));
			int currSelected = flow.getChildren().indexOf(selPane);
			flow.getChildren().remove(currSelected);
			selPane = null;
		}
		
		buttonVisibility();
	}
	
	/**
	 * Renames an album
	 */
	private void renameProcess() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Rename Album");
		dialog.setHeaderText("What do you want to rename the \"" + currUser.getAlbum(flow.getChildren().indexOf(selPane)).getAlbumName() + "\" album to?");
		dialog.setContentText("New Album Name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent() && result.get().trim().length() > 0) {
			String newAlbumName = result.get().trim();
			if (!isDuplicateAlbum(newAlbumName)) {
				currUser.getAlbum(flow.getChildren().indexOf(selPane)).setAlbumName(result.get());
			
				Label lbl = (Label) selPane.getChildren().get(1); // lblAlbumName 
				// Label lbl = (Label) selPane.getChildren().get(1); //Accesses lblNumOfPics 
				lbl.setText(result.get());
			}
			else
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error - Duplicate Album");
				alert.setHeaderText("Duplicate Album!");
				alert.setContentText("The album name you have entered already exists. Please try again.");

				alert.showAndWait();
				renameProcess();
			}
		}
		else
		{
			//canceled
		}
		
		buttonVisibility();
	}
	
 	/**
 	 *  Opens the Album to look at its contents
 	 */
 	private void openAlbumProcess() {
 		try {
 			
 			//Loads photospane fxml
			FXMLLoader photosLoader = new FXMLLoader();
			photosLoader.setLocation(getClass().getResource("/fxml/PhotosPage.fxml"));
			AnchorPane root = (AnchorPane) photosLoader.load();
			
			//sets the photos stage
			Stage photosStage = new Stage();
			photosStage.setTitle("Photos in " + selectedAlbum.getAlbumName());
			photosStage.initModality(Modality.WINDOW_MODAL);
			photosStage.initOwner(currStage);
			Scene scene = new Scene(root);
			photosStage.setScene(scene);
			
			//attach css
			String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
			//String uniqueCSS = getClass().getResource("/css/PhotosPageCSS.css").toExternalForm();
			scene.getStylesheets().addAll(globalCSS);
			
			
			// sets up controller
			PhotosPageController photosController = photosLoader.getController();
				photosController.setCurrentUser(currUser); // sends current user value
				photosController.setAlbumCase(selectedAlbum); // sends current album value
				photosController.start(photosStage, 0);
				photosStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	
			         @Override
			         public void handle(WindowEvent event) {
			             Platform.runLater(new Runnable() {
	
			                 @Override
			                 public void run() {
			                     //System.out.println("PhotosWindow Closed by clicking (X)");
			                     // ask if you want to log out and such
			                     //System.exit(0);
			                     
			                    //System.out.println("Num of Photos: " +selectedAlbum.getAllPhotos().size());
			                 }
			             });
			         }
			     });
			photosStage.showAndWait();
			
			// NEED TO UPDATE THE STATS ON THE ALBUM UPON CLOSE OF ALBUM PANE
			//updateTile();
			flow.getChildren().clear();
			restoreAlbumData();
			
			buttonVisibility();
			
		} catch (IOException e) {
			exceptionPrint(e);
		}
 	}

 	/**
 	 * Share an album with another user
 	 */
 	private void shareAlbumProcess() {
	List<String> choices = new ArrayList<>();
 		
 		for (User u: userList) {
 			if (u.getUsername() != currUser.getUsername())
 				choices.add(u.getUsername());
 		}

 		ChoiceDialog<String> dialog = new ChoiceDialog<>("Select a user", choices);
 		dialog.setTitle("Share album");
 		dialog.setHeaderText("Share "+ selectedAlbum.getAlbumName());
 		dialog.setContentText("Choose a user to share this album with:");
 		String selName = "";
 		// Traditional way to get the response value.
 		Optional<String> result = dialog.showAndWait();
 		if (result.isPresent()){
 			//System.out.println("Your choice: " + result.get());
 			selName = result.get();
 			
 		}
 		
 		
 		if (selName != "") {
 			User selUser = null;
 			for (User u: userList) {
 				if (u.getUsername().equals(selName)) {
 					selUser = u;
 				}
 			}
 			
 			if (selUser != null) {
 				
 				int counter = 0;
 				for (Album x: selUser.getAllAlbums()) {
 					if (x.getAlbumName().equals(selectedAlbum.getAlbumName()))
 							counter++;
 				}
 				
 				if (counter > 0)
 					selUser.addNewAlbum(new Album(selectedAlbum.getAlbumName() + counter, selectedAlbum.getAllPhotos()));
 				else
 					selUser.addNewAlbum(new Album(selectedAlbum.getAlbumName(), selectedAlbum.getAllPhotos()));
 				
 				
 				Alert alert = new Alert(AlertType.INFORMATION);
 				alert.setTitle("Success!");
 				alert.setHeaderText(null);
 				alert.setContentText("The album has been transfered and will show up in their list.");

 				alert.showAndWait();
 			}
 		}
 	}

 	/**
 	 * Add and remove tag categories for the user's entire library
 	 */
 	private void tagsListProcess() {
 		try {
 			
 			//Loads tagsview fxml
			FXMLLoader tagsViewLoader = new FXMLLoader();
			tagsViewLoader.setLocation(getClass().getResource("/fxml/TagsView.fxml"));
			AnchorPane root = (AnchorPane) tagsViewLoader.load();
			
			//sets the tagsview stage
			Stage tagsViewStage = new Stage();
			tagsViewStage.setTitle("User's Tags");
			tagsViewStage.initModality(Modality.WINDOW_MODAL);
			tagsViewStage.initOwner(currStage);
			Scene scene = new Scene(root);
			tagsViewStage.setScene(scene);
			
			//attach css
			String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
			//String uniqueCSS = getClass().getResource("/css/tagsViewPageCSS.css").toExternalForm();
			scene.getStylesheets().addAll(globalCSS);
			
			
			// sets up controller
			TagsViewController tagsViewController = tagsViewLoader.getController();
				//tagsViewController.setCurrentUser(currUser); // sends current user value
				//tagsViewController.setAlbumCase(selectedAlbum); // sends current album value
				tagsViewController.start(tagsViewStage,currUser);
				tagsViewStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	
			         @Override
			         public void handle(WindowEvent event) {
			             Platform.runLater(new Runnable() {
	
			                 @Override
			                 public void run() {
			                     //System.out.println("tagsViewWindow Closed by clicking (X)");
			                     // ask if you want to log out and such
			                     //System.exit(0);
			                     
			                    //System.out.println("Num of tagsView: " +selectedAlbum.getAlltagsView().size());
			                 }
			             });
			         }
			     });
				tagsViewStage.showAndWait();
 		} catch (IOException e) {
			exceptionPrint(e);
		}
	}
 	
	/**
	 * Search across all the albums
	 */
	private void searchProcess() {
		try {
 			
 			//Loads tagsview fxml
			FXMLLoader searchLoader = new FXMLLoader();
			searchLoader.setLocation(getClass().getResource("/fxml/SearchPane.fxml"));
			AnchorPane root = (AnchorPane) searchLoader.load();
			
			//sets the search stage
			Stage searchStage = new Stage();
			searchStage.setTitle("Search all Photos");
			searchStage.initModality(Modality.WINDOW_MODAL);
			searchStage.initOwner(currStage);
			Scene scene = new Scene(root);
			searchStage.setScene(scene);
			
			//attach css
			String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
			//String uniqueCSS = getClass().getResource("/css/searchPageCSS.css").toExternalForm();
			scene.getStylesheets().addAll(globalCSS);
			
			
			// sets up controller
			SearchController searchController = searchLoader.getController();
				//searchController.setCurrentUser(currUser); // sends current user value
				//searchController.setAlbumCase(selectedAlbum); // sends current album value
				searchController.start(searchStage,currUser);
				searchStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			         @Override
			         public void handle(WindowEvent event) {
			             Platform.runLater(new Runnable() {
	
			                 @Override
			                 public void run() {
			                     //System.out.println("searchWindow Closed by clicking (X)");
			                     // ask if you want to log out and such
			                     //System.exit(0);
			                    //System.out.println("Num of search: " +selectedAlbum.getAllsearch().size());
			                	 restoreAlbumData();
			                 }
			             });
			         }
			     });
			searchStage.showAndWait();
			
		
			/*for (int i = 0; i <= totalAlbumsAdded; i++)
			{
				flow.getChildren().remove(i);
			}*/
			flow.getChildren().clear();
			restoreAlbumData();
		} catch (IOException e) {
			exceptionPrint(e);
		}
	}
	
	/**
	 * Edit your password and log off
	 */
	private void userButtonProcess() {
		try {
			//loads user details fxml
			FXMLLoader userMenuLoader = new FXMLLoader();
			userMenuLoader.setLocation(getClass().getResource("/fxml/UserDetails.fxml"));
			AnchorPane root = (AnchorPane) userMenuLoader.load();
			
			//loads user stage
			Stage userMenuStage = new Stage();
			userMenuStage.initModality(Modality.WINDOW_MODAL);
			userMenuStage.initOwner(currStage);
			userMenuStage.setResizable(false);
			Scene scene = new Scene(root);
			userMenuStage.setScene(scene);
			
			//attach css
			String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
			//String uniqueCSS = getClass().getResource("/css/UserDetailsCSS.css").toExternalForm();
			scene.getStylesheets().addAll(globalCSS);
			
			
			boolean isLogoffClicked = false;
			
			//controller
			UserMenuController userMenuController = userMenuLoader.getController();
				userMenuController.start(userMenuStage, currUser, userList, totalUsers, currUserCount);
				userMenuStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			         @Override
			         public void handle(WindowEvent event) {
			             Platform.runLater(new Runnable() {

			                 @Override
			                 public void run() {
			                     //System.out.println("UserMenu Window Closed by clicking (X)");
			                     // ask if you want to log out and such
			                     //System.exit(0);
			                 }
			             });
			         }
			     });
			userMenuStage.showAndWait();
			//System.out.println(isLogoffClicked);
			if (currUser.logout)
				currStage.close();
		} catch (IOException e) {
			exceptionPrint(e);
		}
	}
 	
	// ========= TILE =============
	/**
	 * Creates a tile to disaply on the album pane
	 * @param album An album to extract information from
	 */
	private void createTile(Album album) {
		AnchorPane anchTile = new AnchorPane(); // Everything goes on top of this.
		anchTile.setMinWidth(300);
		anchTile.setMaxWidth(300);
		anchTile.setMinHeight(150);
		anchTile.setMaxHeight(150);

		StackPane stackPane = new StackPane();
		stackPane.setLayoutX(1);
		stackPane.setLayoutY(1);
		stackPane.setMinWidth(148);
		stackPane.setMaxWidth(148);
		stackPane.setMinHeight(148);
		stackPane.setMaxHeight(148);
		
		ImageView imgView = new ImageView();
		imgView.setX(1);
		imgView.setY(1);
		//imgView.setPreserveRatio(true);
		if (album.getAllPhotos().size() > 0) {
			try {
				BufferedImage bufferedImage = ImageIO.read(album.getPhoto(0).getPhotoFile());
				Image image = SwingFXUtils.toFXImage(bufferedImage, null);
			 	imgView.setImage(image);
			} catch (IOException e) {
				exceptionPrint(e);
			}
		}
		else {
			imgView.setImage(new Image(getClass().getResourceAsStream("/resources/albumStock.png")));
		}
		imgView.setViewport(new Rectangle2D(148, 148, 0, 0));
		imgView.setPreserveRatio(true);
		imgView.setFitWidth(148);
		imgView.setFitHeight(148);
		/* height width x y */
		
		stackPane.getChildren().add(imgView);
		//stackPane.setAlignment(imgView,Pos.CENTER);
		StackPane.setAlignment(imgView, Pos.CENTER);
		
		//imgView.setFitHeight(148);
		
		Label lblAlbumName = createLabel(album.getAlbumName(), 160, 15, 125, 125, 17, 17);
		Label lblNumOfPics = createLabel(album.getAllPhotos().size() + " images", 160, 40, 125, 125, 17, 17);
		Label lblFrom = createLabel("From: ", 160, 100, 34, 34, 17, 17);
		Label lblTo = createLabel("To: ", 160, 125, 20, 20, 17, 17);
		
		Label lblStartDate, lblEndDate;
		if (album.getAllPhotos().size() == 0) {
			lblStartDate = createLabel(" - ", 205, 100, 125, 125, 17, 17);
			lblEndDate = createLabel(" - ", 205, 125, 125, 125, 17, 17);	
		}
		else
		{
			lblStartDate = createLabel(album.getEarliestDate(), 205, 100, 125, 125, 17, 17);
			lblEndDate = createLabel(album.getLatestDate(), 205, 125, 125, 125, 17, 17);
		}
		

		
		//Adds all the elements to the anchor pane tile
		//anchTile.getChildren().add(imgView);
		anchTile.getChildren().add(stackPane);
		anchTile.getChildren().add(lblAlbumName);
		anchTile.getChildren().add(lblNumOfPics);
		anchTile.getChildren().add(lblFrom);
		anchTile.getChildren().add(lblTo);
		anchTile.getChildren().add(lblStartDate);
		anchTile.getChildren().add(lblEndDate);
		
		//Handles click events
		anchTile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				selectAlbum(event, anchTile);
			}
		});

		imgView.toFront();
		flow.getChildren().add(anchTile);
	}

	/**
	 * Selects an album by putting a black border on the tile
	 * @param event the mouse click event
	 * @param anchTile the pane that was clicked
	 */
	private void selectAlbum(MouseEvent event, AnchorPane anchTile) {
		if (selPane != anchTile) {
			prevSelPane = selPane;
			selPane = anchTile;
		}
		anchTile.setStyle("-fx-border-color: black; -fx-alignment: top-center");
		anchTile.setEffect(new Glow(0.5));
		//System.out.println("Tile pressed ");
		if (prevSelPane != null) {
			if (prevSelPane != selPane) {
				prevSelPane.setStyle("-fx-alignment: top-center");
				prevSelPane.setEffect(new Glow(0));
			}
		} else {
			prevSelPane = selPane;
		}
		//System.out.println("Tile pressed " + flow.getChildren().indexOf(selPane));
		
		selectedAlbum = currUser.getAlbum(flow.getChildren().indexOf(selPane));
		buttonVisibility();
		event.consume();
		
		//DOUBLECLICK
		
		if (event.getClickCount() == 2) {
			if (selPane != anchTile) {
				prevSelPane = selPane;
				selPane = anchTile;
			}
			anchTile.setStyle("-fx-border-color: black; -fx-alignment: top-center");
			anchTile.setEffect(new Glow(0.5));
			//System.out.println("Tile pressed ");
			if (prevSelPane != null) {
				if (prevSelPane != selPane) {
					prevSelPane.setStyle("-fx-alignment: top-center");
					prevSelPane.setEffect(new Glow(0));
				}
			} else {
				prevSelPane = selPane;
			}
			//System.out.println("Tile pressed " + flow.getChildren().indexOf(selPane));
			
			selectedAlbum = currUser.getAlbum(flow.getChildren().indexOf(selPane));
			buttonVisibility();
			event.consume();
			
			openAlbumProcess();
		}
	}
	
	// ======================================================= HELPERS =================================
	
	/**
	 * Checks if an album is has the same name as another
	 * @param newAlbumName the name of the album to be compared against
	 * @return true if it's a match
	 */
	boolean isDuplicateAlbum(String newAlbumName) {
		for (Album a : currUser.getAllAlbums()) {
			if (a.getAlbumName().equals(newAlbumName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Restores all albums from the user's data
	 */
	private void restoreAlbumData() {
		flow.getChildren().clear();
		ArrayList<Album> arrAlbums = currUser.getAllAlbums();
		selPane = null;
		prevSelPane = null;
		for(Album album : arrAlbums) {
			createTile(album);
			totalAlbumsAdded++;
		}
		buttonVisibility();
	}
	
	/**
	 * initializes the UI portion of the window, such as icons
	 */
	private void initializeGraphics() {
		Image userStock = new Image(getClass().getResourceAsStream("/resources/userStock.png"));
		ImageView userStockView = new ImageView(userStock);
		userStockView.setViewport(new Rectangle2D(40, 40, 0, 0));
		userStockView.setFitHeight(40);
		userStockView.setFitWidth(40);
		btnUser.setGraphic(userStockView);
		btnUser.setContentDisplay(ContentDisplay.LEFT);
		btnUser.setText(currUser.getUsername());
		btnUser.setMinWidth(150);
		
		Image imgTags = new Image(getClass().getResourceAsStream("/resources/Tags.png"));
		ImageView ivTags = new ImageView(imgTags);
		ivTags.setViewport(new Rectangle2D(50,50,0,0));
		ivTags.setFitHeight(50);
		ivTags.setFitWidth(50);
		
		btnTags.setGraphic(ivTags);
		btnTags.setContentDisplay(ContentDisplay.TOP);
		btnTags.setText("Tag List");
		

		Image imgShare = new Image(getClass().getResourceAsStream("/resources/ShareIcon.png"));
		ImageView ivShare = new ImageView(imgShare);
		ivShare.setViewport(new Rectangle2D(50,50,0,0));
		ivShare.setFitHeight(50);
		ivShare.setFitWidth(50);
		btnShare.setGraphic(ivShare);
		btnShare.setContentDisplay(ContentDisplay.TOP);
		btnShare.setText("Share");
		
		Image imgSearch = new Image(getClass().getResourceAsStream("/resources/Search Icon.png"));
		ImageView ivSearch = new ImageView(imgSearch);
		ivSearch.setViewport(new Rectangle2D(50,50,0,0));
		ivSearch.setFitHeight(50);
		ivSearch.setFitWidth(50);
		btnSearch.setGraphic(ivSearch);
		btnSearch.setContentDisplay(ContentDisplay.TOP);
		btnSearch.setText("Search");
		
		Image imgAddAlbum = new Image(getClass().getResourceAsStream("/resources/AddAlbum.png"));
		ImageView ivAddAlbum = new ImageView(imgAddAlbum);
		ivAddAlbum.setViewport(new Rectangle2D(50,50,0,0));
		ivAddAlbum.setFitHeight(50);
		ivAddAlbum.setFitWidth(50);
		btnAdd.setGraphic(ivAddAlbum);
		btnAdd.setContentDisplay(ContentDisplay.TOP);
		btnAdd.setText("Add");
		
		Image imgRename = new Image(getClass().getResourceAsStream("/resources/Rename.png"));
		ImageView ivRename = new ImageView(imgRename);
		ivRename.setViewport(new Rectangle2D(50,50,0,0));
		ivRename.setFitHeight(50);
		ivRename.setFitWidth(50);
		btnRenameAlbum.setGraphic(ivRename);
		btnRenameAlbum.setContentDisplay(ContentDisplay.TOP);
		btnRenameAlbum.setText("Rename");
		
		Image imgDelete = new Image(getClass().getResourceAsStream("/resources/delete.png"));
		ImageView ivDelete = new ImageView(imgDelete);
		ivDelete.setViewport(new Rectangle2D(50,50,0,0));
		ivDelete.setFitHeight(50);
		ivDelete.setFitWidth(50);
		btnDelete.setGraphic(ivDelete);
		btnDelete.setContentDisplay(ContentDisplay.TOP);
		btnDelete.setText("Delete");

	}

	/**
	 * Handles button use
	 */
	private void buttonVisibility() {
		if (flow.getChildren().size() > 0 && selPane != null) {
			btnOpenAlbum.setDisable(false);
			btnRenameAlbum.setDisable(false);
			btnDelete.setDisable(false);
			btnShare.setDisable(false);
		}
		else {			
			btnOpenAlbum.setDisable(true);
			btnRenameAlbum.setDisable(true);
			btnDelete.setDisable(true);
			btnShare.setDisable(true);
		}
		
		
	}
	
	/**
	 * A helper method for creating a label
	 * @param setText label text
	 * @param setLayoutX x coordinate
	 * @param setLayoutY y coordinate
	 * @param setMinWidth minimum width 
	 * @param setMaxWidth maximum width
	 * @param setMinHeight minimum height
	 * @param setMaxHeight maximum height
	 * @return label a configured label
	 */
	private Label createLabel(String setText, int setLayoutX, int setLayoutY, int setMinWidth, int setMaxWidth, int setMinHeight, int setMaxHeight) {
		Label x = new Label(setText);
		x.setLayoutX(setLayoutX);
		x.setLayoutY(setLayoutY);
		x.setMinWidth(setMinWidth);
		x.setMaxWidth(setMaxWidth);
		x.setMinHeight(setMinHeight);
		x.setMaxHeight(setMaxHeight);

		return x;
	}
	
	/**
	 *  Overrides the default window close method
	 * @param event platform close event
	 */
	@FXML
	public void exitApplication(ActionEvent event) {
		//Overrides the default procedure and closes the program
		//System.out.println("PLATFORM CLOSE");
	    Platform.exit();
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
