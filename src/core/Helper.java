package core;

import javax.swing.*;

public class Helper {
    public static void setTheme(){
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
            if(info.getName().equals("Nimbus")){
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static boolean isFieldEmpty(JTextField field){
        return field.getText().trim().isEmpty();
    }

    public static boolean isFieldListEmpty(JTextField[] fields){
        for (JTextField field: fields){
            if (isFieldEmpty(field)) return true;
        }
        return false;
    }

    public static boolean isValidEmail(String mail){
        if(mail == null || mail.trim().isEmpty()) return false;

        if (!mail.contains("@")) return false;

        String[] parts = mail.split("@");
        if (parts.length != 2) return false;

        if (parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) return false;

        if(!parts[1].contains(".")) return false;

        return true;

    }


    public static void optionPaneDialogEN(){
        UIManager.put("OptionPane.okButtonText", "Okey");
        UIManager.put("OptionPane.yesButtonText", "Yess");
        UIManager.put("OptionPane.noButtonText", "Noo");

    }

    public static void showMsg(String message){
        String msg,title;
        optionPaneDialogEN();
        switch (message) {
            case "fill" -> {
                msg = "Please enter required fields!";
                title = "ERROR!";
            }
            case "done" -> {
                msg = "Success";
                title = "Result";
            }
            case "error" -> {
                msg = "An error has occured!";
                title = "ERROR!";
            }
            default -> {
                msg = message;
                title = "Message";
            }
        }

        JOptionPane.showMessageDialog(null,msg,title,JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(String str){
        String msg;

        if (str.equals("sure")){
            msg = "Are you sure?";
        }else {
            msg = str;
        }
        return JOptionPane.showConfirmDialog(null,msg,"Confirm",JOptionPane.YES_NO_OPTION)== 0;

    }
}
