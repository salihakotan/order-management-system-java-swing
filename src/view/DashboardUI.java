package view;

import business.BasketController;
import business.CartController;
import business.CustomerController;
import business.ProductController;
import core.Helper;
import core.Item;
import entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class DashboardUI extends JFrame{
    private JPanel container;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JTabbedPane tab_menu;
    private JPanel pnl_customer;
    private JScrollPane scrl_customer;
    private JTable tbl_customer;
    private JPanel pnl_customer_filter;
    private JTextField fld_f_customer_name;
    private JComboBox<Customer.TYPE> cmb_customer_type;
    private JButton btn_customer_new;
    private JButton btn_customer_filter;
    private JButton btn_customer_filter_reset;
    private JLabel lbl_f_customer_name;
    private JLabel lbl_f_customer_type;
    private JPanel pnl_product;
    private JScrollPane scrl_product;
    private JTable tbl_product;
    private JPanel pnl_product_filter;
    private JTextField fld_f_product_name;
    private JTextField fld_f_product_code;
    private JComboBox<Item> cmb_f_product_stock;
    private JButton btn_f_product;
    private JButton btn_f_clear_product;
    private JButton btn_product_new;
    private JLabel lbl_f_product_name;
    private JLabel lbl_f_product_code;
    private JLabel lbl_f_product_stock;
    private JPanel pnl_basket;
    private JPanel pnl_basket_top;
    private JScrollPane scrl_basket;
    private JComboBox<Item> cmb_basket_customer;
    private JButton btn_basket_reset;
    private JButton btn_basket_order;
    private JLabel lbl_basket_price;
    private JLabel lbl_basket_count;
    private JTable tbl_basket;
    private JPanel pnl_orders;
    private JScrollPane scrl_cart;
    private JTable tbl_cart;
    private User user;
    private CustomerController customerController;
    private ProductController productController;
    private BasketController basketController;
    private CartController cartController;
    private DefaultTableModel tmdl_customer = new DefaultTableModel() ;
    private  DefaultTableModel tmdl_product = new DefaultTableModel();
    private  DefaultTableModel tmdl_basket = new DefaultTableModel();
    private DefaultTableModel tmdl_cart = new DefaultTableModel();
    private JPopupMenu popup_customer = new JPopupMenu();
    private JPopupMenu popup_product = new JPopupMenu();
    private JPopupMenu popup_basket = new JPopupMenu();

    public DashboardUI(User user){
        this.user = user;
        this.customerController = new CustomerController();
        this.productController = new ProductController();
        this.basketController = new BasketController();
        this.cartController = new CartController();


        if (user ==null){
            Helper.showMsg("Error");
            dispose();
        }

        this.add(container);
        this.setTitle("Customer Management System");
        this.setSize(1000,500);

        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;

        this.setLocation(x,y);
        this.setVisible(true);

        this.lbl_welcome.setText("Welcome " + this.user.getName());



        //CUSTOMER TAB
        loadCustomerButtonEvent();
        loadCustomerTable(null);
        loadCustomerPopupMenu();
        this.cmb_customer_type.setModel(new DefaultComboBoxModel<>(Customer.TYPE.values()));
        this.cmb_customer_type.setSelectedItem(null);


        //PRODUCT TAB

        loadProductTable(null);
        loadProductPopupMenu();
        loadProductButtonEvent();
        this.cmb_f_product_stock.addItem(new Item(1,"Exist"));
        this.cmb_f_product_stock.addItem(new Item(2,"Does not exist"));
        this.cmb_f_product_stock.setSelectedItem(null);

        //BASKET TAB
        loadBasketTable();
        loadBasketButtonEvent();
        loadBasketPopupMenu();
        loadBasketCustomerCombo();


        //CART TAB
        loadCartTable();

    }

    private void loadCartTable(){
        Object[] columnCart = {"ID", "Customer name", "Product name", "Price","Date","Note"};

        ArrayList<Cart> carts = this.cartController.findAll();

        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_cart.getModel();
        clearModel.setRowCount(0);

        this.tmdl_cart.setColumnIdentifiers(columnCart);


        for (Cart cart: carts){
            Object[] rowObject = {
                    cart.getId()
                    ,cart.getCustomer().getName(),cart.getProduct().getName(),cart.getPrice(),
                    cart.getDate(),cart.getNote()};
            this.tmdl_cart.addRow(rowObject);
        }


        this.tbl_cart.setModel(tmdl_cart);
        this.tbl_cart.getTableHeader().setReorderingAllowed(false);
        this.tbl_cart.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_cart.setEnabled(false);


    }

    private void loadBasketCustomerCombo(){
        ArrayList<Customer> customers = this.customerController.findAll();
        this.cmb_basket_customer.removeAllItems();
        for (Customer customer: customers){
            int comboKey = customer.getId();
            String comboValue = customer.getName();
            this.cmb_basket_customer.addItem(new Item(comboKey,comboValue));
        }
        this.cmb_basket_customer.setSelectedItem(null);

    }

    private void loadBasketPopupMenu(){
        this.tbl_basket.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_basket.rowAtPoint(e.getPoint());
                tbl_basket.setRowSelectionInterval(selectedRow,selectedRow);
            }
        });

        this.popup_basket.add("Update");

        this.tbl_basket.setComponentPopupMenu(this.popup_basket);

    }

    private void loadBasketButtonEvent(){
        this.btn_basket_reset.addActionListener(e->{
            if (Helper.confirm("sure")){
                if(this.basketController.clear()){
                    Helper.showMsg("done");
                    loadBasketTable();
                }else{
                    Helper.showMsg("error");
                }
            }
        });
        this.btn_basket_order.addActionListener(e->{
            Item selectedCustomer = (Item) this.cmb_basket_customer.getSelectedItem();

            if (selectedCustomer == null){
                Helper.showMsg("Please select a customer!");
            }else {
                Customer customer = this.customerController.getById(selectedCustomer.getKey());
                ArrayList<Basket> baskets = this.basketController.findAll();
                if (customer.getId() ==0){
                    Helper.showMsg("Customer does not exist!");
                }else if (baskets.size() == 0){
                    Helper.showMsg("Please add product to basket!");
                }else {
                    CartUI cartUI = new CartUI(customer);
                    cartUI.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            loadBasketTable();
                            loadProductTable(null);
                            loadCartTable();
                        }
                    });
                }
            }

        });
    }

    private void loadBasketTable(){
        Object[] columnBasket = {"ID", "Product name", "Code", "Price","Stock"};

        ArrayList<Basket> baskets = this.basketController.findAll();

        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_basket.getModel();
        clearModel.setRowCount(0);

        this.tmdl_basket.setColumnIdentifiers(columnBasket);

        int totalPrice=0;
        for (Basket basket: baskets){
            Object[] rowObject = {
                    basket.getId()
                    ,basket.getProduct().getName(),basket.getProduct().getCode(),basket.getProduct().getPrice(),
                    basket.getProduct().getStock()};
            this.tmdl_basket.addRow(rowObject);
            totalPrice += basket.getProduct().getPrice();
        }
        this.lbl_basket_price.setText(totalPrice + " $");
        this.lbl_basket_count.setText(String.valueOf(baskets.size()));

        this.tbl_basket.setModel(tmdl_basket);
        this.tbl_basket.getTableHeader().setReorderingAllowed(false);
        this.tbl_basket.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_basket.setEnabled(false);


    }

    private void loadProductButtonEvent(){
        btn_product_new.addActionListener(e->{
            ProductUI productUI = new ProductUI(new Product());
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);
                }
            });

        });

        this.btn_f_product.addActionListener(e->{
            ArrayList<Product> filteredProducts = this.productController.filter(this.fld_f_product_name.getText(),
                    this.fld_f_product_code.getText(), (Item) this.cmb_f_product_stock.getSelectedItem());

            loadProductTable(filteredProducts);
        });

        this.btn_f_clear_product.addActionListener(e->{
            this.fld_f_product_code.setText(null);
            fld_f_product_name.setText(null);
            cmb_f_product_stock.setSelectedItem(null);
            loadProductTable(null);
        });
    }

    private void loadProductTable(ArrayList<Product> products){
        Object[] columnProduct = {"ID", "Product name", "Code", "Price","Stock"};

        if(products ==null){
            products = this.productController.findAll();
        }

        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_product.getModel();
        clearModel.setRowCount(0);

        this.tmdl_product.setColumnIdentifiers(columnProduct);

        for (Product product: products){
            Object[] rowObject = {product.getId(),product.getName(),product.getCode(),product.getPrice(),product.getStock()};
            this.tmdl_product.addRow(rowObject);
        }

        this.tbl_product.setModel(tmdl_product);
        this.tbl_product.getTableHeader().setReorderingAllowed(false);
        this.tbl_product.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_product.setEnabled(false);
    }

    private void loadProductPopupMenu(){
        this.tbl_product.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_product.rowAtPoint(e.getPoint());
                tbl_product.setRowSelectionInterval(selectedRow,selectedRow);
            }
        });

        this.popup_product.add("Update").addActionListener(e->{
            int selectId = Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(),0).toString());
            ProductUI productUI = new ProductUI(this.productController.getById(selectId) );
            productUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadProductTable(null);
                    loadBasketTable();
                }
            });
        });
        this.popup_product.add("Delete").addActionListener(e->{
            int selectId = Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(),0).toString());
            if (Helper.confirm("sure")){
                if(this.productController.delete(selectId)){
                    Helper.showMsg("done");
                    loadProductTable(null);
                    loadBasketTable();
                }else {
                    Helper.showMsg("error");
                }
            }
        });

        this.popup_product.add("Add to Basket").addActionListener(e->{
            int selectId = Integer.parseInt(tbl_product.getValueAt(tbl_product.getSelectedRow(),0).toString());
            Product basketProduct = this.productController.getById(selectId);
            if (basketProduct.getStock() <= 0){
                Helper.showMsg("This product stock does not exist!");
            }else {
                Basket basket = new Basket(basketProduct.getId());

                if(this.basketController.save(basket)){
                    Helper.showMsg("done");
                    loadBasketTable();
                }else{
                    Helper.showMsg("error");
                }
            }
        });

        this.tbl_product.setComponentPopupMenu(this.popup_product);

    }

    private void loadCustomerButtonEvent(){
        this.btn_logout.addActionListener(e->{
            dispose();
            LoginUI loginUI = new LoginUI();
        });

        this.btn_customer_new.addActionListener(e->{
            CustomerUI customerUI = new CustomerUI(new Customer());
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadBasketCustomerCombo();
                }
            });
        });

        this.btn_customer_filter.addActionListener(e->{
            ArrayList<Customer> filteredCustomers = this.customerController.filter(this.fld_f_customer_name.getText(), (Customer.TYPE) this.cmb_customer_type.getSelectedItem());

            loadCustomerTable(filteredCustomers);
        });

        this.btn_customer_filter_reset.addActionListener(e->{
            loadCustomerTable(null);
            this.fld_f_customer_name.setText(null);
            this.cmb_customer_type.setSelectedItem(null);
        });

    }

    private void loadCustomerPopupMenu(){

        this.tbl_customer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_customer.rowAtPoint(e.getPoint());
                tbl_customer.setRowSelectionInterval(selectedRow,selectedRow);
            }
        });

        this.popup_customer.add("Update").addActionListener(e -> {
            int selectId = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(),0).toString());
            CustomerUI customerUI = new CustomerUI(this.customerController.getById(selectId) );
            customerUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCustomerTable(null);
                    loadBasketCustomerCombo();
                }
            });
        });
        this.popup_customer.add("Delete").addActionListener(e->{
            int selectId = Integer.parseInt(tbl_customer.getValueAt(tbl_customer.getSelectedRow(),0).toString());
          if (Helper.confirm("sure")){
              if(this.customerController.delete(selectId)){
                  Helper.showMsg("done");
                  loadCustomerTable(null);
                  loadBasketCustomerCombo();
              }else {
                  Helper.showMsg("error");
              }
          }
        });

        this.tbl_customer.setComponentPopupMenu(this.popup_customer);
    }


    private void loadCustomerTable(ArrayList<Customer> customers){
        Object[] columnCustomer = {"ID", "Customer name", "Type", "Phone","Email","Address"};

        if(customers ==null){
            customers = this.customerController.findAll();
        }

        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_customer.getModel();
        clearModel.setRowCount(0);

        this.tmdl_customer.setColumnIdentifiers(columnCustomer);

        for (Customer customer: customers){
            Object[] rowObject = {customer.getId(),customer.getName(),customer.getType(),customer.getPhone(),customer.getMail(),customer.getAddress()};
            this.tmdl_customer.addRow(rowObject);
        }

        this.tbl_customer.setModel(tmdl_customer);
        this.tbl_customer.getTableHeader().setReorderingAllowed(false);
        this.tbl_customer.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_customer.setEnabled(false);
    }

}
