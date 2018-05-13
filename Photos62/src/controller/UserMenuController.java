package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import objects.User;

public class UserMenuController {
	// ================================================ FIELDS =================================================
	@FXML private Button btnUShowHide, btnUEditPass, btnUSavePass, btnLogout, btnUCancel;
	@FXML private TextField tfUsername, tfPass;
	@FXML private PasswordField passField;
	int state = 0;
	
	private Stage currStage;
	private User currUser;
	private ArrayList<User> userList;
	private int totalUsers, currUserCount;
	// ============================================== METHODS ==========================================================
	public void start(Stage mainStage, User currUser, ArrayList<User> userList, int totalUsers, int currUserCount) {
		mainStage.setTitle("User Menu");
		this.currStage = mainStage;
		this.currUser = currUser;
		this.userList = userList;
		this.totalUsers = totalUsers;
		this.currUserCount = currUserCount;
		tfUsername.setText(currUser.getUsername());
		tfPass.setText(currUser.getPassword());
		passField.setText(currUser.getPassword());
		passField.setDisable(true);
		tfPass.setDisable(true);
		tfPass.setVisible(false);
		btnUSavePass.setVisible(false);
	}
	
	@FXML // 4/6
	public void btnPress(ActionEvent e) {
		Button btn = (Button) e.getSource();

		if (btn == btnUShowHide) {
			showHideProcess();
		} else if (btn == btnUEditPass) {
			editPassProcess();
		} else if (btn == btnUSavePass) {
			savePassProcess(); 
		} else if (btn == btnLogout) {
			logoutProcess();
		} else if (btn == btnUCancel ) {
			cancelProcess();
		}
	}

	private void cancelProcess() {
		if (state == 0) {
			currStage.close();
		}
		else {
			// revert to state 0
			state = 0;
			// revert password
			tfPass.setText(currUser.getPassword());
			passField.setText(currUser.getPassword());
		}
	}

	private void logoutProcess() {
		try {
			
			FileWriter fw = new FileWriter("appStats.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
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
		//System.out.println("AppData exported on program close");
		
		
		//Users List
		
		try {
			File usersFile = new File("usersList.txt");
			FileOutputStream userOut = new FileOutputStream(usersFile);
			ObjectOutputStream oout = new ObjectOutputStream(userOut);
			for (User u : userList) {
				oout.writeObject(u);
			}
			oout.close();
			//System.out.println("Admin written");

			//ObjectInputStream ois = new ObjectInputStream(new FileInputStream("usersList.txt"));
			// //System.out.println("" + ois.readObject());
			//ois.close();
	
		} catch (Exception e) {
			exceptionPrint(e);
		}
		////System.out.println("UserData exported THROUGH LOGOUT BUTTON on program close");
		currUser.logout = true;
		currStage.close();
	}

	private void savePassProcess() {
		state = 0;
		btnUEditPass.setVisible(true);
		tfPass.setDisable(true);
		passField.setDisable(true);
		btnUSavePass.setVisible(false);
		
		if (passField.isVisible())
			currUser.setPassword(passField.getText());
		else
			currUser.setPassword(tfPass.getText());
		
		
	}

	private void editPassProcess() {
		state = 1;
		btnUEditPass.setVisible(false);
		btnUSavePass.setVisible(true);
		btnUSavePass.requestFocus();
		tfPass.setDisable(false);
		passField.setDisable(false);
		
		
	}

	private void showHideProcess() {
		if (passField.isVisible()) {
			//show password by hiding
			tfPass.setText(passField.getText());
			passField.setVisible(false);
			tfPass.setVisible(true);
			btnUShowHide.setText("H");
		}
		else 
		{
			//hide password
			passField.setText(tfPass.getText());
			passField.setVisible(true);
			tfPass.setVisible(false);
			btnUShowHide.setText("S");
			
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
