package com.netcracker.testerritto.mappers;

import com.netcracker.testerritto.models.Group;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupRowMapper implements RowMapper<Group> {

    @Override
    public Group mapRow(ResultSet resultSet, int i) throws SQLException {
        Group group = new Group();
        group.setGroup_id(resultSet.getInt("id"));
        group.setName(resultSet.getString("name"));
        group.setLink(resultSet.getString("link"));
        group.setCreatorUserId(resultSet.getInt("creatorId"));
        return group;
    }
}