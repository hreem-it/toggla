package io.hreem.toggler.toggle.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import io.hreem.toggler.common.Util;
import io.hreem.toggler.common.repository.ObjectNotFoundException;
import io.hreem.toggler.common.repository.Repository;
import io.hreem.toggler.toggle.model.Toggle;
import io.quarkus.redis.client.RedisClient;

@Named("redis")
@ApplicationScoped
public class RedisRepository implements Repository<String, Toggle> {

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
    public void create(String id, Toggle data) {
        redis.set(List.of(id, util.convert(data)));
    }

    @Override
    public void update(String id, Toggle data) {
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
    public Toggle get(String id) throws ObjectNotFoundException {
        final var result = redis.get(id);
        if (result == null) {
            throw new ObjectNotFoundException("Object with id " + id + " does not exist");
        }
        return util.convert(result.toString(), Toggle.class);
    }

    @Override
    public boolean exists(String id) {
        return redis.exists(List.of(id)).toBoolean();
    }

}
