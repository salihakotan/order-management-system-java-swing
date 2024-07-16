package dao;

import core.Database;
import entity.Cart;
import entity.Product;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CartDao {
    private Connection connection;
    private ProductDao productDao;
    private CustomerDao customerDao;

    public CartDao(){

        connection = Database.getInstance();
        this.productDao = new ProductDao();
        this.customerDao = new CustomerDao();
    }


    public ArrayList<Cart> findAll (){
        ArrayList<Cart> carts = new ArrayList<>();

        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery("SELECT * FROM cart");
            while (resultSet.next()){
                carts.add(this.match(resultSet));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return carts;
    }

    public boolean save(Cart cart){
        String query = "INSERT INTO cart (customer_id,product_id,price,date,note) VALUES (?,?,?,?,?)";

        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1,cart.getCustomerId());
            pr.setInt(2,cart.getProductId());
            pr.setInt(3,cart.getPrice());
            pr.setDate(4, Date.valueOf(cart.getDate()));
            pr.setString(5,cart.getNote());


            return pr.executeUpdate() != -1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public Cart match(ResultSet resultSet) throws SQLException {
        Cart cart = new Cart();

        cart.setId(resultSet.getInt("id"));
        cart.setProductId(resultSet.getInt("product_id"));
        cart.setCustomerId(resultSet.getInt("customer_id"));
        cart.setNote(resultSet.getString("note"));
        cart.setPrice(resultSet.getInt("price"));
        cart.setDate(LocalDate.parse(resultSet.getString("date")));
        cart.setCustomer(customerDao.getById(cart.getCustomerId()));
        cart.setProduct(productDao.getById(cart.getProductId()));


        return cart;

    }

}
