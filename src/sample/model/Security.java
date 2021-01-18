package sample.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Security {
    private ArrayList<Account> loginDB;
    private static String currentCustomerId;

    public Security(String filename){
        loginDB = new ArrayList<Account>();  
        loadLoginDB(filename);
    }

    public static String getCurrentCustomerId(){
        return currentCustomerId;
    }

    public ArrayList<Account > getLoginDB(){
        ArrayList<Account> copy = new ArrayList<Account>();
        for(int i=0; i<loginDB.size(); i++){
            Account a = loginDB.get(i);
            copy.add(new Account(a.getLoginId(), a.getPassword(), a.getCustomerId()));
        }
        return copy;
    }

    public void loadLoginDB(String filename) {
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                loginDB.add(new Account(data[0], data[1], data[2]));
            }
            br.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean checkLogin(String loginID, String password){
        for(int i = 0; i < loginDB.size(); i++){
            if(loginDB.get(i).getLoginId().equals(loginID)){
                if(loginDB.get(i).getPassword().equals(password)){
                    System.out.println("WELCOME " + loginDB.get(i).getLoginId());
                    currentCustomerId = loginDB.get(i).getCustomerId();
                    return true;
                }
                else { 
                    System.out.println("INVALID PASSWORD!");
                    return false;
                }
            }
        }
        System.out.println("INVALID LOGIN!");
        return false;
    }
}
