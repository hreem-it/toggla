package io.hreem.toggla.toggle.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.toggle.model.Toggle;
import io.hreem.toggla.common.repository.DBTypeQualifiers;
import io.hreem.toggla.common.repository.DataTypeQualifiers;

@ApplicationScoped
@DBTypeQualifiers.DynamoDB
@DataTypeQualifiers.Toggle
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
