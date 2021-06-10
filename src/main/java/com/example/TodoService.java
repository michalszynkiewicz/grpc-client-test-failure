package com.example;

import com.example.storage.Todo;
import com.example.storage.TodoStorage;
import com.example.todos.Todos;
import com.example.todos.TodosOuterClass;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.operators.multi.processors.BroadcastProcessor;

import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

@GrpcService
public class TodoService implements Todos {

    public static final TodosOuterClass.Void VOID = TodosOuterClass.Void.getDefaultInstance();

    @Inject
    TodoStorage storage;

    @Inject
    Mapper mapper;

    private final BroadcastProcessor<TodosOuterClass.Todo> broadcast = BroadcastProcessor.create();

    @Override
    public Uni<TodosOuterClass.Void> add(TodosOuterClass.Todo request) {
        return storage.add(mapper.grpcToEntity(request))
                .replaceWith(VOID);
    }

    @Override
    public Uni<TodosOuterClass.Void> markDone(TodosOuterClass.Todo request) {
        return storage.modify(request.getId(), todo -> todo.todoState = TodoState.DONE)
                .replaceWith(VOID);
    }

    @Override
    public Multi<TodosOuterClass.Todo> watch(TodosOuterClass.Void request) {
        return Multi.createBy().merging()
                .streams(storage.listAll().map(mapper::entityToGrpc),
                        broadcast);
    }

    void collectTodos(@ObservesAsync Todo todo) {
        broadcast.onNext(mapper.entityToGrpc(todo));
    }
}
