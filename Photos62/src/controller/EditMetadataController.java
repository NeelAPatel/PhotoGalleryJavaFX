package controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import objects.Photo;
import objects.TagType;

public class EditMetadataController {
	// ================================================ FIELDS =================================================
	
	//Elements
	@FXML private ImageView imgView;
	@FXML private TextField tfCaption, tfTagsField;
	@FXML private Label lblFileName, lblDate;
	@FXML private ListView<TagType> listTags;
	private ObservableList<TagType> obsList;
	@FXML MenuButton mnubtnTagTypes;
	
	//Misc
	private Stage currStage;
	private Photo currPhoto;
	
	private TagType selectedTag;
	private ArrayList<TagType> tempTags;
	@FXML private Button btnCancel, btnSave, btnSaveTags;
	//================================================= INNER CLASSES ===========================================
	
	/**
	 * Custom Listview Cell type for tags
	 *
	 */
	public class EditLVCell extends ListCell<TagType> {
		/* (non-Javadoc)
		 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
		 */
		@Override
	    public void updateItem(TagType tagTypeItem, boolean empty){
	        super.updateItem(tagTypeItem,empty);
			if (tagTypeItem == null || empty) {
				setText(null);
				setGraphic(null);
			}
			else {
				EditLVData data = new EditLVData();
	            data.setTagType(tagTypeItem);
	            setGraphic(data.getPane());
			}
	    }
	}
	
	/**
	 * Custom listview datatype for tags
	 *
	 */
	public class EditLVData {
		@FXML private AnchorPane anchorPane;
	    @FXML private Label lblTagType, lblTags;
		/**
		 * Constructor for creating a custom listview datatype
		 */
		public EditLVData(){
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TagsViewCellLayout.fxml"));
	        fxmlLoader.setController(this);
	        try{fxmlLoader.load();}
	        catch (IOException e) {
	        	exceptionPrint(e);
	        }
		}
		/**
		 * Sets the contents of the cell to the tagtype's values
		 * @param tagTypeItem A tagType to be displayed
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
		 * Gets the anchorpane 
		 * @return anchorpane
		 */
		public AnchorPane getPane() {
			return anchorPane;
		}

	}

	// ============================================== METHODS ==========================================================
	/**
	 * Initializes the editmetadata window
	 * @param editStage the current stage
	 * @param selectedPhoto photo that will be targeted
	 */
	public void start(Stage editStage, Photo selectedPhoto) {
		this.currStage = editStage;
		currStage.setTitle("Edit - " + selectedPhoto.getName());
		this.currPhoto = selectedPhoto;
		
		tempTags = new ArrayList<TagType>();
		for (TagType t: selectedPhoto.getPhotoTags()) {
			tempTags.add(new TagType(t.getNameOfType(), t.getArrTags()));
		}
		
		imgView.requestFocus();
		
		if (selectedPhoto.getCaption().equals("-"))
			tfCaption.setText("");
		else
			tfCaption.setText(selectedPhoto.getCaption());
		
		
		tfTagsField.setDisable(true);
		
		initializeListView();
		initializeImageView();
		initializeMenuTags();
		
		btnSaveTags.setDisable(true);
		tfTagsField.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,
		            String oldValue, String newValue) {
		        btnSaveTags.setDisable(false);
		    }
		});
	}
	
	/**
	 * Handles button press events 
	 * @param e ActionEvent
	 */
	public void btnPress(ActionEvent e) {
    	Button btn = (Button) e.getSource();
    	if (btn == btnCancel) {
			cancelProcess();
    	} else if (btn == btnSave) {
			saveProcess();
    	} else if (btn == btnSaveTags) {
    		saveTagsProcess();
    	}
	}

	/**
	 * Saves the tags into temporary storage
	 */
	private void saveTagsProcess() {
		btnSaveTags.setDisable(true);
		ArrayList<String> newTags = new ArrayList<String>();
		
		String oriStr = tfTagsField.getText();
		//////System.out.println("Selected: " + selectedTag.getNameOfType());
		
		String[] values = oriStr.split(";");
		for(int x = 0; x < values.length; x++)
		{
			if (!(values[x].equals("") || values[x].trim().length() <= 0 )) {
				//////System.out.println("["+ values[x] + "]");
				newTags.add(values[x]);
			}
		}
		
		selectedTag.setArrTags(newTags);
		initializeListView();
	}

	/**
	 * Saves all the data back into the storage
	 */
	private void saveProcess() {
		String newCaption = tfCaption.getText();
		//////System.out.println("New caption: " + newCaption);
		if(newCaption.trim().length()> 0)
			currPhoto.setCaption(newCaption);
		else
			currPhoto.setCaption("-");
		
		currPhoto.setPhotoTags(tempTags);
		
		
		currStage.close();
		
	}
 
	/**
	 * Closes the stage
	 */
	private void cancelProcess() {
		
		currStage.close();
	}
	
	
	// ====================================== HELPERS ===========================================
	
	/**
	 * intializes the menubutton
	 */
	private void initializeMenuTags() {
		//ArrayList<TagType> photoTags = currPhoto.getPhotoTags();
		
		for (TagType t: tempTags) {
			MenuItem mi = new MenuItem(t.getNameOfType());
			
			mi.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					dispSelTagItems(event, t);
					mnubtnTagTypes.setText(t.getNameOfType());
					btnSaveTags.setDisable(true);
					selectedTag = t;
					tfTagsField.setDisable(false);
				}
			});
			mnubtnTagTypes.getItems().add(mi);
		}
	}
	
	/**
	 * Displays the selected tag items
	 * @param event selected event
	 * @param t The tag to be displayed
	 */
	private void dispSelTagItems(ActionEvent event, TagType t) {
		String tags = "";
		for (String s: t.getArrTags()) {
			tags = tags + s + ";";
		}
		tfTagsField.setText(tags);
	}
	
	/**
	 * Initializes list view
	 */
	private void initializeListView() {

		lblFileName.setText(currPhoto.getName());
		lblDate.setText(currPhoto.getDate());
		
		ArrayList<TagType> listArr = new ArrayList<TagType>();
		obsList = FXCollections.observableList(listArr);
		
		for (TagType t: tempTags) {
			obsList.add(t);
		}
		listTags.setItems(obsList);
		listTags.refresh();
		
		listTags.setCellFactory(new Callback<ListView<TagType>, javafx.scene.control.ListCell<TagType>>() {
			public ListCell<TagType> call(ListView<TagType> listView){
                return new EditLVCell();
            }
		});
		
	}
	
	/**
	 * Initializes image view
	 */
	private void initializeImageView() {
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
