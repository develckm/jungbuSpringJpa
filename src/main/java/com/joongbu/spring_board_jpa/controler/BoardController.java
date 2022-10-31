package com.joongbu.spring_board_jpa.controler;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.joongbu.spring_board_jpa.dto.BoardDto;
import com.joongbu.spring_board_jpa.dto.ReplyDto;
import com.joongbu.spring_board_jpa.repository.BoardRepository;
import com.joongbu.spring_board_jpa.repository.ReplyRepository; 

@Controller
@RequestMapping("/board")
public class BoardController {
	@Autowired
	BoardRepository boardRepository;
	
	@Autowired
	ReplyRepository replyRepository;
	
	@GetMapping("/list.do")
	public String list(
			@RequestParam(defaultValue="1")int page,
			Model model	) {
			int ROWS=4;
			Pageable pageable=PageRequest.of(page, ROWS, Sort.by("boardNo").descending());
			Page<BoardDto>  boardList=boardRepository.findAll(pageable);
			System.out.println("페이지 전체 수: "+boardList.getTotalPages());
			System.out.println("검색된 로우의 전체 수:"+boardList.getTotalElements());
			System.out.println("1 페이지인가? :"+boardList.isFirst());
			System.out.println("마지막 페이지인가? :"+boardList.isLast());
			System.out.println("다음 페이지가 있는가? :"+boardList.hasNext());
			System.out.println("이전 페이지가 있는가? :"+boardList.hasPrevious());
			System.out.println("현재 페이지 번호 :"+boardList.getNumber()); 
			System.out.println("한 페이지당 로우 수 :"+boardList.getNumberOfElements());
			System.out.println("페이지 :"+boardList.getPageable());

			model.addAttribute("boardList", boardList);
		return "/board/list";
	}
	@GetMapping("/detail.do")
	public String detil(
			@RequestParam(required=true) int boardNo,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int rows,
			@RequestParam(defaultValue = "replyNo") String order,
			@RequestParam(defaultValue = "DESC") String direct,
			Model model) {
		//Optional : 데이터가 null일 수 있다. 
		Optional<BoardDto> boardOpt=boardRepository.findById(boardNo);
		Sort sort=(direct.equals("DESC"))? Sort.by(order).descending():Sort.by(order);
		Pageable pageable=PageRequest.of(page, rows, sort);
		Page<ReplyDto> replyList=replyRepository.findAll(pageable);
		
		if(boardOpt.isPresent()) {
			model.addAttribute("board", boardOpt.get());
			model.addAttribute("replyList", replyList);
			return "/board/detail";			
		}else {
			return "redirect:/board/list.do";
		}
		
	}
	
}
