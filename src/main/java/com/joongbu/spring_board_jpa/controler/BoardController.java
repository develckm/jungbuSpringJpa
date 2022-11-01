package com.joongbu.spring_board_jpa.controler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.joongbu.spring_board_jpa.dto.BoardDto;
import com.joongbu.spring_board_jpa.dto.BoardImgDto;
import com.joongbu.spring_board_jpa.dto.ReplyDto;
import com.joongbu.spring_board_jpa.dto.UserDto;
import com.joongbu.spring_board_jpa.repository.BoardImgRepository;
import com.joongbu.spring_board_jpa.repository.BoardRepository;
import com.joongbu.spring_board_jpa.repository.ReplyRepository; 

@Controller
@RequestMapping("/board")
public class BoardController {
	@Autowired
	BoardRepository boardRepository;
	
	@Autowired
	ReplyRepository replyRepository;
	
	@Autowired
	BoardImgRepository boardImgRepository;
	
	@Value("${spring.servlet.multipart.location}")
	String imgSavePath;
	
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
	@GetMapping("/insert.do")
	public String insert(
			@SessionAttribute(required=false) UserDto loginUser,
			HttpSession session
			) {
		if(loginUser!=null) {
			return "/board/insert";			
		}else{
			session.setAttribute("msg", "게시글 등록은 로그인을 하셔야 할 수 있습니다.");
			return "redirect:/user/login.do";
		}
	}
	@PostMapping("/insert.do")
	public String insert(
			BoardDto board,
			@RequestParam("img") MultipartFile[] imgs,
			@SessionAttribute UserDto loginUser,
			HttpSession session
			) {
		if(!board.getUser().getUserId().equals(loginUser.getUserId())) {
			session.setAttribute("msg", "로그인 한 유저와 글쓴이가 동일하지 않습니다.");
			return "redirect:/user/login.do";
		}
		BoardDto saveBoard=null;
		try {
			saveBoard=boardRepository.save(board);
			//만약 값이 존재하면 Update,없으면 Insert				
			for(MultipartFile img: imgs) {
				if(!img.isEmpty()) {
					String []contentTypes=img.getContentType().split("/"); // application/json, image/jpeg
					if(contentTypes[0].equals("image")) {
						String fileName="board_"+System.currentTimeMillis()+"_"+((int)(Math.random()*10000))+"."+contentTypes[1];
						Path path=Paths.get(imgSavePath+"/"+fileName);
						img.transferTo(path);
						BoardImgDto boardImg=new BoardImgDto();
						boardImg.setBoardNo(saveBoard.getBoardNo());
						boardImg.setImgPath(fileName);
						boardImgRepository.save(boardImg);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(saveBoard==null) {
			return "redirect:/board/insert.do";			
		}else {
			return "redirect:/board/detail.do?boardNo="+saveBoard.getBoardNo();			
		}
	}
	@GetMapping("/update.do")
	public String update(
			@RequestParam("boardNo") int boardNo,
			@SessionAttribute UserDto loginUser,
			HttpSession session
			) {
		return "/board/update";
	}
	
}
