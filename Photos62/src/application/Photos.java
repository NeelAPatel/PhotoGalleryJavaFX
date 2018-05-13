package application;
	
import java.util.ArrayList;

import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

  

public class Photos extends Application {
	
	private LoginController loginController;
	ArrayList<Node> components = new ArrayList<Node>(); 
	
	/**
	 * Starts the Photos application.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception{
		
		//Loads Login page
		FXMLLoader loginLoader = new FXMLLoader();
		loginLoader.setLocation(getClass().getResource("/fxml/LoginPage.fxml"));
		AnchorPane root = (AnchorPane) loginLoader.load();
		loginController = loginLoader.getController();
		boolean resetApp = false;
		
		
		
		// WARNING THIS WILL RESET THE APP. 
		resetApp = true; 
		
		
		loginController.start(primaryStage, resetApp);
		
		//Adds CSS for login page
		Scene scene = new Scene(root);
		String globalCSS = getClass().getResource("/css/ZGlobalElementsCSS.css").toExternalForm();
		//String uniqueCSS = getClass().getResource("/css/LoginPageCSS.css").toExternalForm();
		scene.getStylesheets().addAll(globalCSS);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
	}
	
	/**
	 * Main method of Photos.
	 * @param args Array of Runtime arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Overrides and intercepts the default stop process of the program to export program data upon closure.
	 */
	@Override
	public void stop(){
		// Overrides/intercepts built in sort method and exports programs' data
	   // System.out.println("LoginWindow Closed by clicking (X)");
	  //  System.out.println("Stage is closing");
	    loginController.exportProgramData();
	    
	   // System.out.println("=|=|= PROGRAM CLOSED SAFELY =|=|=");
	}
}
