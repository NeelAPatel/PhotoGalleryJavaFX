package controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import objects.Album;
import objects.Photo;
import objects.TagType;
import objects.User;


public class PhotosPageController {
	// ================================================ FIELDS =================================================
	// -- Menubar
	@FXML private Button btnBackToAlbums, btnAdd, btnDelete, btnEdit, btnCopyTo, btnMoveTo;
	@FXML private Label lblPanelName;
	@FXML private Button btnCreateFromResults;
	
	// -- Sidebar
	@FXML private Label lblSelFileName, lblSelCaption, lblSelAlbumName, lblSelDate;
	@FXML private Button btnExpandImg;
	@FXML private ImageView imgVSelPhoto;
	@FXML private ListView<TagType> listSelTags;
	private ObservableList<TagType> obsList;
	
	// -- ScrollPane
	@FXML private ScrollPane scrPane;
	@FXML private TilePane tilePane;
	
    // -- Tiles
    @FXML private AnchorPane selPane, prevSelPane;
    @FXML private Photo selectedPhoto;
    
    // -- Stage
    private Stage currStage;
    private Album selectedAlbum;
    
	// -- UserInformation
	private User currUser;
	ListView<Album> listView;
	
	//much hate
	//================================================= INNER CLASSES ===========================================
	//DO NOT EDIT
	public class PhotosLVCell extends ListCell<TagType> {
		@Override
		/**
		 * Updates the listview cell
		 */
	    public void updateItem(TagType tagTypeItem, boolean empty){
	        super.updateItem(tagTypeItem,empty);
			if (tagTypeItem == null || empty) {
				setText(null);
				setGraphic(null);
			}
			else {
				PhotosLVData data = new PhotosLVData();
	            data.setTagType(tagTypeItem);
	            setGraphic(data.getPane());
			}
	    }
	}
	
	public class PhotosLVData {
		@FXML private AnchorPane anchorPane;
	    @FXML private Label lblTagType, lblTags;
		/**
		 * Constructor for custom listview data for tags 
		 */
		public PhotosLVData(){
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TagsViewCellLayout.fxml"));
	        fxmlLoader.setController(this);
	        try{fxmlLoader.load();}
	        catch (IOException e) {
	        	exceptionPrint(e);}
		}
		/**
		 * Sets the tag data into the cell 
		 * @param tagTypeItem the tag to extract data from
		 */
		public void setTagType(TagType tagTypeItem) {
			lblTagType.setText(tagTypeItem.getNameOfType());
			tagTypeItem.getArrTags();
			String tags = "";
			for (String s: tagTypeItem.getArrTags()) {
				tags = tags + s + ";";
			}
			lblTags.setText(tags);
		}
		/**
		 * gets the anchorpane containing the elements
		 * @return anchorpane
		 */
		public AnchorPane getPane() {
			return anchorPane;
		}
	}
		
	
	// ============================================== METHODS ==========================================================
	
	/**
	 * Stats and initializes the stage for the photos page
	 * @param photosStage the current Stage
	 * @param isSearch checks if its the search state or not
	 */
	public void start(Stage photosStage, int isSearch) {
		//initial values
		currStage = photosStage;
		currStage.setTitle("Photos in " + selectedAlbum.getAlbumName());
		lblPanelName.setText(selectedAlbum.getAlbumName());
		
		//Listview
		ArrayList<TagType> listArr = new ArrayList<TagType>();
		obsList = FXCollections.observableList(listArr);
		/*
		for (TagType t: currUser.getAllTagTypes()) {
			obsList.add(t);
		}
		listSelTags.setItems(obsList);
		*/
		listSelTags.setItems(obsList);
		//Sets listview's custom cell format
		listSelTags.setCellFactory(new Callback<ListView<TagType>, javafx.scene.control.ListCell<TagType>>() {
			public ListCell<TagType> call(ListView<TagType> listView){
                return new PhotosLVCell();
            }
		});

				
		
		initializeGraphics();
		restoreAlbumData();
		
		showPreviewPane(false);
		btnCreateFromResults.setVisible(false);
		if (isSearch == 1) {
			btnAdd.setVisible(false);
			btnCopyTo.setVisible(false);
			btnDelete.setVisible(false);
			btnEdit.setVisible(false);
			btnMoveTo.setVisible(false);
			btnCreateFromResults.setVisible(true);
		}
		
		buttonVisibility();
	}
	
	
	/**
	 * Handles all the button press events
	 * @param e Action event
	 */
	@FXML
	private void btnPress(ActionEvent e) {
    	Button btn = (Button) e.getSource();
    	if (btn == btnBackToAlbums) {
			backToAlbumPage();
    	} else if (btn == btnAdd) {
			addNewPhotoProcess();
    	} else if (btn == btnDelete) {
			deletePhotoProcess();
    	} else if (btn == btnEdit) {
			editMetadataProcess();
    	} else if (btn == btnCopyTo) {
			copyToProcess();
    	} else if (btn == btnMoveTo) {
			moveToProcess();
    	} else if (btn == btnExpandImg) {
			expandImgProcess();
    	} else if (btn == btnCreateFromResults) {
    		createFromResults();
    	}
	}

