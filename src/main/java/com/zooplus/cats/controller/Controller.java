package com.zooplus.cats.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zooplus.cats.model.Food;
import spark.Request;
import spark.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zooplus.cats.model.Operation.deleteCat;
import static com.zooplus.cats.model.Operation.retrieveCatFood;
import static com.zooplus.cats.model.Operation.retrieveCatParent;
import static com.zooplus.cats.model.Operation.retrieveCats;
import static com.zooplus.cats.model.Operation.upsertCat;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Controller {

    private static final String MAIN_ROUTE = "/v1";

    private static final String GET_CATS_ROUTE = "/v1/cats";
    private static final String GET_CAT_ROUTE = "/v1/cats/:name";
    private static final String GET_CAT_PARENT_ROUTE = "/v1/cats/:name/parent";
    private static final String GET_CAT_PARENT_FOOD_ROUTE = "/v1/cats/:name/parent/food";
    private static final String GET_CATS_MASTER = "/v1/cats/master";
    private static final String GET_CAT_PICTURE = "/v1/cats/Felix/portrait";
    private static final String GET_AUTH = "/auth";

    private static final String PUT_CAT_ROUTE = "/v1/cats";

    private static final String POST_CAT_ROUTE = "/v1/cats";

    private static final String DELETE_CAT_ROUTE = "/v1/cats/:name";

    public Controller(Service http) {

        http.get(GET_AUTH, new LoggableRoute((request, response) -> {
            String responseBody = "";
            if(isAuthorized(request.queryString())){
                response.status(200);
                responseBody = new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("auth.json").toURI())));
            } else {
                response.status(403);
            }
            return responseBody;
        }));

        http.get(MAIN_ROUTE, new LoggableRoute(((request, response) -> {
            response.type("text/html");
            response.status(200);
            return "<h1>Welcome to the CATS API!</h1>";
        })));

        http.get(GET_CATS_MASTER, new LoggableRoute((request, response) -> {
            response.status(200);
            String responseBody = readFromResources();
            return responseBody;
        }));

        http.get(GET_CAT_ROUTE, new LoggableRoute((request, response) -> {
            String responseBody = "";
            response.type("application/json");
            if (hasDogCookie(request)) {
                response.cookie("Food", "RoyalFeline");
                response.status(406);
            } else {
                response.cookie("Forgiven", "DoNotDoItAgain");
                Map<String, String> filter = new HashMap<>();
                filter.put("name", request.params(":name"));
                CatApiModel catApiModel = retrieveCats(filter).stream().map(CatApiModel::new).findFirst().orElse(null);
                if (catApiModel == null) {
                    response.status(404);
                } else {
                    response.status(200);
                    responseBody = catApiModel.toString();
                }
            }
            return responseBody;
        }));

        http.get(GET_CATS_ROUTE, new LoggableRoute((request, response) -> {
            String responseBody = "";
            response.type("application/json");
            List<CatApiModel> catApiModels = retrieveCats(queryParams2Map(request.queryString())).stream().map(CatApiModel::new).collect(toList());
            if (catApiModels.size() == 0) {
                response.status(404);
            } else {
                response.status(200);
                responseBody = catApiModels.toString();
            }
            return responseBody;
        }));

        http.get(GET_CAT_PARENT_ROUTE, new LoggableRoute((request, response) -> {
            response.type("application/json");
            CatApiModel catApiModel = new CatApiModel(retrieveCatParent(request.params(":name")));
            response.status(200);
            return catApiModel.toString();
        }));

        http.get(GET_CAT_PARENT_FOOD_ROUTE, new LoggableRoute((request, response) -> {
            response.type("application/json");
            String responseBody = "";
            CatApiModel catApiModel = new CatApiModel(retrieveCatParent(request.params(":name")));
            Food food = retrieveCatFood(catApiModel.id);
            if (food == null) {
                response.status(404);
            } else {
                response.status(200);
                responseBody = new FoodApiModel(food).toString();
            }
            return responseBody;
        }));

        http.put(PUT_CAT_ROUTE, new LoggableRoute(((request, response) -> {
            String responseBody = "";
            response.type("application/json");
            if (request.headers("Soy-El-Doctor") != null && request.headers("Soy-El-Doctor").equals("Grijando")) {
                response.status(403);
            } else {
                JsonObject jsonObject = requestBody2JsonObject(request);
                String name = jsonObject.get("name").getAsString();
                String age = jsonObject.get("age").getAsString();
                String parentId = jsonObject.get("parentId") != null ? jsonObject.get("parentId").getAsString() : "0";
                responseBody = new CatApiModel(upsertCat(name, age, parentId)).toString();
                response.status(201);
            }
            return responseBody;
        })));

        http.post(POST_CAT_ROUTE, new LoggableRoute(((request, response) -> {
            response.type("application/json");

            JsonObject jsonObject = requestBody2JsonObject(request);
            String name = jsonObject.get("name").getAsString();
            String age = jsonObject.get("age").getAsString();
            String parentId = jsonObject.get("parentId") != null ? jsonObject.get("parentId").getAsString() : "0";

            CatApiModel catApiModel = new CatApiModel(upsertCat(name, age, parentId));
            response.status(200);
            return catApiModel.toString();
        })));

        http.delete(DELETE_CAT_ROUTE, new LoggableRoute((request, response) -> {
            deleteCat(request.params(":name"));
            response.header("Cat-Reply", "hasta luego Mari Carmen");
            response.header("Cat-Reply", "no me puedes importar menos");
            response.status(204);
            return "";
        }));

        http.get(GET_CAT_PICTURE, new LoggableRoute((request, response) -> {
            response.status(200);
            ClassLoader classLoader = getClass().getClassLoader();
            String felixPicturePath = classLoader.getResource("Felix.jpeg").getPath();
            byte[] bytes = Files.readAllBytes(Paths.get(felixPicturePath));
            HttpServletResponse raw = response.raw();

            raw.getOutputStream().write(bytes);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();

            return response.raw();
        }));
    }

    private boolean isAuthorized(String formFields) {
        return formFields.contains("username=arcones") && formFields.contains("password=CambiameDeUnaVez123");
    }

    private String readFromResources() throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("master.json").toURI())));
    }

    private boolean hasDogCookie(Request request) {
        return request.cookie("Food") != null && request.cookie("Food").contains("RoyalCanin");
    }

    private Map<String, String> queryParams2Map(String queryString) {
        List<String> pairs = new ArrayList<>();
        if (queryString == null || queryString.isEmpty()) {
            pairs = Collections.emptyList();
        } else if (queryString.contains("&")) {
            pairs = asList(queryString.split("&"));
        } else {
            pairs.add(queryString);
        }
        return pairs.stream().collect(toMap(pair -> pair.replaceFirst("=.*", ""), pair -> pair.replaceFirst(".*=", "")));
    }

    private JsonObject requestBody2JsonObject(Request request) {
        Gson gson = new Gson();
        return gson.fromJson(request.body(), JsonObject.class);
    }
}