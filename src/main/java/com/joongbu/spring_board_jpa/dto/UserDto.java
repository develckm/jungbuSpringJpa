package com.joongbu.spring_board_jpa.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
/*
+---------+--------------+------+-----+-------------------+-------------------+
| Field   | Type         | Null | Key | Default           | Extra             |
+---------+--------------+------+-----+-------------------+-------------------+
| user_id | varchar(255) | NO   | PRI | NULL              |                   |
| name    | varchar(255) | NO   |     | NULL              |                   |
| pw      | varchar(255) | NO   |     | NULL              |                   |
| phone   | varchar(255) | NO   | UNI | NULL              |                   |
| email   | varchar(255) | NO   | UNI | NULL              |                   |
| birth   | date         | NO   |     | NULL              |                   |
| signup  | datetime     | YES  |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
+---------+--------------+------+-----+-------------------+-------------------+
 * */

@Data
@Entity
@Table(name = "USER")
public class UserDto {
	@Id
	@Column(name = "user_id")
	private String userId;
	private String name;   
	private String pw;     
	private String phone;  
	private String email;  
	private Date birth;  
	private Date signup;
}