	/**
	 *  Creates a new album from the search results 
	 *  */
	
	private void createFromResults() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add Album");
		dialog.setHeaderText("Create an Album from search results");
		dialog.setContentText("Album Name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		
		
		if (result.isPresent()) {
			String newAlbumName = result.get();
			
			if (!isDuplicateAlbum(newAlbumName)) {
				
				if (result.get().equals("")) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error - Blank Album Name");
					alert.setHeaderText("Name not found");
					alert.setContentText("A blank Album name was found. Please try again.");

					alert.showAndWait();
					createFromResults();
				}
				else {
					//Album x = new Album(result.get());
					//createTile(x);
					// Create Album Object
					selectedAlbum.setAlbumName(newAlbumName);
					currUser.addNewAlbum(selectedAlbum);
				}
				//Select newly added file
				
				
				//event.consume();
			}
			else
			{
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error - Duplicate Album");
				alert.setHeaderText("Duplicate Album!");
				alert.setContentText("The album name you have entered already exists. Please try again.");

				alert.showAndWait();
				createFromResults();
				
				
			}
			
			
			
		}
		else{
			//cancel or closed
		}
		//currUser.addNewAlbum(selectedAlbum);
		btnCreateFromResults.setDisable(true);
	}


	/**
	 * Checks if the album is a duplicate by checking its name
	 * @param newAlbumName new album to be compared against
	 * @return true if it is a match
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
	 * Opens AddPhotoWindow and retrieves a new Photo object
	 */
	private void addNewPhotoProcess() {
		
		Photo newPhoto = new Photo();
		try {
			//Loader for PhotoPage
			FXMLLoader addPhotoLoader = new FXMLLoader();
			addPhotoLoader.setLocation(getClass().getResource("/fxml/AddPhoto.fxml"));
			AnchorPane root = (AnchorPane) addPhotoLoader.load();
			
			//Creates Stage for PhotoPage
			Stage addPhotoStage = new Stage();
			addPhotoStage.setTitle("Add new Photo");
			addPhotoStage.initModality(Modality.WINDOW_MODAL);
			addPhotoStage.initOwner(currStage);
			addPhotoStage.setResizable(false);
			Scene scene = new Scene(root);
			addPhotoStage.setScene(scene);
			
			//Attach CSS files
			String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
			//String uniqueCSS = getClass().getResource("/css/AddPhotoCSS.css").toExternalForm();
			scene.getStylesheets().addAll(globalCSS);
			
			//Create Controller for PhotoPage
			AddPhotoController addPhotoController = addPhotoLoader.getController();
			addPhotoController.setSelectedPhotoPlaceholder(newPhoto,selectedAlbum);
			addPhotoController.start(addPhotoStage, selectedAlbum.getAlbumName(), currUser);
			addPhotoStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		         @Override
		         public void handle(WindowEvent event) {
		             Platform.runLater(new Runnable() {
		                 @Override
		                 public void run() {
		                     //System.out.println("addPhoto Window Closed by clicking (X)");
		                     //lblOpenAlbumName.setText(newPhoto.getFilePath()); // X = cancel;  
		                     //System.exit(0); // ask if you want to log out and such    
		                 }
		             });
		         }
		     }); 
			
			
			//Start PhotoPage
			addPhotoStage.showAndWait();
			if (newPhoto.getPhotoFile() != null)
				addPhotoToTilePane(newPhoto);
		} catch (IOException e) {
			exceptionPrint(e);
		}
	}
	
	/**
	 * Adds the photo to the tile by creativing on
	 * @param newPhoto
	 */
	private void addPhotoToTilePane(Photo newPhoto) {

		createTile(newPhoto);
		selectedAlbum.addToAlbum(newPhoto);
		
	}
	
	// Deletes selected photo
	private void deletePhotoProcess() {
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete " + selectedPhoto.getName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();
		
		if (alert.getResult() == ButtonType.YES) {
			selectedAlbum.getAllPhotos().remove(tilePane.getChildren().indexOf(selPane));
			int currSelected = tilePane.getChildren().indexOf(selPane);
			tilePane.getChildren().remove(currSelected);
			selPane = null;
		}
		
		////System.out.println("===Start===");
		for (int i = 0; i < currUser.getAllAlbums().size(); i++) {
			
			////System.out.println("[" + i +"] " + currUser.getAlbum(i).getAlbumName());
			
		}
		////System.out.println("==END===");
		buttonVisibility();
	}
	
	/**
	 * opens the edit metadata window to edit values of the image
	 */
	private void editMetadataProcess() {
		//Photo editedPhoto = new Photo();
		try {
			//Loader for PhotoPage
			FXMLLoader editLoader = new FXMLLoader();
			editLoader.setLocation(getClass().getResource("/fxml/EditMetadata.fxml"));
			AnchorPane root = (AnchorPane) editLoader.load();
			
			//Creates Stage for PhotoPage
			Stage editStage = new Stage();
			editStage.setTitle("Add new Photo");
			editStage.initModality(Modality.WINDOW_MODAL);
			editStage.initOwner(currStage);
			editStage.setResizable(false);
			Scene scene = new Scene(root);	
			editStage.setScene(scene);
			
			//Attach CSS files
			String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
			//String uniqueCSS = getClass().getResource("/css/AddPhotoCSS.css").toExternalForm();
			scene.getStylesheets().addAll(globalCSS);
			
			//Create Controller for PhotoPage
			EditMetadataController editController = editLoader.getController();
			//editController.setSelectedPhotoPlaceholder(newPhoto,selectedAlbum);
			editController.start(editStage, selectedPhoto);
			editStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		         @Override
		         public void handle(WindowEvent event) {
		             Platform.runLater(new Runnable() {
		                 @Override
		                 public void run() {
		                     //System.out.println("editMetadata Window Closed by clicking (X)");
		                     //lblOpenAlbumName.setText(newPhoto.getFilePath()); // X = cancel;  
		                     //System.exit(0); // ask if you want to log out and such    
		                 }
		             });
		         }
		     }); 
			
			
			//Start PhotoPage
			editStage.showAndWait();
			/*if (newPhoto.getPhotoFile() != null)
				addPhotoToTilePane(newPhoto); */
		} catch (IOException e) {
			exceptionPrint(e);
		}
		
		showSelectedDetails(selectedPhoto);
	}
	
	/**
	 * Copies the selected Photo from one album to another
	 */
	private void copyToProcess() {
		if (selectedPhoto != null) {
			try {	
				
				FXMLLoader moveToLoader = new FXMLLoader();
				moveToLoader.setLocation(getClass().getResource("/fxml/moveTo.fxml"));
				AnchorPane root = (AnchorPane) moveToLoader.load();
				
				Stage moveToStage = new Stage();
				moveToStage.setTitle("Move Photo");
				moveToStage.initModality(Modality.WINDOW_MODAL);
				moveToStage.initOwner(currStage);
				moveToStage.setResizable(false);
				
				Scene scene = new Scene(root);
				//attach css
				String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
				//String uniqueCSS = getClass().getResource("/css/PhotosPageCSS.css").toExternalForm();
				scene.getStylesheets().addAll(globalCSS);
				moveToStage.setScene(scene);
				MoveToController moveToController= moveToLoader.getController();
				moveToController.start(moveToStage, selectedPhoto,selectedAlbum, currUser,false); // Add more parameters if you need to send over more things
				moveToController.setOnCloseRequest(new EventHandler<WindowEvent>() {
					
			         @Override
			         public void handle(WindowEvent event) {
			             Platform.runLater(new Runnable() {
	
			                 @Override
			                 public void run() {
			                     //System.out.println("PhotosWindow Closed by clicking (X)");
			                     // ask if you want to log out and such
			                     //System.exit(0);
			                     restoreAlbumData();
			                 }
			             });
			         }
			     });
				moveToStage.showAndWait();
				tilePane.getChildren().clear();
				restoreAlbumData();
			
			} catch (IOException e) {
				exceptionPrint(e);
			}
		}

		
		
		
		
		
		
		
		
	}
	
	/**
	 * Moves the selected photo from one album to another
	 */
	private void moveToProcess() {
		if (selectedPhoto != null) {
			try {	
				
				FXMLLoader moveToLoader = new FXMLLoader();
				moveToLoader.setLocation(getClass().getResource("/fxml/moveTo.fxml"));
				AnchorPane root = (AnchorPane) moveToLoader.load();
				
				Stage moveToStage = new Stage();
				moveToStage.setTitle("Move Photo");
				moveToStage.initModality(Modality.WINDOW_MODAL);
				moveToStage.initOwner(currStage);
				moveToStage.setResizable(false);
				
				Scene scene = new Scene(root);
				//attach css
				String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
				//String uniqueCSS = getClass().getResource("/css/PhotosPageCSS.css").toExternalForm();
				scene.getStylesheets().addAll(globalCSS);
				moveToStage.setScene(scene);
				MoveToController moveToController= moveToLoader.getController();
				moveToController.start(moveToStage, selectedPhoto,selectedAlbum, currUser,true); // Add more parameters if you need to send over more things
				moveToController.setOnCloseRequest(new EventHandler<WindowEvent>() {
					
			         @Override
			         public void handle(WindowEvent event) {
			             Platform.runLater(new Runnable() {
	
			                 @Override
			                 public void run() {
			                     //System.out.println("PhotosWindow Closed by clicking (X)");
			                     // ask if you want to log out and such
			                     //System.exit(0);
			                	 restoreAlbumData();
			                 }
			             });
			         }
			     });
				moveToStage.showAndWait();
				tilePane.getChildren().clear();
				restoreAlbumData();
			
			} catch (IOException e) {
				exceptionPrint(e);
			}
		}

	}
	
	/**
	 * Opens and expands the image into a larger format
	 */
	private void expandImgProcess()  {
		
		try {
		FXMLLoader imagesLoader = new FXMLLoader();
		imagesLoader.setLocation(getClass().getResource("/fxml/viewImage.fxml"));
		AnchorPane root = (AnchorPane) imagesLoader.load();
		
		Stage imageStage = new Stage();
		imageStage.setTitle("Image");
		imageStage.initModality(Modality.WINDOW_MODAL);
		imageStage.initOwner(currStage);
		imageStage.setResizable(false);
		Scene scene = new Scene(root);
		imageStage.setScene(scene);
		
		/*
		String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
		String uniqueCSS = getClass().getResource("/css/ViewImageCSS.css").toExternalForm();
		scene.getStylesheets().addAll(globalCSS, uniqueCSS);*/
		
		ViewImageController imageController= imagesLoader.getController();
		imageController.setCurrentUser(currUser); // sends current user value
		imageController.setAlbumCase(selectedAlbum); // sends current album value
		imageController.start(imageStage, selectedPhoto);
		imageController.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
	         @Override
	         public void handle(WindowEvent event) {
	             Platform.runLater(new Runnable() {

	                 @Override
	                 public void run() {
	                     //System.out.println("expandimg Closed by clicking (X)");
	                     // ask if you want to log out and such
	                     //System.exit(0);
	                     
	                    ////System.out.println("Num of Photos: " +selectedAlbum.getAllPhotos().size());
	                 }
	             });
	         }
	     });
		imageStage.showAndWait();
	
		} catch (Exception e) {
			exceptionPrint(e);
		}
	}
	
	/**
	 * Shows the details of the selected item in the previewwindow
	 * @param selected the selected photo to be previewed
	 */
	private void showSelectedDetails(Photo selected) {

		showPreviewPane(true);
		lblSelFileName.setText(selected.getName());
		lblSelCaption.setText(selected.getCaption());
		lblSelAlbumName.setText(selected.getAlbumName());
		lblSelDate.setText(selected.getDate());
		
		////System.out.println("TAG SIZE: " + selected.getPhotoTags().size());
		ArrayList<TagType> listArr = new ArrayList<TagType>();
		obsList = FXCollections.observableList(listArr);
		for(TagType t: selected.getPhotoTags()) {
			////System.out.println("BLAH: " + t.getNameOfType());
			obsList.add(t);
		}
		listSelTags.setItems(obsList);
		listSelTags.refresh();
		
		
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(selected.getPhotoFile());
	        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
	        imgVSelPhoto.setImage(image);
		} catch (IOException e) {
			exceptionPrint(e);
		}
			
			
		//Populate listview tags
		
			/*ArrayList<TagType> listArr = new ArrayList<TagType>();
		obsList = FXCollections.observableList(listArr);
		listSelTags.setItems(obsList);*/
		
		

		
	}

	
	// ======================================= HELPERS =======================================
	
	private void createTile(Photo newPhoto) {
		AnchorPane anchTile = new AnchorPane();
		anchTile.setMinWidth(180);
		anchTile.setMaxWidth(180);
		anchTile.setMinHeight(160);
		anchTile.setMaxHeight(160);
		
		ImageView imgView = new ImageView();
		imgView.setX(15);
		imgView.setY(15);
		imgView.setFitWidth(150);
		imgView.setFitHeight(100);
		
		imgView.setPreserveRatio(true);
		//imgView.setViewport(new Rectangle2D(150,150,0,0));

        BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(newPhoto.getPhotoFile());
	        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
	        
			imgView.setImage(image);
		} catch (IOException e) {
			exceptionPrint(e);
		}
		if (imgView.getFitWidth() < 150) {
	
			//imgView.setX(90-((int) imgView.getFitWidth()/2));
			
		}
		
		
		
		
		Label lblName = new Label(newPhoto.getName());
		lblName.setLayoutX(15);
		lblName.setLayoutY(134);
		lblName.setMinWidth(150);
		lblName.setMaxWidth(150);
		lblName.setMinHeight(17);
		lblName.setMaxHeight(17);
		lblName.setTextAlignment(TextAlignment.CENTER);
		
		
		anchTile.getChildren().addAll(imgView,lblName);
		anchTile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				selectPhoto(event, anchTile);
				
			}
		});
		
		imgView.toFront();
		tilePane.getChildren().add(anchTile);
	}
	
	/**
	 * Selects the photo based on the mouse click event
	 * @param event the mouse click event
	 * @param anchTile the tile being clicked
	 */
	private void selectPhoto(MouseEvent event, AnchorPane anchTile) {
		if (selPane != anchTile) {
			prevSelPane = selPane;
			selPane = anchTile;
		}
		anchTile.setStyle("-fx-border-color: black; -fx-alignment: top-center");
		anchTile.setEffect(new Glow(0.5));
		////System.out.println("Tile pressed ");
		if (prevSelPane != null) {
			if (prevSelPane != selPane) {
				prevSelPane.setStyle("-fx-alignment: top-center");
				prevSelPane.setEffect(new Glow(0));
			}
		} else {
			prevSelPane = selPane;
		}
		////System.out.println("Tile pressed " + tilePane.getChildren().indexOf(selPane));
		
		selectedPhoto = selectedAlbum.getPhoto(tilePane.getChildren().indexOf(selPane));
		showSelectedDetails(selectedPhoto);
		buttonVisibility();
		event.consume();
		

	}
	
	/**
	 * initializes the icons and graphics
	 */
	private void initializeGraphics() {
		
		Image imgAdd = new Image(getClass().getResourceAsStream("/resources/addPhoto.png"));
		Image imgEditMeta = new Image(getClass().getResourceAsStream("/resources/editMetadata.png"));
		Image imgDelete = new Image(getClass().getResourceAsStream("/resources/delete.png"));
		Image imgCopyTo = new Image(getClass().getResourceAsStream("/resources/copyTo.png"));
		Image imgMoveTo = new Image(getClass().getResourceAsStream("/resources/move to.png"));
		
		
		Image imgExpand = new Image(getClass().getResourceAsStream("/resources/expand.png"));
		
		ImageView ivAdd = new ImageView(imgAdd);
		ImageView ivEditMeta = new ImageView(imgEditMeta);
		ImageView ivDelete = new ImageView(imgDelete);
		ImageView ivCopyTo = new ImageView(imgCopyTo);
		ImageView ivMoveTo = new ImageView(imgMoveTo);
		
		ImageView ivExpand = new ImageView(imgExpand);
		
	
		ivAdd.setViewport(new Rectangle2D(50,50,0,0));
		ivAdd.setFitHeight(50);
		ivAdd.setFitWidth(50);
		ivEditMeta.setViewport(new Rectangle2D(50,50,0,0));
		ivEditMeta.setFitHeight(50);
		ivEditMeta.setFitWidth(50);
		ivDelete.setViewport(new Rectangle2D(50,50,0,0));
		ivDelete.setFitHeight(50);
		ivDelete.setFitWidth(50);
		ivCopyTo.setViewport(new Rectangle2D(50,50,0,0));
		ivCopyTo.setFitHeight(50);
		ivCopyTo.setFitWidth(50);
		ivMoveTo.setViewport(new Rectangle2D(50,50,0,0));
		ivMoveTo.setFitHeight(50);
		ivMoveTo.setFitWidth(50);
		
		
		ivExpand.setViewport(new Rectangle2D(30,30,0,0));
		ivExpand.setFitHeight(30);
		ivExpand.setFitWidth(30);
		
		btnAdd.setGraphic(ivAdd);
		btnAdd.setContentDisplay(ContentDisplay.TOP);
		btnAdd.setText("Add");
		btnEdit.setGraphic(ivEditMeta);
		btnEdit.setContentDisplay(ContentDisplay.TOP);
		btnEdit.setText("Edit");
		btnDelete.setGraphic(ivDelete);
		btnDelete.setContentDisplay(ContentDisplay.TOP);
		btnDelete.setText("Delete");
		btnCopyTo.setGraphic(ivCopyTo);
		btnCopyTo.setContentDisplay(ContentDisplay.TOP);
		btnCopyTo.setText("Copy To");
		btnMoveTo.setGraphic(ivMoveTo);
		btnMoveTo.setContentDisplay(ContentDisplay.TOP);
		btnMoveTo.setText("Move To");
		
		btnExpandImg.setGraphic(ivExpand);
		btnExpandImg.setContentDisplay(ContentDisplay.CENTER);
		btnExpandImg.setText("");
		
		
	}

	/**
	 * Handles button visibility during different states of the window
	 */
	private void buttonVisibility() {
		
		if (tilePane.getChildren().size() > 0 && selPane != null) {
			btnBackToAlbums.setDisable(false);
			btnAdd.setDisable(false);
			btnDelete.setDisable(false);
			btnEdit.setDisable(false); 
			btnCopyTo.setDisable(false);
			btnMoveTo.setDisable(false);
		}
		else {
			btnBackToAlbums.setDisable(false);
			btnAdd.setDisable(false);
			btnDelete.setDisable(true);
			btnEdit.setDisable(true);
			btnCopyTo.setDisable(true);
			btnMoveTo.setDisable(true);
		}
	}

	/**
	 * Populates the tilepane with the data from inside the album
	 */
	private void restoreAlbumData() {
		ArrayList<Photo> arrPhotos = selectedAlbum.getAllPhotos();
		for(Photo pic : arrPhotos)
		{
			createTile(pic);
		}
		showPreviewPane(false);
		selPane = null;
		prevSelPane = null;
		buttonVisibility();
	}
	
	/**
	 * Closes the stage
	 */
	private void backToAlbumPage() {
		currStage.close();
	}

	/**
	 * sets the current user
	 * @param currUser the current user
	 */
	public void setCurrentUser(User currUser) {
		this.currUser = currUser;
	}

	/**
	 * Sets the currentalbum from the selection in the prior window
	 * @param selectedAlbum the selected album chosen to open
	 */
	public void setAlbumCase(Album selectedAlbum) {
		this.selectedAlbum = selectedAlbum;
	}
	
 	/**
 	 * Enables the preview pane1
 	 * @param tf true if the panel is going to be shown
 	 */
	private void showPreviewPane(boolean tf) {
		btnExpandImg.setVisible(tf);
		listSelTags.setVisible(tf);
		lblSelFileName.setVisible(tf);
		lblSelCaption.setVisible(tf);
		lblSelAlbumName.setVisible(tf);
		lblSelDate.setVisible(tf);
		imgVSelPhoto.setVisible(tf);
	}
	
	/**
	 * Prints the exception in a dialog box
	 * @param e Exception
	 */
	@FXML
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
