package com.joongbu.spring_board_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joongbu.spring_board_jpa.dto.ReplyDto;

//JpaRepository<dto(entity),pk type>: 자동으로 쿼리 생성
@Repository
public interface ReplyRepository extends JpaRepository<ReplyDto, Integer>{

}
