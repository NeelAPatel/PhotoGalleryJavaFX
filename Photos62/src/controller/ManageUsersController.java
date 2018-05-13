package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import objects.User;


public class ManageUsersController {
	// ================================================ FIELDS =================================================
	// -- Stage
	private Stage currStage;
	
	// -- UserInformation
	private ArrayList<User> userList;
	int totalUsers, currUserCount;
	
	//Buttons
	@FXML private Button btnAddUser, btnRenameUser, btnDeleteUser, btnClose; 

	@FXML private ListView<User> listviewUsers;
	private ObservableList<User> obsList;
	// ======================================== INNER CLASSES =================================================
	public class ManageUsersLVCell extends ListCell<User> {		
		/* (non-Javadoc)
		 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
		 */
		@Override
	    public void updateItem(User userItem, boolean empty){
	        super.updateItem(userItem,empty);
	 		if (userItem == null || empty) {
				setText(null);
				setGraphic(null);
			}
			else{
				ManageUsersLVData data = new ManageUsersLVData();
	            data.setUser(userItem);
	            setGraphic(data.getPane());
			}
	    }
	}
	
	
	public class ManageUsersLVData {
		@FXML private AnchorPane anchorPane;
	    @FXML private Label lblUsername, lblPass, lblCensored;
	    @FXML private Button btnShowHide;
	    /**
	     * Custom listview datatype for displaying users
	     */
	    public ManageUsersLVData(){
	        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ManageUsersCellLayout.fxml"));
	        fxmlLoader.setController(this);
	        try{
	            fxmlLoader.load();
	            lblPass.setVisible(false);
	            lblCensored.setText("⚫⚫⚫⚫⚫"); 
	        }
	        catch (IOException e)
	        {
	        	exceptionPrint(e);
	        }
	    }
	    
	    /**
	     * Sets the values to the data from userItem
	     * @param userItem A user to extract data from and display it in a listview cell
	     */
	    public void setUser(User userItem) {
	    	lblUsername.setText(userItem.getUsername());
	    	lblPass.setText(userItem.getPassword());
	    	lblCensored.setText("⚫⚫⚫⚫⚫");
	    	lblPass.setVisible(false);
	    }
	    
	    /**
	     * Handles button click events that occur inside the listview cell
	     * @param e Button click event
	     */
	    @FXML
	    private void btnPress(ActionEvent e) {
	    	Button btn = (Button) e.getSource();
			if (btn == btnShowHide)
				showHideProcess();
	    }

		/**
		 * Shows and hides the password
		 */
		private void showHideProcess() {		
			if (lblPass.isVisible()){
				lblPass.setVisible(false);
				lblCensored.setVisible(true);
				btnShowHide.setText("S");
			}
			else{
				lblPass.setVisible(true);
				lblCensored.setVisible(false);
				btnShowHide.setText("H");
			}
		}
		
