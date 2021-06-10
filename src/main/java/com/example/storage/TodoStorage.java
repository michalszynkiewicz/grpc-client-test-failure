package com.example.storage;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.function.Consumer;

@ApplicationScoped
public class TodoStorage {

    @Inject
    Event<Todo> todoEmitter;

    public Uni<Void> add(Todo entity) {
        return Panache.withTransaction(() -> Todo.persist(entity))
                .onItem().invoke(() -> todoEmitter.fireAsync(entity));
    }

    public Multi<Todo> listAll() {
        return Todo.streamAll();
    }

    public Uni<Todo> modify(long id, Consumer<Todo> modification) {
        return Panache.withTransaction(() -> Todo.<Todo>findById(id)
                .onItem().invoke(modification)
                .onItem().invoke(todoEmitter::fireAsync)
        );
    }
}
