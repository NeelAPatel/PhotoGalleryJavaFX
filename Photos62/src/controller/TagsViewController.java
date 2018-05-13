package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import objects.TagType;
import objects.User;

public class TagsViewController {
	
	// ================================================ FIELDS =================================================
	@FXML private Button btnAdd, btnRemove;
	
	private User currUser;
	//private Stage currStage;
	private ArrayList<TagType> userTagTypes;
	@FXML private ListView<TagType> listTags;
	private ObservableList<TagType> obsList;
	
	
	//================================================= INNER CLASSES ===========================================
	//DO NOT EDIT
	public class TagsLVCell extends ListCell<TagType> {
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
				TagsLVData data = new TagsLVData();
	            data.setTagType(tagTypeItem);
	            setGraphic(data.getPane());
			}
	    }
	}
	
	public class TagsLVData {
		@FXML private AnchorPane anchorPane;
	    @FXML private Label lblTagType, lblTags;
		/**
		 * Constructor for the custom listview cells
		 */
		public TagsLVData(){
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TagsViewCellLayout.fxml"));
	        fxmlLoader.setController(this);
	        try{fxmlLoader.load();}
	        catch (IOException e) {
	        	exceptionPrint(e);}
		}
		/**
		 * Sets the data from the tagType
		 * @param tagTypeItem the tagtype to extract data from
		 */
		public void setTagType(TagType tagTypeItem) {
			lblTagType.setText(tagTypeItem.getNameOfType());	
		}
		/** 
		 * Gets the anchorpane with all the children in it
		 * @return Anchorpane
		 */
		public AnchorPane getPane() {
			return anchorPane;
		}

	}
	
	// ============================================== METHODS ==========================================================
	/**
	 * initializes the tag window
	 * @param tagsViewStage the current stage
	 * @param user the current user
	 */
	public void start(Stage tagsViewStage, User user) {
		//this.currStage = tagsViewStage;
		this.currUser = user;
		
		userTagTypes = currUser.getAllTagTypes();
		
		ArrayList<TagType> listArr = new ArrayList<TagType>();
		obsList = FXCollections.observableList(listArr);
		
		//populate list
		for (TagType t: userTagTypes) {
			obsList.add(t);
		}
		listTags.setItems(obsList);
		
		//Sets listview's custom cell format
		listTags.setCellFactory(new Callback<ListView<TagType>, javafx.scene.control.ListCell<TagType>>() {
			 @Override
	            public ListCell<TagType> call(ListView<TagType> listView){
	                return new TagsLVCell();
	            }
		});
		listTags.getSelectionModel().select(0);
		
	}
	
	/**
	 * Handles all the button press events in the window
	 * @param e Button press event
	 */
	@FXML // 4/6
	public void btnPress(ActionEvent e) {
		Button btn = (Button) e.getSource();

		if (btn == btnAdd) {
			addTagTypeProcess(); // WORKS - need to check for duplicates
		} else if (btn == btnRemove) {
			removeTagTypeProcess(); // WORKS
		} 
	}

	/**
	 * Adds a tag type to the user's database
	 */
	private void addTagTypeProcess() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add new Tag Type");
		dialog.setHeaderText("Create a new category for tags");
		dialog.setContentText("Type Name: ");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		
		
		if (result.isPresent()) {
			String newTypeName = result.get();
			
			if (!isDuplicateTag(newTypeName)) {
				TagType x = new TagType (result.get());
				//createTile(x);
				// Create Album Object
				userTagTypes.add(x);
				currUser.updateAddTags();
				obsList.add(x);
				
				listTags.refresh();
				listTags.getSelectionModel().select(listTags.getItems().indexOf(x));
				listTags.requestFocus();
				
				
			}
			else
			{
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error - Duplicate tag type");
				alert.setHeaderText("Duplicate tag type!");
				alert.setContentText("The new tag type you have attempted to create already exists.Please try again.");

				alert.showAndWait();
				addTagTypeProcess();
			}
			
			
			
		}
		else{
			//cancel or closed
		}
	}
	

	/**
	 * Removes a tag type from the user's database
	 */
	private void removeTagTypeProcess() {
		int selectedIndex = listTags.getSelectionModel().getSelectedIndex();
		TagType selType = listTags.getItems().get(selectedIndex);
		
		if (selectedIndex > 0 && selectedIndex <= listTags.getItems().size()) {
			currUser.updateDeleteTags(selType);
			listTags.getSelectionModel().select(selectedIndex - 1);
			listTags.getItems().remove(selectedIndex);
			userTagTypes.remove(selectedIndex);
			
			listTags.refresh();
			listTags.requestFocus();
		}
		else if (selectedIndex == 0) {
			currUser.updateDeleteTags(selType);
			listTags.getSelectionModel().select(selectedIndex + 1);
			listTags.getItems().remove(selectedIndex);
			userTagTypes.remove(selectedIndex);
			listTags.refresh();
			listTags.requestFocus();
		}
		
		
		//index is max, then step backward
		//index is middle, step backward
		//index is 0, step forward and remove.
		
		
		
	}

	/**
	 * Checks if two tag types are the same
	 * @param newTypeName the name of the tag type to compare with
	 * @return true tag is duplicate
	 */
	private boolean isDuplicateTag(String newTypeName) {
		for(TagType t: currUser.getAllTagTypes()) {
			if (t.getNameOfType().equals(newTypeName)) {
				return true;
			}
		}
		return false;
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