		/** 
		 * Returns the anchorpane containing all of the data
		 * @return AnchorPane
		 */
		public AnchorPane getPane() { 
			return anchorPane;
		}
	}

	
	// ============================================== METHODS ==========================================================
	/**
	 * Initializes the Manage User window
	 * @param mngUStage the current stage
	 */
	public void start(Stage mngUStage) {
		currStage = mngUStage;
		
		ArrayList<User> listArr = new ArrayList<User>();
		obsList = FXCollections.observableList(listArr);
		
		//Populate obsList
		for (User u: userList) {
			obsList.add(u);
		}
		listviewUsers.setItems(obsList);
		
		//Sets listview's custom cell format
        listviewUsers.setCellFactory(new Callback<ListView<User>, javafx.scene.control.ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> listView)
            {
                return new ManageUsersLVCell();
            }
        });
        listviewUsers.requestFocus();
        if(obsList.size()>0) {
        	listviewUsers.getSelectionModel().select(0);
        }
        btnVisibility();
	}
	
	/**
	 * Handles button press events
	 * @param e Button press event
	 */
	@FXML 
	public void btnPress(ActionEvent e) {
		Button btn = (Button) e.getSource();

		if (btn == btnAddUser) {
			addUserProcess(); // WORKS - need to check for duplicates
		} else if (btn == btnRenameUser) {
			renameUserProcess(); // WORKS
		} else if (btn == btnDeleteUser) {
			deleteUserProcess();
		} else if (btn == btnClose) {
			closeWindowProcess();
		}
	}
	
	/**
	 * Adds a user to the userlist 
	 */
	private void addUserProcess() {
		
		User newUser = getNewUser();
		
		if (newUser.getUsername().equals("") && newUser.getPassword().equals("")){
			
		}
		else if (newUser.getUsername().equals("") && !(newUser.getPassword().equals(""))){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Blank Username!");
			alert.setContentText("The User you have created has a blank Username");
			
			alert.showAndWait();
			addUserProcess();	
		}
		else if (!(newUser.getUsername().equals("")) && (newUser.getPassword().equals(""))){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Blank Password!");
			alert.setContentText("The User you have created has a blank password");
			
			alert.showAndWait();
			addUserProcess();	
		}
		else if (isDuplicate (newUser)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Duplicate Usernames!");
			alert.setContentText("The User you have created has a name that already exists in the database. Please choose a different username.");
			
			alert.showAndWait();
			addUserProcess();			
		}
		else
		{
			userList.add(newUser);
			obsList.add(newUser);
			listviewUsers.getSelectionModel().select(newUser);
			listviewUsers.requestFocus();
			listviewUsers.refresh();
			totalUsers++;
			currUserCount++;
			btnVisibility();
		}
		
	}

	/**
	 * Checks if a user already exists with the same name
	 * @param newUser a User to be compared against
	 * @return true if username matches
	 */
	private boolean isDuplicate(User newUser) {
		for (User u: userList) {
			if (u.getUsername().equals(newUser.getUsername())){
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets a new user's details from the user and enters it into the database
	 * @return a newly created user
	 */
	private User getNewUser() {
		User temp = new User(currUserCount, "", "");
		
		
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("New User");
		dialog.setHeaderText("Create a new User");

		// Set the icon (must be included in the project).
		//dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Create User", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> username.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(username.getText(), password.getText());
		    }
		    return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
		    ////System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
		    temp.setUsername(usernamePassword.getKey());
		    temp.setPassword(usernamePassword.getValue());
		});
		
		return temp;
	}

	/**
	 * Renames a user to something else
	 */
	private void renameUserProcess() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Rename User");
		dialog.setHeaderText("What do you want to rename this user to?");
		dialog.setContentText("New name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		   // //System.out.println("Your name: " + result.get());
			
			
		    
		    userList.get(listviewUsers.getSelectionModel().getSelectedIndex()).setUsername(result.get());
		    listviewUsers.getSelectionModel().getSelectedItem().setUsername(result.get());
		    listviewUsers.refresh();
		    
		}
		btnVisibility();
	}

	/**
	 * Deletes a user and their files from the database
	 */
	private void deleteUserProcess() {
		User selectedUser = listviewUsers.getSelectionModel().getSelectedItem();
		int selectedIndex = listviewUsers.getSelectionModel().getSelectedIndex();
		
		if (selectedIndex > 0 && selectedIndex <= listviewUsers.getItems().size()) {
			listviewUsers.getSelectionModel().select(selectedIndex - 1);
			listviewUsers.getItems().remove(selectedIndex);
			userList.remove(selectedIndex);
			listviewUsers.refresh();
			listviewUsers.requestFocus();
		}
		else if (selectedIndex == 0) {
			listviewUsers.getSelectionModel().select(selectedIndex + 1);
			listviewUsers.getItems().remove(selectedIndex);
			userList.remove(selectedIndex);
			listviewUsers.refresh();
			listviewUsers.requestFocus();
		}
		
		
		obsList.remove(selectedUser);
		userList.remove(selectedUser);
	}
	
	/**
	 * Closes the Manage Users window
	 */
	private void closeWindowProcess() {
		currStage.close();
	}

	/**
	 * Manages button visibility by enabling and disabling as needed 
	 */
	private void btnVisibility() {
		if(obsList.size() == 0) {
			btnRenameUser.setDisable(true);
			btnDeleteUser.setDisable(true);
		}
		else if (obsList.size() > 0 && listviewUsers.getSelectionModel().getSelectedIndex() > -1) {
			btnRenameUser.setDisable(false);
			btnDeleteUser.setDisable(false);
		}
	}

	/**
	 * Sets the current Stage
	 * @param dialogStage the current Stage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.currStage = dialogStage;
	}

	/**
	 * Recieves the userlist and releavant information
	 * @param userList the list of users
	 * @param totalUsers the total amount of users
	 * @param currUserCount the highest user count
	 */
	public void setUserList(ArrayList<User> userList, int totalUsers, int currUserCount) {
		this.userList = userList;
		this.totalUsers = totalUsers;
		this.currUserCount = currUserCount;
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
