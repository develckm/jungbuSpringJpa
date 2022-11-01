package com.joongbu.spring_board_jpa.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import lombok.Data;
/*
+-------------+--------------+------+-----+-------------------+-------------------+
| Field       | Type         | Null | Key | Default           | Extra             |
+-------------+--------------+------+-----+-------------------+-------------------+
| reply_no    | int          | NO   | PRI | NULL              | auto_increment    |
| title       | varchar(255) | NO   |     | NULL              |                   |
| contents    | varchar(255) | YES  |     |                   |                   |
| post_time   | datetime     | YES  |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| img_path    | varchar(255) | YES  |     | NULL              |                   |
| board_no    | int          | NO   | MUL | NULL              |                   |
| user_id     | varchar(255) | NO   | MUL | NULL              |                   |
| fk_reply_no | int          | YES  | MUL | NULL              |                   |
+-------------+--------------+------+-----+-------------------+-------------------+*/
@Data
@Entity
@Table(name = "REPLY")
public class ReplyDto {
	@Id
	@Column(name = "reply_no")
	private int replyNo; //PK 
	@Column(name = "board_no")
	private int boardNo; //FK: baord.baord_no 
	private String title;    
	private String contents; 
	@Column(name = "img_path")
	private String imgPath; 
	@Column(name = "post_time")
	private Date postTime;
	//private String userId;//FK: user.user_id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id",insertable=false,updatable=false)
	private UserDto user;
	@Formula(value = "(select count(*) from reply_prefer rp where rp.prefer=1 AND rp.reply_no=reply_no)")
	private int likes;    
	@Formula(value = "(select count(*) from reply_prefer rp where rp.prefer=0 AND rp.reply_no=reply_no)")
	private int bads;    
	@Column(name = "fk_reply_no")
	private Integer fkReplyNo; //FK : self join(reply.reply_no is null)
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="fk_reply_no",insertable = false,updatable = false)
	private List<ReplyDto> replyList; //reply : reply = 1 : N   대 댓글
}


