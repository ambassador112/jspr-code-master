package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class PostController {
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;
    private final Gson gson = new Gson(); // Singleton Gson

    public PostController(PostService service) {
        this.service = service;
    }

    public void all(HttpServletResponse response) throws IOException {
        setJsonResponse(response);
        response.getWriter().print(gson.toJson(service.all()));
    }

    public void getById(long id, HttpServletResponse response) throws IOException {
        setJsonResponse(response);
        final var post = service.getById(id);
        if (post.isPresent()) {
            response.getWriter().print(gson.toJson(post.get()));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        setJsonResponse(response);
        final var post = gson.fromJson(body, Post.class);
        final var savedPost = service.save(post);
        response.getWriter().print(gson.toJson(savedPost));
    }

    public void removeById(long id, HttpServletResponse response) throws IOException {
        service.removeById(id);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void setJsonResponse(HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON);
        response.setCharacterEncoding("UTF-8");
    }
}
