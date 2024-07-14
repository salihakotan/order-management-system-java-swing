package dao;

import core.Database;
import entity.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDao {

    private Connection connection;

    public CustomerDao(){
        this.connection = Database.getInstance();
    }

    public ArrayList<Customer> findAll (){
        ArrayList<Customer> customers = new ArrayList<>();

        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery("SELECT * FROM customer");
            while (resultSet.next()){
                customers.add(this.match(resultSet));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return customers;
    }

    public boolean save(Customer customer){
        String query = "INSERT INTO customer (name,type,phone,mail,address) VALUES (?,?,?,?,?)";

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1,customer.getName());
            preparedStatement.setString(2,customer.getType().toString());
            preparedStatement.setString(3,customer.getPhone());
            preparedStatement.setString(4,customer.getMail());
            preparedStatement.setString(5,customer.getAddress());

            return preparedStatement.executeUpdate() != -1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public Customer match(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();

        customer.setId(resultSet.getInt("id"));
        customer.setName(resultSet.getString("name"));
        customer.setMail(resultSet.getString("mail"));
        customer.setPhone(resultSet.getString("phone"));
        customer.setAddress(resultSet.getString("address"));
        customer.setType(Customer.TYPE.valueOf(resultSet.getString("type")));


        return customer;

    }

}
