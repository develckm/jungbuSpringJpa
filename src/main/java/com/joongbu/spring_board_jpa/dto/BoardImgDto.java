package com.joongbu.spring_board_jpa.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
/*
+--------------+--------------+------+-----+---------+----------------+
| Field        | Type         | Null | Key | Default | Extra          |
+--------------+--------------+------+-----+---------+----------------+
| board_img_no | int          | NO   | PRI | NULL    | auto_increment |
| board_no     | int          | NO   | MUL | NULL    |                |
| img_path     | varchar(255) | NO   |     | NULL    |                |
+--------------+--------------+------+-----+---------+----------------+*/
@Entity
@Table(name = "BOARD_IMG")
@Data
public class BoardImgDto {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_img_no")
	private int boardImgNo;
	
	@Column(name = "board_no")
	private int boardNo;
	
	@Column(name = "img_path")
	private String imgPath;
}
