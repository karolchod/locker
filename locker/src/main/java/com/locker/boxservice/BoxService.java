package com.locker.boxservice;

import com.locker.lockerservice.Locker;
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
public class BoxService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Box> getBoxes() {
        System.out.println("Fetching all boxes");
        String sql = "SELECT * FROM box";
        List<Box> boxes = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Box.class));
        return boxes;
    }

    public List<Box> getBoxesInLocker(int id) {
        System.out.println("Fetching boxes from locker " + id);
        String sql = "SELECT * FROM box WHERE locker_id = " + id + " ORDER BY id ASC";
        List<Box> boxes = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Box.class));
        return boxes;
    }

    public Locker getLockerByBox(int id) {
        System.out.println("Fetching locker of box " + id);
        String sql = "SELECT * FROM box WHERE id = " + id;
        Box box = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Box.class));
        assert box != null;
        String sql2 = "SELECT * FROM locker WHERE id = " + box.getLocker_id();
        Locker locker = jdbcTemplate.queryForObject(sql2, BeanPropertyRowMapper.newInstance(Locker.class));
        return locker;
    }


    public Box getBox(int id) {
        System.out.println("Fetching box id " + id);
        String sql = "SELECT * FROM box WHERE id = " + id;
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Box.class));
    }

    public void saveBox(Box box) {
        System.out.println("Saving new box to " + box.getLocker_id() + " locker to the database");
        SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate);
        insertActor.withTableName("box").usingColumns("locker_id");
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(box);
        insertActor.execute(param);
    }

    public void updateBox(Box box) {
        System.out.println("Updating box " + box.getId());
        String sql;
        sql = "UPDATE box SET locker_id=:locker_id, isused=:isused, isopen=:isopen  WHERE id = :id";

        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(box);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
        template.update(sql, param);
    }


}
