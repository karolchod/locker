package com.locker.lockerservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LockerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Locker> getLockers() {
        System.out.println("Fetching all roles");
        String sql = "SELECT * FROM locker";
        List<Locker> lockers = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Locker.class));
        return lockers;
    }

    public Locker getLocker(int id) {
        System.out.println("Fetching locker id " + id);
        String sql = "SELECT * FROM locker WHERE id = " + id;
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Locker.class));
    }

    public void saveLocker(Locker locker) {
        System.out.println("Saving new locker " + locker.getName() + " to the database");
        SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate);
        insertActor.withTableName("locker").usingColumns("name");
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(locker);
        insertActor.execute(param);
    }

    public void updateLocker(Locker locker) {
        System.out.println("Updating locker " + locker.getName());
        String sql;
        sql = "UPDATE locker SET name = :name WHERE id = :id";

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(locker);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
        template.update(sql, param);
    }

}
