package com.locker.parcelservice;

import com.locker.boxservice.Box;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.List;

@Data //getters and setters
@NoArgsConstructor
@AllArgsConstructor
public class Parcel {
    private int id;
    private int box_id;
    private int user_sender_id;
    private int user_recipient_id;
    private java.sql.Date createddate;
    private boolean isfinished;

}
