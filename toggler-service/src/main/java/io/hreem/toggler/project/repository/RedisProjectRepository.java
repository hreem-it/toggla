package io.hreem.toggler.project.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.hreem.toggler.common.Util;
import io.hreem.toggler.common.repository.ObjectNotFoundException;
import io.hreem.toggler.common.repository.Repository;
import io.hreem.toggler.project.model.Project;
import io.quarkus.redis.client.RedisClient;
import io.hreem.toggler.common.repository.DBTypeQualifiers;
import io.hreem.toggler.common.repository.DataTypeQualifiers;

@ApplicationScoped
@DBTypeQualifiers.Redis
@DataTypeQualifiers.Project
public class RedisProjectRepository implements Repository<String, Project> {

    @Inject
    Util util;

    @Inject
    RedisClient redis;

    @Override
    public List<String> getAllKeysMatching(String pattern) {
        final var response = redis.keys(pattern);
        return response.stream()
                .map(toggleKeyResponse -> toggleKeyResponse.toString())
                .toList();
    }

    @Override
    public void create(String id, Project data) {
        redis.set(List.of(id, util.convert(data)));
    }

    @Override
    public void update(String id, Project data) {
        redis.set(List.of(id, util.convert(data)));
    }

    @Override
    public void delete(String id) throws ObjectNotFoundException {
        if (this.exists(id)) {
            redis.del(List.of(id));
        } else {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }
    }

    @Override
    public Project get(String id) throws ObjectNotFoundException {
        final var result = redis.get(id);
        if (result == null) {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }
        return util.convert(result.toString(), Project.class);
    }

    @Override
    public boolean exists(String id) {
        return redis.exists(List.of(id)).toBoolean();
    }

}
