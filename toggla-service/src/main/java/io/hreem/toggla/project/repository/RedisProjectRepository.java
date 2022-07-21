package io.hreem.toggla.project.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.hreem.toggla.common.Util;
import io.hreem.toggla.common.repository.ObjectNotFoundException;
import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.project.model.Project;
import io.quarkus.logging.Log;
import io.quarkus.redis.client.RedisClient;
import io.hreem.toggla.common.repository.DBTypeQualifiers;
import io.hreem.toggla.common.repository.DataTypeQualifiers;

@ApplicationScoped
@DBTypeQualifiers.Redis
@DataTypeQualifiers.Project
public class RedisProjectRepository implements Repository<String, Project> {

    private static final String REDIS_ID_PREFIX = "project:";

    @Inject
    Util util;

    @Inject
    RedisClient redis;

    @Override
    public List<String> getAllKeysMatching(String pattern) {
        final var response = redis.keys(REDIS_ID_PREFIX + pattern);
        return response.stream()
                .map(toggleKeyResponse -> toggleKeyResponse.toString())
                .map(key -> key.split(REDIS_ID_PREFIX)[1])
                .toList();
    }

    @Override
    public void create(String id, Project data) {
        redis.set(List.of(REDIS_ID_PREFIX + id, util.convert(data)));
    }

    @Override
    public void update(String id, Project data) {
        redis.set(List.of(REDIS_ID_PREFIX + id, util.convert(data)));
    }

    @Override
    public void delete(String id) throws ObjectNotFoundException {
        if (this.exists(id)) {
            redis.del(List.of(REDIS_ID_PREFIX + id));
        } else {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }
    }

    @Override
    public Project get(String id) throws ObjectNotFoundException {
        final var result = redis.get(REDIS_ID_PREFIX + id);
        if (result == null) {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }
        return util.convert(result.toString(), Project.class);
    }

    @Override
    public boolean exists(String id) {
        return redis.exists(List.of(REDIS_ID_PREFIX + id)).toBoolean();
    }

}
