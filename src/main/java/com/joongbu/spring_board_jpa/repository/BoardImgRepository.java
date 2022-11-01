package com.joongbu.spring_board_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joongbu.spring_board_jpa.dto.BoardImgDto;

@Repository
public interface BoardImgRepository extends JpaRepository<BoardImgDto, Integer> {

}
