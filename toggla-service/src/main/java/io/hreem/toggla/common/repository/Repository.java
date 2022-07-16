package io.hreem.toggla.common.repository;

import java.util.List;

public interface Repository<A, B> {
    List<A> getAllKeysMatching(String pattern);

    void create(A id, B data);

    void update(A id, B data) throws ObjectNotFoundException;

    void delete(A id) throws ObjectNotFoundException;

    B get(A id) throws ObjectNotFoundException;

    boolean exists(A id);
}
