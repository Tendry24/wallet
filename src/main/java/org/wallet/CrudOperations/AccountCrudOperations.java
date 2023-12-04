package org.wallet.CrudOperations;

import org.wallet.Models.Account;
import org.wallet.connectionDB.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountCrudOperations implements CrudOperations<Account> {
    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        Connection connection = ConnectionDB.getConnection();
        try {
            String sql = "SELECT * FROM \"account\"";
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);

            while(resultSet.next()){
                Account account = mapResultSet(resultSet);

                accounts.add(account);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accounts;
    }

    @Override
    public List<Account> saveAll(List<Account> toSave) {
        List<Account> savedAccounts = new ArrayList<>();

        for (Account account : toSave) {
            Account savedAccount = this.save(account);
            savedAccounts.add(savedAccount);
        }

        return savedAccounts;
    }

    @Override
    public Account save(Account toSave) {
        Account savedAccount;
        Connection connection = ConnectionDB.getConnection();

        String sql = "INSERT INTO \"account\" (balance, currency_id) " +
                "VALUES(?, ?) RETURNING *";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setFloat(1, toSave.getBalance());
            statement.setString(2, toSave.getCurrencyId());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            savedAccount = mapResultSet(resultSet);

            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return savedAccount;
    }

    @Override
    public Account delete(Account toDelete) {
        Account deletedAccount;
        Connection connection = ConnectionDB.getConnection();

        try {
            String sql = "DELETE FROM \"account\" WHERE account_id = ? RETURNING *";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, toDelete.getAccountId());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            deletedAccount = mapResultSet(resultSet);

            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return deletedAccount;
    }

    private Account mapResultSet(ResultSet resultSet) throws SQLException {
        Account account = new Account();
        account.setAccountId(resultSet.getString("account_id"));
        account.setBalance(resultSet.getFloat("balance"));
        account.setCurrencyId(resultSet.getString("currency_id"));

        return account;
    }
}