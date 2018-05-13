package controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import objects.User;
import objects.Album;
import objects.Photo;
import objects.TagType;
public class ViewImageController{
	
	@FXML private Button btnPrev, btnNext, btnClose;
    private Stage currStage;
	private Album selectedAlbum;
	@FXML private AnchorPane plain;
	@FXML private ImageView imgView;
	@FXML private Button prev,next;
	//@FXML private MenuButton menu;//hate
	
	private Photo currPhoto;
	
	@FXML private ListView<TagType> listTags;
	private ObservableList<TagType> obsList;
	@FXML private Label lblFileName, lblAlbum, lblDate, lblCaption;
	
	
	
	
	
	
	//================================================= INNER CLASSES ===========================================
		//DO NOT EDIT
		public class ViewLVCell extends ListCell<TagType> {
			@Override
			/**
			 * Updates the custom list cell with the tagtype
			 */
		    public void updateItem(TagType tagTypeItem, boolean empty){
		        super.updateItem(tagTypeItem,empty);
				if (tagTypeItem == null || empty) {
					setText(null);
					setGraphic(null);
				}
				else {
					ViewLVData data = new ViewLVData();
		            data.setTagType(tagTypeItem);
		            setGraphic(data.getPane());
				}
		    }
		}
		
		
		public class ViewLVData {
			@FXML private AnchorPane anchorPane;
		    @FXML private Label lblTagType, lblTags;
		
		    /**
		     * Constructor for the listview's custom cell data
		     */
		    public ViewLVData(){
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TagsViewCellLayout.fxml"));
		        fxmlLoader.setController(this);
		        try{fxmlLoader.load();}
		        catch (IOException e) {
		        	exceptionPrint(e);}
			}
		    /**
		     * Sets the data onto the cell
		     * @param tagTypeItem the tag to extract data from 
		     */
			public void setTagType(TagType tagTypeItem) {
				
				lblTagType.setText(tagTypeItem.getNameOfType());
				
				String tags = "";
				for (String s: tagTypeItem.getArrTags()) {
					tags = tags + s + ";";
				}
				lblTags.setText(tags);
			}
			/**
			 * Gets the anchorpane with all the elements in it
			 * @return Anchor Pane
			 */
			public AnchorPane getPane() {
				return anchorPane;
			}

		}

	
	
	
	// ============================================== METHODS ==========================================================
	
	/**
	 * initializes the view window	
	 * @param primaryStage the current stage
	 * @param startingPhoto photo to start the slideshow on
	 */
	public void start(Stage primaryStage, Photo startingPhoto){
		currStage= primaryStage;
		currStage.setTitle("Photo");
		this.currPhoto = startingPhoto;

		//Listview
		ArrayList<TagType> listArr = new ArrayList<TagType>();
		obsList = FXCollections.observableList(listArr);
		/*
		for (TagType t: currUser.getAllTagTypes()) {
			obsList.add(t);
		}
		listSelTags.setItems(obsList);
		*/
		listTags.setItems(obsList);
		//Sets listview's custom cell format
		listTags.setCellFactory(new Callback<ListView<TagType>, javafx.scene.control.ListCell<TagType>>() {
			public ListCell<TagType> call(ListView<TagType> listView){
                return new ViewLVCell();
            }
		});
		//displayImage(startingPhoto);
		showDetails();
				
	}
	
	/**
	 * Shows the current details of the photo being displayed
	 */
	private void showDetails() {
		lblCaption.setText(currPhoto.getCaption());
		lblFileName.setText(currPhoto.getName());
		lblDate.setText(currPhoto.getDate());
		lblAlbum.setText(currPhoto.getAlbumName());
		
		ArrayList<TagType> listArr = new ArrayList<TagType>();
		obsList = FXCollections.observableList(listArr);
		for(TagType t: currPhoto.getPhotoTags()) {
			//////System.out.println("BLAH: " + t.getNameOfType());
			obsList.add(t);
		}
		listTags.setItems(obsList);
		listTags.refresh();
		
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(currPhoto.getPhotoFile());
	        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
	        imgView.setImage(image);
		} catch (IOException e) {
			exceptionPrint(e);
		}
	}
	
	/**
	 * Handles button press events according to its source, and runs it's process.
	 * @param e ActionEvent
	 */
	@FXML
	public void btnPress(ActionEvent e) {
		Button btn = (Button) e.getSource();
		if (btn == btnClose) 
			closeProcess();
		else if (btn == btnPrev) {
			prevImgProcess();
		}
		else if (btn == btnNext) {
			nextImgProcess();
		}
	}
	
	/**
	 * Goes to the next image in the current album and loops	
	 */
	private void nextImgProcess() {
		int currIndex = selectedAlbum.getAllPhotos().indexOf(currPhoto);
		Photo next;
		if (currIndex == selectedAlbum.getAllPhotos().size()-1) {
			next = selectedAlbum.getAllPhotos().get(0);
		}
		else {
			next = selectedAlbum.getAllPhotos().get(currIndex + 1);
		}
		

		showDetails();
		currPhoto = next;
		
	}
	
	/**
	 * Goes to the previous image in the current album and loops
	 */
	private void prevImgProcess() {
		int currIndex = selectedAlbum.getAllPhotos().indexOf(currPhoto);
		Photo prev;
		if (currIndex == 0) {
			prev = selectedAlbum.getAllPhotos().get(selectedAlbum.getAllPhotos().size()-1);
		}
		else {
			prev = selectedAlbum.getAllPhotos().get(currIndex - 1);
		}
		

		showDetails();
		currPhoto = prev;
		
	}
	
	/**
	 * closes the window
	 */
	private void closeProcess() {
		currStage.close();
	}
	
	/**
	 * Sets the current album
	 * @param selectedAlbum the current album
	 */
	public void setAlbumCase(Album selectedAlbum) {
		this.selectedAlbum = selectedAlbum;
	}
	

	/**
	 * Overrides the default close method for the window
	 * @param eventHandler window close event
	 */
	public void setOnCloseRequest(EventHandler<WindowEvent> eventHandler) {
	}

	/**
	 * Sets the current user
	 * @param currUser the current user
	 */
	public void setCurrentUser(User currUser) {
		//this.currUser=currUser;
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
