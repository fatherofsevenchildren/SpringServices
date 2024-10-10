package edu.school21.spring.service.repositories;

import edu.school21.spring.service.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

public class UsersRepositoryJdbcImpl implements UsersRepository {

    private final DataSource dataSource;

    public UsersRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = null;
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM users WHERE email=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(resultSet.getLong("id"), email);
            }
        } catch (SQLException e) {
            out.println(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        User user = null;
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM users WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(id, resultSet.getString("email"));
            }
        } catch (SQLException e) {
            out.println(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long userId = resultSet.getLong("id");
                String userEmail = resultSet.getString("email");
                users.add(new User(userId, userEmail));
            }
        } catch (SQLException e) {
            out.println(e);
        }
        return users;
    }

    @Override
    public void save(User entity) {
        try (Connection connection = dataSource.getConnection()) {
            String query = "INSERT INTO users (email) VALUES (?) RETURNING id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, entity.getEmail());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                entity.setId(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            out.println(e);
        }
    }

    @Override
    public void update(User entity) {
        try (Connection connection = dataSource.getConnection()) {
            String query = "UPDATE users SET email=? WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, entity.getEmail());
            preparedStatement.setLong(2, entity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            out.println(e);
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            String query = "DELETE FROM users WHERE id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            out.println(e);
        }
    }
}