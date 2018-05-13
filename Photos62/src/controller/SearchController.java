package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Album;
import objects.Photo;
import objects.TagType;
import objects.User;

public class SearchController {

	// -- Tag box
	@FXML private MenuButton mnuTags;
	@FXML private TextField tfTagsField;
	@FXML	private Button btnAddTagsToSearch, btnClearTags;
	@FXML	private Label lblTagHelp;

	// -- Name box
	@FXML	private TextField tfFileName, tfCaption;
	@FXML	private Button btnSetNames, btnClearNames;

	// - Date box
	@FXML	private DatePicker dpFrom, dpTo;
	@FXML	private Label lblDateHelp;
	@FXML	private Button btnSetDates, btnClearDates;

	// -- Preview
	@FXML	private ListView<TagType> listTags;
	@FXML	private Label lblFileName, lblCaption, lblFromDate, lblToDate;

	@FXML	private Button btnAndOr;
	@FXML	private Label lblAndOr;
	@FXML	private Button btnClearCriteria, btnSearch;

	private ObservableList<TagType> obsList;
	// -- Search Criteria
	private Calendar calFrom, calTo;

	// -- Misc
	private Stage currStage;
	private User currUser;
	private ArrayList<TagType> tempTags;
	private TagType selectedTag;


	// ================================================= INNER CLASSES

	// DO NOT EDIT

	
	static class XCell extends ListCell<TagType> {
		HBox hbox = new HBox();
		Label label = new Label("");
		Pane pane = new Pane();
		Button button = new Button("Del");
		
		/**
		 * Constructor for creating tags with button inside
		 */
		public XCell() {
			super();

			hbox.getChildren().addAll(label, pane, button);
			HBox.setHgrow(pane, Priority.ALWAYS);
			button.setOnAction(event -> getListView().getItems().remove(getItem()));
		}

		@Override
		/**
		 * updates the cell to contain the contents of tag category 
		 */
		protected void updateItem(TagType item, boolean empty) {
			super.updateItem(item, empty);
			setText(null);
			setGraphic(null);

			if (item != null && !empty) {
				label.setText(item.getNameOfType() + " - " + item.getArrTags().get(0));
				setGraphic(hbox);
			}
		}
	}

	// ============================================== METHODS ==========================================================
	
