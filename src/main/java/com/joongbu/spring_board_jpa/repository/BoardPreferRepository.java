package com.joongbu.spring_board_jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joongbu.spring_board_jpa.dto.BoardPreferDto;
@Repository
public interface BoardPreferRepository extends JpaRepository<BoardPreferDto, Integer>{
	Optional<BoardPreferDto> getByUserIdAndBoardNo(String userId,int boardNo);
}
