package io.hreem.toggler.toggle.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import io.hreem.toggler.common.repository.Repository;
import io.hreem.toggler.toggle.model.Toggle;

@Named("dynamodb")
@ApplicationScoped
public class DynamoDBRepository implements Repository<String, Toggle> {

    @Override
    public List<String> getAllKeysMatching(String pattern) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void create(String id, Toggle data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(String id, Toggle data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public Toggle get(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(String id) {
        // TODO Auto-generated method stub
        return false;
    }

}
