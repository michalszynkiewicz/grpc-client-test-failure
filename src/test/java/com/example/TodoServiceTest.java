package com.example;

import com.example.todos.Todos;
import com.example.todos.TodosOuterClass;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@QuarkusTest
public class TodoServiceTest {

    public static final TodosOuterClass.Void VOID = TodosOuterClass.Void.newBuilder().getDefaultInstanceForType();

    @GrpcClient
    Todos todos;

    @Test
    void shouldWork() {
        List<TodosOuterClass.Todo> updates = new CopyOnWriteArrayList<>();
        todos.watch(VOID)
                .onFailure().invoke(Throwable::printStackTrace)
                .subscribe().with(updates::add);

        todos.add(TodosOuterClass.Todo.newBuilder().setDescription("feed the cat").setTitle("cat").build())
            .onFailure().invoke(Throwable::printStackTrace);

        await().atMost(Duration.ofSeconds(5))
                .until(() -> updates.size() == 1);
    }
}
