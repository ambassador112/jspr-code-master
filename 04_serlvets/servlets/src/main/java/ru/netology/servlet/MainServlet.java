package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private static final String API_POSTS = "/api/posts";
    private static final String API_POSTS_ID_REGEX = "/api/posts/\\d+";
    private PostController controller;

    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        final var path = req.getRequestURI();
        final var method = req.getMethod();
        try {
            if (method.equals("GET") && path.equals(API_POSTS)) {
                controller.all(resp);
            } else if (method.equals("GET") && path.matches(API_POSTS_ID_REGEX)) {
                handleIdRequest(path, resp, controller::getById);
            } else if (method.equals("POST") && path.equals(API_POSTS)) {
                controller.save(req.getReader(), resp);
            } else if (method.equals("DELETE") && path.matches(API_POSTS_ID_REGEX)) {
                handleIdRequest(path, resp, controller::removeById);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleIdRequest(String path, HttpServletResponse resp, RequestHandler handler) throws IOException {
        final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
        handler.handle(id, resp);
    }

    @FunctionalInterface
    interface RequestHandler {
        void handle(long id, HttpServletResponse resp) throws IOException, IOException;
    }
}
