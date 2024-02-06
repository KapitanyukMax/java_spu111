package org.example;

import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String userName = "root";
        String password = "";
        String host = "localhost";
        String port = "3306";
        String database = "java_spu111";
        String strConn = "jdbc:mariadb://" + host + ":" + port + "/" + database;

        menu(strConn, userName, password);
    }

    private static void inputData() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.println("Hello, " + name + "!");
    }

    private static int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    private static void simpleArray() {
        int n = 10;
        int[] arr = new int[10];
        for (int i = 0; i < n; i++)
            arr[i] = getRandom(-5, 30);

        System.out.print("Array: ");
        for (int item : arr)
            System.out.printf("%d\t", item);
        System.out.println();

        int count = 0;
        for (int item : arr)
            if (item >= 0)
                count++;
        System.out.println("Positive count: " + count);

        Arrays.sort(arr);
        System.out.print("Sorted array: ");
        for (int item : arr)
            System.out.printf("%d\t", item);
        System.out.println();
    }

    private static void sortPerson() {
        Person[] list = {
                new Person("Ivan","Melnyk"),
                new Person("Malvina","Morkva"),
                new Person("Petro","Pidkabluchnyk"),
                new Person("Oleg","Gryzun"),
        };
        System.out.println("-----Simple list-----");
        for (var item : list) {
            System.out.println(item);
        }
        Arrays.sort(list);
        System.out.println("-----Ordered list-----");
        for (var item : list) {
            System.out.println(item);
        }
    }

    private static void createCategory(String strConn, String userName, String password) {
        // Встановлюємо з'єднання з базою
        try (Connection conn = DriverManager.getConnection(strConn, userName, password)) {
            System.out.println("Connected successfully");

            // Створюємо запит
            Statement statement = conn.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS categories ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL,"
                    + "description TEXT"
                    + ")";

            // Виконуємо запит
            statement.execute(createTableSQL);
            statement.close();

            System.out.println("Table \"categories\" created");
        }
        catch (Exception ex) {
            System.out.println("Connection error: " + ex.getMessage());
        }
    }

    private static void insertCategory(String strConn, String userName,
                                       String password, CategoryCreate categoryCreate) {
        // // Встановлюємо з'єднання з базою
        try (Connection conn = DriverManager.getConnection(strConn, userName, password)) {
            String insertQuery = "INSERT INTO categories (name, description) VALUES (?, ?)";

            // Створюємо параметризованого запиту
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);

            // Встановлюємо значення параметрів
            preparedStatement.setString(1, categoryCreate.getName());
            preparedStatement.setString(2, categoryCreate.getDescription());

            // Виконуємо запит
            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            System.out.println("Rows affected: " + rowsAffected);
            System.out.println("Category inserted successfully.");
        }
        catch (Exception ex) {
            System.out.println("Connection error: " + ex.getMessage());
        }
    }

    private static List<CategoryItem> listCategories(String strConn, String userName, String password)
    {
        // Встановлюємо з'єднання з базою
        try(Connection conn = DriverManager.getConnection(strConn,userName,password)) {
            // Створюємо запит
            String selectQuery = "SELECT * FROM categories";
            PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);

            // Виконуємо запит
            ResultSet resultSet = preparedStatement.executeQuery();

            List<CategoryItem> list = new ArrayList<>();
            while (resultSet.next()) {
                CategoryItem category = new CategoryItem();
                // Опрацьовуємо результати запиту за полями
                category.setId(resultSet.getInt("id"));
                category.setName(resultSet.getString("name"));
                category.setDescription(resultSet.getString("description"));
                list.add(category);
            }

            // Закриваємо ресурси
            resultSet.close();
            preparedStatement.close();
            return list;
        }
        catch(Exception ex) {
            System.out.println("Помилка читання списку даних: " + ex.getMessage());
            return null;
        }
    }

    private static void updateCategory(String strConn, String userName, String password,
                                       int id, String newName, String newDescription) {
        // Встановлюємо з'єднання з базою
        try(Connection conn = DriverManager.getConnection(strConn,userName,password)) {
            // Створюємо запит
            String updateQuery = "UPDATE categories " +
                                 "SET name = ?, description = ? " +
                                 "WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);

            // Встановлюємо значення параметрів
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newDescription);
            preparedStatement.setInt(3, id);

            // Виконуємо запит
            preparedStatement.executeQuery();
            preparedStatement.close();
        }
        catch(Exception ex) {
            System.out.println("Помилка оновлення даних: " + ex.getMessage());
        }
    }

    private static void deleteCategory(String strConn, String userName, String password, int id) {
        // Встановлюємо з'єднання з базою
        try(Connection conn = DriverManager.getConnection(strConn,userName,password)) {
            // Створюємо запит
            String updateQuery = "DELETE FROM categories WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);

            // Встановлюємо значення параметра
            preparedStatement.setInt(1, id);

            // Виконуємо запит
            preparedStatement.executeQuery();
            preparedStatement.close();
        }
        catch(Exception ex) {
            System.out.println("Помилка видалення категорії: " + ex.getMessage());
        }
    }

    private static void menu(String strConn, String userName, String password) {
        Scanner scanner = new Scanner(System.in);

        int action = 0;
        do {
            System.out.println("1 - Show all categories");
            System.out.println("2 - Add category");
            System.out.println("3 - Update category");
            System.out.println("4 - Delete category");
            System.out.println("5 - Exit");
            System.out.print("->_");
            action = Integer.parseInt(scanner.nextLine());

            switch (action) {
                case 1: {
                    var categories = listCategories(strConn, userName, password);
                    for (var category : categories) {
                        System.out.println(category);
                    }
                    break;
                }
                case 2: {
                    CategoryCreate categoryCreate = new CategoryCreate();
                    System.out.print("Enter category name: ");
                    categoryCreate.setName(scanner.nextLine());
                    System.out.print("Enter category description: ");
                    categoryCreate.setDescription(scanner.nextLine());

                    insertCategory(strConn, userName, password, categoryCreate);
                    break;
                }
                case 3: {
                    System.out.print("Enter category id: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter new category name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new category description: ");
                    String newDescription = scanner.nextLine();
                    updateCategory(strConn, userName, password, id, newName, newDescription);
                    break;
                }
                case 4: {
                    System.out.print("Enter category id: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    deleteCategory(strConn, userName, password, id);
                    break;
                }
            }
        } while (action != 5);
    }
}
