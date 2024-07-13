package view;

import core.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame {
    private JPanel container;
    private JPanel pnl_top;
    private JLabel lbl_title;
    private JPanel pnl_bottom;
    private JTextField fld_mail;
    private JButton btn_login;
    private JLabel lbl_mail;
    private JLabel lbl_password;
    private JPasswordField fld_password;

    public LoginUI(){
        this.add(container);
        this.setTitle("Customer management system");
        this.setSize(400,400);
        this.setVisible(true);

        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;

        this.setLocation(x,y);


        btn_login.addActionListener(e -> {
            JTextField[] checkList = {fld_mail,fld_password};

            if (!Helper.isValidEmail(fld_mail.getText())){
                Helper.showMsg("Please enter a valid email!");
            }else if (Helper.isFieldListEmpty(checkList)){
                Helper.showMsg("fill");
            }else {

            }
        });
    }

}
