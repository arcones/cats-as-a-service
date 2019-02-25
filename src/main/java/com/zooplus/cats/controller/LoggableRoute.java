package com.zooplus.cats.controller;

import spark.Request;
import spark.Response;
import spark.Route;

class LoggableRoute implements Route {

    private final Route route;

    LoggableRoute(final Route route) {
        this.route = route;
    }

    @Override
    public Object handle(final Request request, final Response response) throws Exception {
        System.out.println("New " + request.requestMethod() + " request for [" + request.pathInfo() + "]");
        Object result = route.handle(request, response);
        System.out.println("End of " + request.requestMethod() + " request for [" + request.pathInfo() + "]");
        return result;
    }
}