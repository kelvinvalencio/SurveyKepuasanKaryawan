/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;
import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Kelvin Valencio
 */
public class Model {    
    //VALIDATED_ID mengandung ID yang telah divalidasi berhasil login
    //dan dimanipulasi oleh fungsi getValidatedId()
    private String VALIDATED_ID = null;
    private Connection connection = null;
    private PreparedStatement statement = null;

    //Pengaturan singleton design pattern
    public static Model INSTANCE = null;
    public static Model getModel(){
        if(INSTANCE == null){
            INSTANCE = new Model();
        }
        return INSTANCE;
    }
    
    //Pengaturan singleton: private constructor
    private Model(){
        boolean isDbAlreadyCreated = false;
        
        //Cek driver
        try{
            System.out.println("Cek driver..");
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver ditemukan.\n");
        }catch(Exception exception){
            JOptionPane.showMessageDialog(null, "Driver gagal di load. Database tidak dapat terhubung. Aplikasi gagal berfungsi dengan baik.");
            System.out.println("Exception on Model - cons - driver_check \n" + exception);
        }
        
        //Cek dan inisialisasi koneksi
        try{
            System.out.println("Cek koneksi..");
            connection = DriverManager.getConnection(InfoKoneksi.URL_ROOT, InfoKoneksi.user, InfoKoneksi.password);
            System.out.println("Koneksi berhasil dibuat.\n");
        }catch(SQLException exception){
            System.out.println("Koneksi gagal dibuat.");
            JOptionPane.showMessageDialog(null, "Koneksi gagal dibuat. Pastikan terdapat user \"root\", password kosong dan port pada 3306.");
            JOptionPane.showMessageDialog(null, "Aplikasi gagal berfungsi.");
            System.out.println("Exception on Model - cons - connection_check \n" + exception);
        }
        
        //Cek database
        try{
            System.out.println("Cek database dan tabel yang diperlukan..");
             
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW DATABASES;");
            
            while(resultSet.next()){
                String result = resultSet.getString(1);
                if(result.compareTo("survey") == 0){
                    isDbAlreadyCreated = true;
                    break;
                }
            }
            
            if(!isDbAlreadyCreated){
                System.out.println("Database belum ada. Membuat database yang diperlukan..");
                statement.execute("CREATE DATABASE survey;");
                
                resultSet.close();
            }
            else{
                System.out.println("Database sudah ada.");
                connection = DriverManager.getConnection(InfoKoneksi.URL, InfoKoneksi.user, InfoKoneksi.password);
            }
            
        }catch(SQLException exception){
            System.out.println("Exception on Model - cons - DB_check");
        }
        
        //Cek tabel data_karyawan
        try{
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, "data_karyawan", null);
            
            if(!resultSet.next()){
                System.out.println("Belum ada tabel 1. Membuat tabel..");
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE data_karyawan( id VARCHAR(8) PRIMARY KEY NOT NULL, "
                                + "nama VARCHAR(100) NOT NULL, password VARCHAR(100) NOT NULL, jk VARCHAR(1), divisi VARCHAR(100) );");
                
                resultSet.close();
            }
            else{
                System.out.println("Tabel 1 sudah ada.");
            }
        }catch(SQLException exception){
            System.out.println(exception.toString());
        }
        
