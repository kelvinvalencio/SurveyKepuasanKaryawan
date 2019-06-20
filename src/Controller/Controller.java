/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

/**
 *
 * @author Sevian
 */
import Model.Model;
import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
public class Controller {
    
    private Model model = Model.getModel();
    private String USERID = null;
    private boolean LOGIN_STATE = false;
    
    public boolean login(JTextField id, JPasswordField password){
        
        //Hashing SHA-2
        String hashed_password = Crypto.Hasher.getSha2(password.getText());
        
        if(Model.INSTANCE.login(id.getText(), hashed_password)){
            USERID = getUserId();
            if(USERID != null){
                LOGIN_STATE = true;
            }
            return true;
        }
        return false;
    }
    
    public boolean register(JTextField id, JPasswordField password, JTextField nama, JComboBox jk, JComboBox divisi){
        //Hashing SHA-2
        String hashed_password = Crypto.Hasher.getSha2(password.getText());
        boolean result = Model.INSTANCE.register(id.getText(), hashed_password, nama.getText(), 
                jk.getSelectedItem().toString(), divisi.getSelectedItem().toString());
        return result;
    }
    
    public String getUserId(){
        return Model.INSTANCE.getValidatedId();
    }
    
    public boolean getLoginState(){
        return LOGIN_STATE;
    }
    
    public void setLoginState(boolean LOGIN_STATE){
        this.LOGIN_STATE = LOGIN_STATE;
    }
    
    public String[] getProfileData(){
        String[] arrayOfString = new String[4];
        int i = 0;
        if(this.getLoginState()){
           try{
               ResultSet rs = Model.INSTANCE.getProfileData(this.getUserId()); 
               if(rs.next()){
                   arrayOfString[0] = rs.getString("id");
                   arrayOfString[1] = rs.getString("nama");
                   arrayOfString[2] = rs.getString("jk");
                   arrayOfString[3] = rs.getString("divisi");
               }
           }catch(SQLException exception){
               System.out.println("Exception on LoginController - getProfileData():");
               System.out.println(exception.toString());
           }
           return arrayOfString;
        }
        JOptionPane.showMessageDialog(null, "Kendala autentikasi untuk menerima data profil.");
        throw new RuntimeException("Authentication error.");
    }
    
    public boolean checkIdAvailibility(String USER_ID){
        return Model.INSTANCE.checkIdAvailibility(USER_ID);
    }
    
    public boolean sendSurvey(int s, int ss, int ts, int sts, int n){
        return Model.INSTANCE.sendSurvey(Model.INSTANCE.getValidatedId(), s, ss, ts, sts, n);
    }
    
    public boolean sendUpdatedSurvey(int s, int ss, int ts, int sts, int n){
        return Model.INSTANCE.sendUpdatedSurvey(Model.INSTANCE.getValidatedId(), s, ss, ts, sts, n);
    }
    
    public boolean checkIfTheUserHasFilledTheSurvey(){
        return Model.INSTANCE.checkIfUserHasFilledTheSurvey(USERID);
    }
    
    public boolean fillCurrentUserSurvey(JLabel first, JLabel second, JLabel third, JLabel fourth, JLabel fifth){
        Integer[] currentUserSurveyArray = new Integer[5];
        String[] percentages = new String[5];
        ResultSet resultSet = null;
        resultSet = Model.INSTANCE.getUserSurvey(Model.INSTANCE.getValidatedId());
        
        try{
            if(resultSet == null){
                throw new SQLException("Exception on LoginController - fillCurrentUserSurvey(): resultSet is NULL");
            }
            if(resultSet.next()){
                
                currentUserSurveyArray[0] = resultSet.getInt(1);
                currentUserSurveyArray[1] = resultSet.getInt(2);
                currentUserSurveyArray[2] = resultSet.getInt(3);
                currentUserSurveyArray[3] = resultSet.getInt(4);
                currentUserSurveyArray[4] = resultSet.getInt(5);
                
                for(int i = 0; i < percentages.length; i++){
                    float curr = currentUserSurveyArray[i] / 15f * 100f;
                    String currStr = String.format("%.1f", curr);
                    percentages[i] = "  (" + currStr + " %)";
                }
                
                first.setText(currentUserSurveyArray[0].toString() + percentages[0]);
                second.setText(currentUserSurveyArray[1].toString() + percentages[1]);
                third.setText(currentUserSurveyArray[2].toString() + percentages[2]);
                fourth.setText(currentUserSurveyArray[3].toString() + percentages[3]);
                fifth.setText(currentUserSurveyArray[4].toString() + percentages[4]);
            }
            else{
                return false;
            }
        }catch(SQLException exception){
            System.out.println("Exception on LoginController - fillCurrentUserSurvey()");
            System.out.println(exception.toString());
            return false;
        }
        return true;
    }
    public boolean fillUsersSurvey(JLabel first, JLabel second, JLabel third, JLabel fourth, JLabel fifth){
        Integer[] allUsersSurveyArray = new Integer[5];
        String[] percentages = new String[5];
        float total = 0;
        ResultSet resultSet = null;
        for(int i = 0; i < 5; i++)
            allUsersSurveyArray[i] = 0;
        try{
            resultSet = Model.INSTANCE.getAllSurvey();
            while(resultSet.next()){
                allUsersSurveyArray[0] += resultSet.getInt(1);
                allUsersSurveyArray[1] += resultSet.getInt(2);
                allUsersSurveyArray[2] += resultSet.getInt(3);
                allUsersSurveyArray[3] += resultSet.getInt(4);
                allUsersSurveyArray[4] += resultSet.getInt(5);
                total += resultSet.getInt(1) + resultSet.getInt(2) + resultSet.getInt(3) + resultSet.getInt(4) + resultSet.getInt(5);
                
            }
            for(int i = 0; i < percentages.length; i++){
                String string = String.format("%.1f", (allUsersSurveyArray[i] / total * 100));
                percentages[i] =  " (" + string + " %)";
            }
            first.setText(allUsersSurveyArray[0].toString() + percentages[0]);
            second.setText(allUsersSurveyArray[1].toString() + percentages[1]);
            third.setText(allUsersSurveyArray[2].toString() + percentages[2]);
            fourth.setText(allUsersSurveyArray[3].toString() + percentages[3]);
            fifth.setText(allUsersSurveyArray[4].toString() + percentages[4]);
        }catch(SQLException exception){
            System.out.println("Exception on LoginController - fillAllSurveys()");
            System.out.println(exception.toString());
            return false;
        }
        return true;
    }
    
    public boolean deleteAccount(String password){
        //Hashing SHA-2
        password = Crypto.Hasher.getSha2(password);
        boolean result = Model.INSTANCE.deleteAccount(USERID, password);
        if(result){
            setLoginState(false);
            return true;
        }
        return false;
    }
}
