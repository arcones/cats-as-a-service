package com.zooplus.cats.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operation {

    private static final PersistenceManager PERSISTENCE_MANAGER = new PersistenceManager();
    private static final String ERROR_TRACE_PREFIX = "SQL operation has failed: ";

    public static Cat upsertCat(String name, String age, String parentId) {
        Cat cat = null;
        try {
            Map<String, String> filter = new HashMap<>();
            filter.put("NAME", name);
            if (retrieveCats(filter).isEmpty()) {
                PERSISTENCE_MANAGER.getStatement().executeUpdate("INSERT INTO CAT VALUES(NULL, \"" + name + "\", " + age + ", " + parentId + ")");
            } else {
                PERSISTENCE_MANAGER.getStatement().executeUpdate("UPDATE CAT SET AGE = " + age + ", PARENT_ID = " + parentId + " WHERE NAME = \"" + name + "\"");
            }
            ResultSet resultSet = PERSISTENCE_MANAGER.getStatement().executeQuery("SELECT * FROM CAT ORDER BY ID DESC LIMIT 1");
            cat = new Cat(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getInt("AGE"), resultSet.getInt("PARENT_ID"));
            System.out.println(cat + " has been upserted");
        } catch (SQLException sqlException) {
            System.err.println(ERROR_TRACE_PREFIX + sqlException.getMessage());
        }
        return cat;
    }

    public static List<Cat> retrieveCats(Map<String, String> queryParams) {
        List<Cat> cats = new ArrayList<>();
        try {
            String where = map2Where(queryParams);
            ResultSet resultSet = PERSISTENCE_MANAGER.getStatement().executeQuery("SELECT * FROM CAT " + where);
            while (resultSet.next()) {
                cats.add(new Cat(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getInt("AGE"), resultSet.getInt("PARENT_ID")));
            }
            System.out.println(cats.size() + " cats have been retrieved");
        } catch (SQLException sqlException) {
            System.err.println(ERROR_TRACE_PREFIX + sqlException.getMessage());
        }
        return cats;
    }

    public static Food retrieveCatFood(int catId) {
        Food food = null;
        try {
            ResultSet resultSet = PERSISTENCE_MANAGER.getStatement().executeQuery("SELECT * FROM FOOD WHERE CAT_ID="+catId);
            food = new Food(resultSet.getInt("ID"), resultSet.getInt("CAT_ID"), resultSet.getString("BRAND"));
            System.out.println(food + " has been retrieved");
        } catch (SQLException sqlException) {
            System.err.println(ERROR_TRACE_PREFIX + sqlException.getMessage());
        }
        return food;
    }

    public static Cat retrieveCatParent(String name) {
        Cat cat = null;
        try {
            ResultSet resultSet = PERSISTENCE_MANAGER.getStatement().executeQuery("SELECT * FROM CAT WHERE ID = (SELECT PARENT_ID FROM CAT WHERE NAME=\"" + name + "\")");
            cat = new Cat(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getInt("AGE"), resultSet.getInt("PARENT_ID"));
            System.out.println(cat + " has been retrieved");
        } catch (SQLException sqlException) {
            System.err.println(ERROR_TRACE_PREFIX + sqlException.getMessage());
        }
        return cat;
    }

    public static void deleteCat(String name) {
        try {
            PERSISTENCE_MANAGER.getStatement().executeUpdate("DELETE FROM CAT WHERE NAME=\"" + name + "\"");
            System.out.println("Cat " + name + " has been deleted");
        } catch (SQLException sqlException) {
            System.err.println(ERROR_TRACE_PREFIX + sqlException.getMessage());
        }
    }

    private static String map2Where(Map<String, String> queryParams) {
        StringBuilder whereBuilder = new StringBuilder();
        String where;
        if (queryParams.size() != 0) {
            whereBuilder = new StringBuilder("WHERE ");
            for (Map.Entry entry : queryParams.entrySet()) {
                whereBuilder.append(entry.getKey()).append(" = \"").append(entry.getValue()).append("\" AND ");
            }
        }
        where = whereBuilder.toString();
        return where.isEmpty() ? where : where.substring(0, where.length() - 4);
    }
}