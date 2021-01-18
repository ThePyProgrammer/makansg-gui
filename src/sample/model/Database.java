package sample.model;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Database {
	private ArrayList<Customer> customerDB; // to store customer's address
	private ArrayList<Supplier> supplierDB; // to store all data in Supplier.txt
	private ArrayList<Postal> postalDB;	// to store all data in Postal.txt
	private ArrayList<Supplier> listedItemsDB; // to store items found within the district area
	private ArrayList<Supplier> shoppingCartDB; // to store items selected by customer

	public Database(String customerFile, String supplierFile, String postalFile){
		supplierDB = new ArrayList<Supplier>();
		postalDB = new ArrayList<Postal>();
		customerDB = new ArrayList<Customer>();
		listedItemsDB = new ArrayList<Supplier>();
		shoppingCartDB = new ArrayList<Supplier>();
		loadCustomerDB(customerFile);
		loadSupplierDB(supplierFile);
		loadPostalDB(postalFile);
	}

    public ArrayList<Supplier> getSupplier(){return this.supplierDB;}
	public ArrayList<Supplier> getListedItems(){
		return this.listedItemsDB;
	}
	public ArrayList<Supplier> getShoppingCart(){
		return this.shoppingCartDB;
	}

	public void loadCustomerDB(String filename){
		BufferedReader br = null;
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				Customer c = new Customer(data[0], data[1], data[2], data[3]);
				customerDB.add(c);
			}
			br.close();
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	public void loadSupplierDB(String filename){
		BufferedReader br = null;
		try{
			String line;
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				Supplier s = new Supplier(data[0], data[1], data[2], Double.parseDouble(data[3]));
				supplierDB.add(s);
			}
			br.close();
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	public void loadPostalDB(String filename) {
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				postalDB.add(new Postal(data[0], data[1]));
			}
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} 
	}
	
	public Customer getCustomer(String customerId) {
		Customer current = null;
		for(Customer c: customerDB) {
			if (c.getCustomerId().equals(customerId)) {
				current = new Customer(c.getCustomerId(), c.getName(),c.getAddress(),c.getPostalCode());
			}
		}
		return current;
	}

	public boolean checkPostal(String p){
		int sector = 0;
		if (p.length() != 6)
			return false;
		for(int i=0; i< p.length(); i++)
			if(!Character.isDigit(p.charAt(i)))
					return false;
		sector = Integer.parseInt(p.substring(0, 2));
		if(!(sector >= 1 && sector <=82) || sector == 74)
			return false;
		return true;
	}

	public String getArea(String postalCode ){
		String sector = postalCode.substring(0,2);
		for (Postal p: postalDB){
			if(p.getSectorCode().equals(sector))
				return p.getDistrictArea();
		}
		return "";
	}

	public ArrayList<String> getRelatedSector(String postalCode ){
		ArrayList<String> related = new ArrayList<String>();
		String district = getArea(postalCode);
		for (Postal p: postalDB){
			if(p.getDistrictArea().equals(district))
				related.add(p.getSectorCode());
		}
		return related;
	}

	public boolean checkWithinDistrictArea(String postalCode){
		Customer current = new Customer();
		ArrayList<String>  listOfRelatedSectorCode = new ArrayList<>();
		current = getCustomer(Security.getCurrentCustomerId());  
		listOfRelatedSectorCode = getRelatedSector(postalCode); 
		for(String d: listOfRelatedSectorCode){
			if(current.getPostalCode().substring(0,2).equals(d)) 
				return true;
		}
		return false;
	}

	public void addListedItems(String postalCode){
		for(String sector: getRelatedSector(postalCode)){
			for(Supplier s: getSupplier()){
				if(s.getSupplierID().substring(0,2).equals(sector))
					listedItemsDB.add(s);
			}
		}
	}

	public String getOrderSummary() {
		String s = "";
		double total = 0;
		for(int i = 0; i < shoppingCartDB.size(); i++) {
			var temp = shoppingCartDB.get(i);
			total += temp.getPrice();
			s += String.format("%2d) %s %-25s$%.2f%n", i+1, temp.getFoodID(), temp.getFood(), temp.getPrice());
		}
		s += String.format("%nYOUR TOTAL BILL IS : $%.2f %n", total);
		return s;
	}

	public void writeTransaction(String filename){
		try{
			PrintWriter output = new PrintWriter(new FileOutputStream(filename, true));
			for (Supplier s: shoppingCartDB){
				Date now = new Date();
				output.print(now + " ");
				output.print(Security.getCurrentCustomerId() + " ");
				output.println(s);
			}
			output.close();
		}  catch (IOException ex) { 
			System.out.println(ex.getMessage());
		}
	}

	//Given, no edits required.
	public static void displayLogo(){
		System.out.println("_______ _______ _     _ _______ __   _  _____   ______");
		System.out.println("|  |  | |_____| |____/  |_____| | \\  | |_____  |  ____");
		System.out.println("|  |  | |     | |    \\_ |     | |  \\_|  _____| |_____|");
		System.out.println();
	}
}
