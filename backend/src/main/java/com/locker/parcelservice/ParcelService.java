package com.locker.parcelservice;

import com.locker.boxservice.Box;
import com.locker.boxservice.BoxService;
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
public class ParcelService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Parcel> getParcels() {
        System.out.println("Fetching all parcels");
        String sql = "SELECT * FROM parcel";
        List<Parcel> parcels = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parcel.class));
        return parcels;
    }

    public List<Parcel> getActiveParcels(){
        System.out.println("Fetching active parcels");
        String sql = "SELECT * FROM parcel WHERE isfinished = false";
        List<Parcel> parcels = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parcel.class));
        return parcels;
    }

    public List<Parcel> getFinishedParcels(){
        System.out.println("Fetching finished parcels");
        String sql = "SELECT * FROM parcel WHERE isfinished = true";
        List<Parcel> parcels = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parcel.class));
        return parcels;
    }

    public List<Parcel> getParcelsByBoxId(int box_id){
        System.out.println("Fetching parcels in box"+box_id);
        String sql = "SELECT * FROM parcel WHERE box_id ="+box_id;
        List<Parcel> parcels = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parcel.class));
        return parcels;
    }

    public List<Parcel> getParcelsBySender(int sender_id){
        System.out.println("Fetching parcels from user "+sender_id);
        String sql = "SELECT * FROM parcel WHERE user_sender_id = "+sender_id;
        List<Parcel> parcels = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parcel.class));
        return parcels;
    }

    public List<Parcel> getParcelsByRecipient(int recipient_id){
        System.out.println("Fetching parcels to user "+recipient_id);
        String sql = "SELECT * FROM parcel WHERE user_recipient_id = "+recipient_id;
        List<Parcel> parcels = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parcel.class));
        return parcels;
    }

    public Parcel getParcelById(int id){
        System.out.println("Fetching all parcels");
        String sql = "SELECT * FROM parcel WHERE id = "+id;
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Parcel.class));
    }

    public void createParcel(Parcel parcel) {
        System.out.println("Saving new parcel to the database");
        parcel.setCreateddate(new java.sql.Date(System.currentTimeMillis()));
        SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate);
        insertActor.withTableName("parcel").usingColumns("box_id", "user_sender_id", "user_recipient_id", "createddate");
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(parcel);
        insertActor.execute(param);
    }

    public void updateParcel(Parcel parcel) {
        System.out.println("Updating parcel " + parcel.getId());
        String sql;
        sql = "UPDATE parcel SET box_id=:box_id, user_sender_id=:user_sender_id,user_recipient_id=:user_recipient_id, isfinished=:isfinished  WHERE id = :id";
        //do not update date
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(parcel);
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);
        template.update(sql, param);
    }

}
