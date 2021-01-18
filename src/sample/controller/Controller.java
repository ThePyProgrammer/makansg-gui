package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.model.*;

import java.io.IOException;
import java.util.*;

public class Controller {
    @FXML
    private Label loginLabel;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button log;
    @FXML
    private Label shoppingLabel;
    @FXML
    private TextArea shopping;
    @FXML
    private Button checkout;
    @FXML
    private Button clear;
    @FXML
    private Label postalLabel;
    @FXML
    private TextField postalField;
    @FXML
    private Button search;
    @FXML
    private Label itemsLabel;
    @FXML
    private TextArea items;
    @FXML
    private Label itemLabel;
    @FXML
    private TextField food;
    @FXML
    private Button addBtn;
    @FXML
    private Button removeBtn;
    @FXML
    private Hyperlink aboutBtn;

    private static Database db = new Database("src/sample/Customer.txt", "src/sample/Supplier.txt", "src/sample/Postal.txt");
    private static Security sec = new Security("src/sample/Secure.txt");
    private static Customer customer;
    private static String postal;
    private static Scanner s;
    private static ArrayList<Supplier> suppliers;

    private void visibilise(Boolean b) {
        String id = b? "visible":"visiblechange";
        shoppingLabel.setId(id);
        shopping.setId(id);
        checkout.setId(id);
        clear.setId(id);
        postalLabel.setId(id);
        postalField.setId(id);
        search.setId(id);
        itemsLabel.setId(id);
        items.setId(id);
        itemLabel.setId(id);
        food.setId(id);
        addBtn.setId(id);
        removeBtn.setId(id);
        aboutBtn.setId(id);
    }
    @FXML
    private void login(ActionEvent e) {
        String uname = username.getText(), pw = password.getText();
        if (sec.checkLogin(uname, pw)) {
            customer = db.getCustomer(Security.getCurrentCustomerId());
            log.setText("Logout");
            log.setOnAction(event -> logout(event));
            visibilise(true);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Login Successful!");
            alert.setContentText("Welcome to MakanSG, "+uname+"!");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
            username.setEditable(false);
            password.setEditable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid Login!");
            alert.setContentText("Looks like your Username and/or Password are invalid! '"+uname+"' and '"+pw+"' just don't match.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }
    @FXML
    private void logout(ActionEvent e) {
        visibilise(false);
        log.setText("Login");
        log.setOnAction(event -> login(event));
        username.setEditable(true);
        password.setEditable(true);
    }
    @FXML
    private void setPostal(ActionEvent e) {
        postal = postalField.getText();
        if (!db.checkPostal(postal)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid Postal Code!");
            alert.setContentText("Please enter a valid postal code, not '"+postal+"'");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } else if (!db.checkWithinDistrictArea(postal)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid District Area!");
            alert.setContentText("Postal code '"+postal+"' is not within the district area of your delivery address!");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } else {
            var relatedSectors = db.getRelatedSector(postal);
            String printing = "Searching for food items at "+db.getArea(postal)+".\nRelated sectors are "+relatedSectors+"...\n";
            db.addListedItems(postal);
            suppliers = db.getListedItems();
            Collections.sort(suppliers, new SortByID());
            String current = "";
            int index = 0;
            for(Supplier supplier: suppliers) {
                index++;
                if (current.length() == 0 || !supplier.getSupplierID().equals(current)) {
                    current = supplier.getSupplierID();
                    printing += "\n"+current+"\n------------------------------------------------\n";
                }
                printing += String.format("[%3d] %s %-25s$%.2f%n", index, supplier.getFoodID(), supplier.getFood(), supplier.getPrice());
                items.setText(printing);
            }
        }
    }
    @FXML
    public void add(ActionEvent e) {
        String entry = food.getText();
        try {
            int value = Integer.parseInt(entry)-1;
            if (value < 0 || value >= suppliers.size()) {
                throw new Exception("");
            } else {
                db.getShoppingCart().add(suppliers.get(value));
                shopping.setText(db.getOrderSummary());
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid Entry!");
            alert.setContentText("Please enter a valid food item number.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }
    @FXML
    public void remove(ActionEvent e) {
        String entry = food.getText();
        try {
            int value = Integer.parseInt(entry)-1;
            if (value < 0 || value >= db.getShoppingCart().size()) {
                throw new Exception("");
            } else {
                var temp = db.getShoppingCart().get(value);
                db.getShoppingCart().remove(value);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Item removed successfully!");
                alert.setContentText("Item "+temp+" has been removed!");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
                shopping.setText(db.getOrderSummary());
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Invalid Entry!");
            alert.setContentText("Please enter a valid food item number.");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }
    @FXML
    public void clearIt(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Shopping Cart");
        alert.setHeaderText("Are you sure you wish to clear your shopping cart?");
        alert.setContentText("If you click OK, there's no going back. Unless of course you key in all the numbers in the exact same order. But you should get the point. Don't try to make this boring!");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (alert.showAndWait().get() == ButtonType.OK) {
            db.getShoppingCart().clear();
            shopping.setText("");
        }
    }
    @FXML
    public void doCheckout(ActionEvent e) {
        db.writeTransaction("src/sample/Transaction.txt");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order confirmed");
        alert.setHeaderText("Transaction has been completed completely.");
        alert.setContentText("YOUR FOOD WILL BE DELIVERED TO:\n\n"+customer.getName()+'\n'+customer.getAddress().toUpperCase()+"\nSINGAPORE "+customer.getPostalCode()+"\nThank you for ordering with us. Hope to see you again!");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void about(ActionEvent e) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("about.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle("About the Programmer");
        stage.getIcons().add(new Image(("file:src/LOGO.png")));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(aboutBtn.getScene().getWindow());
        stage.showAndWait();
    }
}

class SortByID implements Comparator<Supplier>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Supplier a, Supplier b)
    {
        return (a.getSupplierID().compareTo(b.getSupplierID()));
    }
}