        //Cek tabel hasil_survey
        try{
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, "hasil_survey", null);
            if(!resultSet.next()){
                System.out.println("Belum ada tabel 2. Membuat tabel..");
                Statement statement = connection.createStatement();
                statement.execute("CREATE TABLE hasil_survey( id VARCHAR(100), setuju INT(100), sangat_setuju INT(100), tidak_setuju INT(100),"
                                + "sangat_tidak_setuju INT(100), netral INT(100), FOREIGN KEY (id) REFERENCES"
                                + " data_karyawan(id) );");
                
                resultSet.close();
            }
            else{
                System.out.println("Tabel 2 sudah ada.");
            }

        }catch(SQLException exception){
            System.out.println(exception.toString());
        }
        
    }
    
    public String getValidatedId(){
        return VALIDATED_ID;
    }
    
    public boolean sendSurvey(String id, int s, int ss, int ts, int sts, int n){
        try{
            statement = connection.prepareStatement("SELECT id FROM hasil_survey WHERE id = ?");
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return false;
            }
            statement = connection.prepareStatement("INSERT INTO hasil_survey VALUES(?, ?, ?, ?, ?, ?)");
            statement.setString(1, id);
            statement.setInt(2, s);
            statement.setInt(3, ss);
            statement.setInt(4, ts);
            statement.setInt(5, sts);
            statement.setInt(6, n);
            statement.execute();
        }catch(SQLException exception){
            System.out.println("Exception on Model - getSurvey();");
            System.out.println(exception.toString());
            return false;
        }
        return true;
    }
    
    public boolean sendUpdatedSurvey(String id, int s, int ss, int ts, int sts, int n){
        try{
             
            statement = connection.prepareStatement("UPDATE hasil_survey SET setuju = ?, sangat_setuju = ?, "
                    + "tidak_setuju = ?, sangat_tidak_setuju = ?, netral = ? WHERE id = ?");
            statement.setInt(1, s);
            statement.setInt(2, ss);
            statement.setInt(3, ts);
            statement.setInt(4, sts);
            statement.setInt(5, n);
            statement.setString(6, id);
            statement.execute();
            
        }catch(SQLException exception){
            System.out.println("Exception on Model - getUpdatedSurvey();");
            System.out.println(exception.toString());
            return false;
        }
        return true;
    }
    
    public ResultSet getProfileData(String USERID){
        ResultSet resultSet = null;
        try{ 
            statement = connection.prepareStatement("SELECT * FROM data_karyawan WHERE id = ?");
            statement.setString(1, USERID);
            
            resultSet = statement.executeQuery();
            
        }catch(SQLException exception){
            System.out.println("Exception on Model - getProfileData():");
            System.out.println(exception.toString());
        }
        return resultSet;
    }
    
    public boolean login(String id, String password){
        
        try{
            //PreparedStatement
            statement = connection.prepareStatement("SELECT * FROM data_karyawan WHERE id = ? AND password = ?");
            statement.setString(1, id);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                VALIDATED_ID = id;
                return true;
            }
            
            else return false;
            
        }catch(SQLException exception){
            System.out.println("Exception on Model - login():");
            System.out.println(exception.toString());
            return false;
        }
    }
    
    public boolean register(String id, String password, String nama, String jenis_kelamin, String divisi){

        try{
            statement = connection.prepareStatement("INSERT INTO data_karyawan (id, nama, password, jk, divisi) VALUES(?, ?, ?, ?, ?);"); 
            statement.setString(1, id);
            statement.setString(2, nama);
            statement.setString(3, password);
            statement.setString(4, jenis_kelamin);
            statement.setString(5, divisi);
            
            statement.execute();
            
        }catch(SQLException exception){
            System.out.println("Exception on Model - register(): ");
            System.out.println(exception.toString());
            return false;
        }
        return true;
    }
    
    public boolean checkIdAvailibility(String userid){
        
        try{            
            //PreparedStatement
            statement = connection.prepareStatement("SELECT id FROM data_karyawan WHERE id = ?");
            statement.setString(1, userid);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                return true;
            }
            
            else return false;
            
        }catch(SQLException exception){
            System.out.println("Exception on Model - checkIdAvailibility(): ");
            System.out.println(exception.toString());
            return false;
        }
    }
    
    public ResultSet getUserSurvey(String id){
        ResultSet resultSet = null;
        try{
             
            statement = connection.prepareStatement("SELECT setuju, sangat_setuju, tidak_setuju, sangat_tidak_setuju, netral FROM "
                    + "hasil_survey WHERE id = ?");
            statement.setString(1, id);
            resultSet = statement.executeQuery();
        }catch(SQLException exception){
            System.out.println("Exception on Model - getUserSurvey");
            System.out.println(exception.toString());
        }
        return resultSet;
    }
    
    public ResultSet getAllSurvey(){
        ResultSet resultSet = null;
        try{
             
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT setuju, sangat_setuju, tidak_setuju, sangat_tidak_setuju, netral FROM "
                    + "hasil_survey;");
        }catch(SQLException exception){
            System.out.println("Exception on Model - getAllSurvey");
            System.out.println(exception.toString());
        }
        return resultSet;
    }
    
    public boolean checkIfUserHasFilledTheSurvey(String id){
        boolean result = false;
        try{
             
            statement = connection.prepareStatement("SELECT id FROM hasil_survey WHERE id = ?");
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                result = true;
            }
        }catch(SQLException exception){
            System.out.println("Exception on Model - checkIfUserHasFilledTheSurvey");
            return false;
        }
        return result;
    }
    
    public boolean deleteAccount(String id, String password){
        try{
            statement = connection.prepareStatement("SELECT * FROM data_karyawan WHERE id = ? and password = ?");
            statement.setString(1, id);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                return false;
            }
            
            statement = connection.prepareStatement("DELETE FROM hasil_survey WHERE id = ?");
            statement.setString(1, id);
            statement.execute();
            
            statement = connection.prepareStatement("DELETE FROM data_karyawan WHERE id = ? AND password = ?");
            statement.setString(1, id);
            statement.setString(2, password);
            statement.execute();
             
        }catch(SQLException exception){
            System.out.println("Exception on Model - deleteAccount()");
            System.out.println(exception.toString());
            return false;
        }
        return true;
    }
}