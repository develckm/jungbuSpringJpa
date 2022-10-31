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
SPRING_BOARD.board
+-----------+--------------+------+-----+-------------------+-------------------+
| Field     | Type         | Null | Key | Default           | Extra             |
+-----------+--------------+------+-----+-------------------+-------------------+
| board_no  | int          | NO   | PRI | NULL              | auto_increment    |
| title     | varchar(255) | NO   |     | NULL              |                   |
| contents  | varchar(255) | YES  |     |                   |                   |
| post_time | datetime     | YES  |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
| user_id   | varchar(255) | NO   | MUL | NULL              |                   |
| views     | int          | NO   |     | 0                 |                   |
+-----------+--------------+------+-----+-------------------+-------------------+
 * */
@Data  //POJO 형식의 get set을 class로 자동 컴파일
@Entity //TABLE을 Repository가 맵핑하겠다.
@Table(name="BOARD")  //jpa가 class명을 테이블 이름으로 맵핑하기 때문에 이름이 다를때 명시
public class BoardDto {
	//**jpa 예약어로 "_"를 필드 접근자로 사용중이기 때문에 무조건 필드명은 낙타표기법을 해야한다!!!!
	@Id  //pk에 명시를 해야 동작!
	@Column(name = "board_no")
	private int boardNo; 
	private String title;    
	private String contents; 
	@Column(name = "post_time")
	private Date postTime;
	private int views;   

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false) 
	private UserDto user; //user 조인되어서 출력되는 테이블이기 때문에 board에서 수정하거나 등록할 수 없다.

//  paging을 할 수 없기 때문에 삭제
//	@OneToMany(fetch = FetchType.LAZY)
//	@JoinColumn(name = "board_no", insertable = false, updatable = false)
//	private List<ReplyDto> replyList;
	
	//@Formula : table의 필드는 아닌데 출력할 내역(바뀐 이름 Or 서브쿼리 결과)
	@Formula(value = "(SELECT COUNT(*) FROM BOARD_PREFER p WHERE p.prefer=1 AND p.board_no=board_no)") 
	private int likes;
	@Formula(value = "(SELECT COUNT(*) FROM BOARD_PREFER p WHERE p.prefer=0 AND p.board_no=board_no)") 
	private int bads;

	
}
