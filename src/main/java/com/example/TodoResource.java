package com.example;

import com.example.storage.Todo;
import com.example.storage.TodoStorage;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @Inject
    Mapper mapper;

    @Inject
    TodoStorage storage;

    @POST
    public Uni<TodoDto> add(TodoDto todo) {
        Todo entity = mapper.dtoToEntity(todo);
        entity.todoState = TodoState.NEW;

        return storage.add(entity)
                .replaceWith(entity)
                .map(mapper::entityToDto);
    }

    @GET
    public Multi<TodoDto> getAll() {
        return storage.listAll()
                .map(mapper::entityToDto);
    }

    @PUT
    @Path("/{id}")
    public Uni<TodoDto> modify(@RestPath Long id, TodoDto todoDto) {
        return storage.modify(id, todo -> mapper.merge(todoDto, todo))
                .map(mapper::entityToDto);
    }


}