	/**
	 * initializes the search window
	 * @param stage the current stage
	 * @param user the current User
	 */
	public void start(Stage stage, User user) {
		this.currStage = stage;
		this.currUser = user;

		tfTagsField.setDisable(true);
		btnAddTagsToSearch.setDisable(true);
		calFrom = null;
		calTo = null;
		tfTagsField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				// //System.out.println(" Text Changed to " + newValue + ")\n");
				btnAddTagsToSearch.setDisable(false);
				lblTagHelp.setText("");
			}
		});
		
		dpFrom.valueProperty().addListener((ov, oldValue, newValue) -> {
            lblDateHelp.setText("");
        });
		dpTo.valueProperty().addListener((ov, oldValue, newValue) -> {
            lblDateHelp.setText("");
        });
		
		lblDateHelp.setText("");
		lblTagHelp.setText("");
		
		lblFromDate.setText("None");
		lblToDate.setText("None");
		
		initializeListView();
		initializeMenuTags();
	}

	@FXML
	/**
	 * Handles all the button events in the window
	 * @param e Button press events
	 */
	public void btnPress(ActionEvent e) {
		Button btn = (Button) e.getSource();

		if (btn == btnClearTags) {
			clearTagAreaProcess();
		} else if (btn == btnClearDates) {
			clearDatesAreaProcess();
		} else if (btn == btnSetDates) {
			setDatesToSearchProcess();
		} else if (btn == btnAndOr) {
			andOrProcess();
		} else if (btn == btnClearCriteria) {
			clearCriteriaProcess();
		} else if (btn == btnSearch) {
			searchProcess();
		} else if (btn == btnAddTagsToSearch) {
			addTagsToSearchProcess();
		}

	}

	
	// ==== Tags
	/**
	 * Adds tags to the preview panel 
	 */
	private void addTagsToSearchProcess() {
		String tagEntry = tfTagsField.getText();
		if (tagEntry.trim().length() > 0) {
			ArrayList<String> x = new ArrayList<String>();
			x.add(tagEntry);
			TagType tag = new TagType(selectedTag.getNameOfType(), x);
	
			boolean isDuplicate = false;
	
			for (int i = 0; i < obsList.size(); i++) {
				if (obsList.get(i).getArrTags().get(0).equals(tag.getArrTags().get(0))) {
					isDuplicate = true;
					break;
				}
			}
	
			if (isDuplicate) {
				lblTagHelp.setText("Error: Sorry this Tag value already exists");
				lblTagHelp.setTextFill(Color.RED);
			} else {
				obsList.add(tag);
				lblTagHelp.setText("Tag added");
				lblTagHelp.setTextFill(Color.GREEN);
			}
		}
		else
		{
			lblTagHelp.setText("Error: Blank tag cannot be added to search");
			lblTagHelp.setTextFill(Color.RED);
		}
		listTags.refresh();
		lblTagHelp.requestFocus();

	}
	
	/**
	 * clears the tags in the box
	 */
	private void clearTagAreaProcess() {
		mnuTags.setText("Tags");
		tfTagsField.setText("");
		lblTagHelp.setText("");
	}
	
	// ==== Dates
	/**
	 * clears the dates in the box
	 */
	private void clearDatesAreaProcess() { 
		dpFrom.getEditor().clear();
		dpFrom.setValue(null);
		dpTo.getEditor().clear();
		dpTo.setValue(null);
		lblDateHelp.setText("");
	}

	/**
	 * Sets the dates into the criteria pane
	 */
	private void setDatesToSearchProcess() {

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYY");

		if (dpFrom.getValue() != null && dpTo.getValue() != null) {

			LocalDate ldf = dpFrom.getValue();
			Instant insf = Instant.from(ldf.atStartOfDay(ZoneId.systemDefault()));
			Calendar f = Calendar.getInstance();
			f.setTimeInMillis(0);
			Date df = (Date) Date.from(insf);
			f.setTime(df);

			LocalDate ldt = dpTo.getValue();
			Instant inst = Instant.from(ldt.atStartOfDay(ZoneId.systemDefault()));
			Calendar t = Calendar.getInstance();
			t.setTimeInMillis(0);
			Date dt = (Date) Date.from(inst);
			t.setTime(dt);

			if (f.after(t)) {
				lblDateHelp.setText("Error: FromDate > ToDate");
				lblDateHelp.setTextFill(Color.RED);
			} 
			else 
			{
				lblFromDate.setText(sdf.format(f.getTime()));
				lblToDate.setText(sdf.format(t.getTime()));
				calFrom = f;
				calTo = t;
				lblDateHelp.setText("Both From and To dates set");
				lblDateHelp.setTextFill(Color.GREEN);
			}
		} 
		else if (dpFrom.getValue() != null && dpTo.getValue() == null){
			LocalDate ldf = dpFrom.getValue();
			Instant insf = Instant.from(ldf.atStartOfDay(ZoneId.systemDefault()));
			Calendar f = Calendar.getInstance();
			f.setTimeInMillis(0);
			Date df = (Date) Date.from(insf);
			f.setTime(df);
			lblFromDate.setText(sdf.format(f.getTime()));
			calFrom = f;
			lblDateHelp.setText("From date set");
			lblDateHelp.setTextFill(Color.GREEN);
			lblToDate.setText("None");
			calTo = null;
			
		} 
		else if (dpTo.getValue() != null && dpFrom.getValue() == null) {
			LocalDate ldt = dpTo.getValue();
			Instant inst = Instant.from(ldt.atStartOfDay(ZoneId.systemDefault()));
			Calendar t = Calendar.getInstance();
			t.setTimeInMillis(0);
			Date dt = (Date) Date.from(inst);
			t.setTime(dt);
			lblToDate.setText(sdf.format(t.getTime()));
			calTo = t;
			lblDateHelp.setText("To date set");
			lblDateHelp.setTextFill(Color.GREEN);
			lblFromDate.setText("None");
			calFrom = null;

		}
		else if (dpFrom.getValue() == null && dpTo.getValue() == null) {
			lblDateHelp.setText("No dates set");
			lblDateHelp.setTextFill(Color.GREEN);
			
			lblFromDate.setText("None");
			calFrom = null;
			lblToDate.setText("None");
			calTo = null;
			
			
		}

	}

	
	// ==== Search panel
	/**
	 * Toggles between and/or
	 */
	private void andOrProcess() {
		if (btnAndOr.getText().equals("AND")) {
			btnAndOr.setText("OR");
			lblAndOr.setText("tagA OR tagB OR tagC...");
		} else if (btnAndOr.getText().equals("OR")) {
			btnAndOr.setText("AND");
			lblAndOr.setText("tagA AND tagB AND tagC...");
		}
	}

	/**
	 * Clears all the set criteria
	 */
	private void clearCriteriaProcess() {
		calFrom = null;
		calTo = null;
		tempTags = new ArrayList<TagType> ();
		obsList = FXCollections.observableArrayList(tempTags);
		listTags.setItems(obsList);
		
		
		listTags.refresh();
		lblFromDate.setText("");
		lblToDate.setText("");
		
		
		lblAndOr.requestFocus();
		
		
	}

	/**
	 * Searches based on the criteria set.
	 */
	private void searchProcess() {
		// Setup
		Album searchResults = new Album("Search Results");
		int dateSearchState = 0; // 0 = both are empty, 1 = from, 2 = to, 3 = both
		int andOr = -1; // 0 = or, 1 = and
		//int tagsCheck = 0; // 1 = check for tags

		boolean datePass = false;
		boolean tagsPass = false;
				
		// AND/OR State
		if (btnAndOr.getText().equals("AND"))
			andOr = 0;
		if (btnAndOr.getText().equals("OR"))
			andOr = 1;
		
		// CALENDAR STATE
		if (calFrom != null && calTo != null)
			dateSearchState = 3;
		else if (calFrom != null && calTo == null)
			dateSearchState = 1;
		else if (calFrom == null && calTo != null)
			dateSearchState = 2;
		else if (calFrom == null && calTo == null)
			dateSearchState = 0;
		
		//TAGS STATE
		/*if (obsList.size() > 0) 
			tagsCheck = 1;*/
		
		for (Album myAlbum : currUser.getAllAlbums()) {
			for(Photo myPhoto : myAlbum.getAllPhotos()) {
				/*Process: 
				 * - Test Daterange
				 * - TestTags
				 * if pass
				 * 
				 */
				
				datePass = checkDateRange(myPhoto, calFrom, calTo, dateSearchState);
				////System.out.println("HELLO??1");
				tagsPass = checkTagExistance(myPhoto, andOr);
				////System.out.println("HELLO??2");
				if(datePass && tagsPass) {
					searchResults.addToAlbum(new Photo(myPhoto.getPhotoFile(), myPhoto.getAlbumName(), myPhoto.getPhotoTags()));
				}
				
			}// Photo			
		}//AlbumFor
		
		
		
		openResults(searchResults);
		
		
	}
	
	/**
	 * Opens the results in a new window
	 * @param searchResults An album consisting of photos that fit into the criteria given
	 */
	private void openResults(Album searchResults) {
		if (searchResults.getAllPhotos().size()>0) {
			try {
				//Loads photospane fxml
				FXMLLoader photosLoader = new FXMLLoader();
				photosLoader.setLocation(getClass().getResource("/fxml/PhotosPage.fxml"));
				AnchorPane root = (AnchorPane) photosLoader.load();
				
				//sets the photos stage
				Stage photosStage = new Stage();
				photosStage.setTitle("Photos in " + searchResults.getAlbumName());
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
					photosController.setAlbumCase(searchResults); // sends current album value
					photosController.start(photosStage, 1);
					photosStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		
				         @Override
				         public void handle(WindowEvent event) {
				             Platform.runLater(new Runnable() {
		
				                 @Override
				                 public void run() {
				                     //System.out.println("PhotosWindow Closed by clicking (X)");
				                     // ask if you want to log out and such
				                     //System.exit(0);
				                     
				                    ////System.out.println("Num of Photos: " +selectedAlbum.getAllPhotos().size());
				                 }
				             });
				         }
				     });
				photosStage.showAndWait();
				currStage.close();
				
				// NEED TO UPDATE THE STATS ON THE ALBUM UPON CLOSE OF ALBUM PANE
				//updateTile();
				
				
			} catch (IOException e) {
				exceptionPrint(e);
			}
		}
		else
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("No Photos");
			alert.setHeaderText("No Photos found");
			alert.setContentText("No photos matched your search terms.");

			alert.showAndWait();
		}
	}
	
	/**
	 * Checks if the tags exists in the photo
	 * @param myPhoto the photo to test on
	 * @param andOr toggle between and/or
	 * @return true if tags exist and pass the and/or test
	 */
	private boolean checkTagExistance(Photo myPhoto, int andOr) {
		if (andOr == 1) {
			//OR stance
			boolean check = false;
			for (TagType pTagType: myPhoto.getPhotoTags()) {
				for (TagType sTagType : obsList) {
					if (pTagType.getNameOfType().equals(sTagType.getNameOfType())){
						for(String strTag : pTagType.getArrTags()) {
							if (strTag.equals(sTagType.getArrTags().get(0))) {
								check = true;
							}
							else
							{
								////System.out.print("  <<< False");
							}
							
						}
						
					}					
				}
			}
			
			if (check)
				return true;
		}
		else if (andOr == 0) {
			////System.out.println("Testing AND");
			
			ArrayList<String> andOps = new ArrayList<String>();
			
			//boolean added = false;
			
			
			for(TagType sType: obsList) {
				for (TagType pType: myPhoto.getPhotoTags()) {
					
					if (sType.getNameOfType().equals(pType.getNameOfType())) // Same category 
					{
						// check if array of p has s in it
						ArrayList<String> pTags = pType.getArrTags();
						ArrayList<String> sTags = sType.getArrTags();
						
						////System.out.println(sTags.get(0));
						if (pTags.contains(sTags.get(0))) {
							andOps.add("1");
							////System.out.print("1 ");
						}
						//else
						//	//System.out.print("0 ");
						
						
						
						
					}

				}
			}
			
			boolean check = false;
			if (obsList.size() == andOps.size()) {
				check = true;
			}
			else
			{
				check = false;
			}
			
			if (check) {
				////System.out.print("<<True!");
				return true;
				
			}
			
		}
		return false;
	}

	/**
	 * Checks if the photo falls within the date range
	 * @param myPhoto photo being tested on
	 * @param calFrom from date
	 * @param calTo to date
	 * @param dateSearchState state of range format 0 = none; 1 = From only; 2 - To only; 3 - both
	 * @return true if photo is within range
	 */
	private boolean checkDateRange(Photo myPhoto, Calendar calFrom, Calendar calTo, int dateSearchState) {
		
		if (dateSearchState == 0)
			return true;
		else if (dateSearchState == 1) {
			if (myPhoto.getRawDate().after(calFrom) || isSameDay(myPhoto.getRawDate(),calFrom))
				return true;			
		}
		else if (dateSearchState == 2) {
			if (myPhoto.getRawDate().before(calTo) || isSameDay(myPhoto.getRawDate(),calTo))
				return true;
		}
		else if (dateSearchState == 3) {
			if ((myPhoto.getRawDate().after(calFrom) || isSameDay(myPhoto.getRawDate(),calFrom)) && (myPhoto.getRawDate().before(calTo) || isSameDay(myPhoto.getRawDate(),calTo)))
				return true;
		}		
		return false;
	}

	// ========================== HELPERS ======================================
	/**
	 * Initializes the tags' listview
	 */
	private void initializeListView() {
		tempTags = new ArrayList<TagType>();
		obsList = FXCollections.observableList(tempTags);

		listTags.setItems(obsList);
		listTags.refresh();

		/*
		 * listTags.setCellFactory(new Callback<ListView<TagType>,
		 * javafx.scene.control.ListCell<TagType>>() { public ListCell<TagType>
		 * call(ListView<TagType> listView){ return new SearchLVCell(); } });
		 */

		listTags.setCellFactory(param -> new XCell());

	}

	/**
	 * intializes the menubutton with tagtypes
	 */
	private void initializeMenuTags() {
		// ArrayList<TagType> photoTags = currPhoto.getPhotoTags();
		tempTags = new ArrayList<TagType>();

		for (TagType t : currUser.getAllTagTypes()) {
			MenuItem mi = new MenuItem(t.getNameOfType());

			mi.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// dispSelTagItems(event, t);
					mnuTags.setText(t.getNameOfType());
					// btnSaveTags.setDisable(true);
					selectedTag = t;
					tfTagsField.setDisable(false);
					tfTagsField.setText("");
				}

			});

			mnuTags.getItems().add(mi);

		}
	}
	
	/**
	 * Checks if both dates are the same day
	 * @param cal1 date 1
	 * @param cal2 date 2
	 * @return true if both are same dates
	 */
	private boolean isSameDay(Calendar cal1, Calendar cal2) {
		boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
		
		return sameDay;
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
