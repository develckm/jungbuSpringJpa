package com.joongbu.spring_board_jpa.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
/*
 +-----------------+--------------+------+-----+---------+----------------+
| Field           | Type         | Null | Key | Default | Extra          |
+-----------------+--------------+------+-----+---------+----------------+
| board_prefer_no | int          | NO   | PRI | NULL    | auto_increment |
| board_no        | int          | NO   | MUL | NULL    |                |
| prefer          | tinyint(1)   | YES  |     | NULL    |                |
| user_id         | varchar(255) | NO   | MUL | NULL    |                |
+-----------------+--------------+------+-----+---------+----------------+*/
@Entity
@Table(	name = "BOARD_PREFER",
		uniqueConstraints = @UniqueConstraint(columnNames ={"board_no","user_id"}))
@Data
public class BoardPreferDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_prefer_no")
	private int boardPreferNo; 
	@Column(name = "board_no")
	private int boardNo;        
	private boolean prefer;  
	@Column(name = "user_id")
	private String userId;         
}
