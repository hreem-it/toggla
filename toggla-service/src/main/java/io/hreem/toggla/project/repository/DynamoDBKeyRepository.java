package io.hreem.toggla.project.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.hreem.toggla.common.repository.Repository;
import io.hreem.toggla.common.repository.DBTypeQualifiers;
import io.hreem.toggla.common.repository.DataTypeQualifiers;

@ApplicationScoped
@DBTypeQualifiers.DynamoDB
@DataTypeQualifiers.APIKey
public class DynamoDBKeyRepository implements Repository<String, String> {

    @Override
    public List<String> getAllKeysMatching(String pattern) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void create(String id, String data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(String id, String data) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public String get(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(String id) {
        // TODO Auto-generated method stub
        return false;
    }

}
