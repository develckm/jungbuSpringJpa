package com.joongbu.spring_board_jpa.controler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.aspectj.weaver.Iterators;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.joongbu.spring_board_jpa.dto.BoardDto;
import com.joongbu.spring_board_jpa.dto.BoardImgDto;
import com.joongbu.spring_board_jpa.dto.BoardPreferDto;
import com.joongbu.spring_board_jpa.dto.ReplyDto;
import com.joongbu.spring_board_jpa.dto.UserDto;
import com.joongbu.spring_board_jpa.repository.BoardImgRepository;
import com.joongbu.spring_board_jpa.repository.BoardPreferRepository;
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
	
	@Autowired
	BoardPreferRepository boardPreferRepository;
	
	@Value("${spring.servlet.multipart.location}")
	String imgSavePath;
	
	@GetMapping("/list.do")
	public String list(
			@RequestParam(defaultValue="0")int page,
			Model model	) {
			int ROWS=4;
			Pageable pageable=PageRequest.of(page, ROWS, Sort.by("boardNo").descending());
			Page<BoardDto>  boardList=boardRepository.findAll(pageable);
			System.out.println("????????? ?????? ???: "+boardList.getTotalPages());
			System.out.println("????????? ????????? ?????? ???:"+boardList.getTotalElements());
			System.out.println("1 ???????????????? :"+boardList.isFirst());
			System.out.println("????????? ???????????????? :"+boardList.isLast());
			System.out.println("?????? ???????????? ?????????? :"+boardList.hasNext());
			System.out.println("?????? ???????????? ?????????? :"+boardList.hasPrevious());
			System.out.println("?????? ????????? ?????? :"+boardList.getNumber()); 
			System.out.println("??? ???????????? ?????? ??? :"+boardList.getNumberOfElements());
			System.out.println("????????? :"+boardList.getPageable());

			model.addAttribute("boardList", boardList);
		return "/board/list";
	}
	@GetMapping("/detail.do")
	public String detil(
			@RequestParam(required=true) int boardNo,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int rows,
			@RequestParam(defaultValue = "replyNo") String order,
			@RequestParam(defaultValue = "DESC") String direct,
			@SessionAttribute(required = false) UserDto loginUser,
			Model model) {
		//Optional : ???????????? null??? ??? ??????. 
		Optional<BoardDto> boardOpt=boardRepository.findById(boardNo);
		if(loginUser!=null) {
			Optional<BoardPreferDto> boardPreferOpt=
					boardPreferRepository.getByUserIdAndBoardNo(loginUser.getUserId(), boardNo);
			if(boardPreferOpt.isPresent()) {
				model.addAttribute("boardPrefer", boardPreferOpt.get());
			}
		}
		
		Sort sort=(direct.equals("DESC"))? Sort.by(order).descending():Sort.by(order);
		Pageable pageable=PageRequest.of(page, rows, sort);
		Page<ReplyDto> replyList=replyRepository.findByBoardNo(boardNo,pageable);
		
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
			session.setAttribute("msg", "????????? ????????? ???????????? ????????? ??? ??? ????????????.");
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
			session.setAttribute("msg", "????????? ??? ????????? ???????????? ???????????? ????????????.");
			return "redirect:/user/login.do";
		}
		BoardDto saveBoard=null;
		try {
			saveBoard=boardRepository.save(board);
			//?????? ?????? ???????????? Update,????????? Insert				
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
			@SessionAttribute(required = false) UserDto loginUser,
			HttpSession session,
			Model model
			) {
		if(loginUser==null) {
			session.setAttribute("msg", "????????? ??? ?????? ?????????!");
			return "redirect:/user/login.do";
		}
		Optional<BoardDto> boardOpt=boardRepository.findById(boardNo);
		if(boardOpt.isPresent()) {
			if(boardOpt.get().getUser().getUserId().equals(loginUser.getUserId())) {
				model.addAttribute("board", boardOpt.get());
				return "/board/update";							
			}else {
				session.setAttribute("msg", "???????????? ?????? ???????????????.");
				return "redirect:/user/login.do";
			}
			
		}else {
			return "redirect:/baord/list.do";
		}
	}
	@PostMapping("/update.do")
	public String update(
			BoardDto board,
			@RequestParam( name = "boardImgNo",required = false) int[] boardImgNos,
			@RequestParam(name="img") MultipartFile[] imgs) {
		BoardDto saveBoard=null;
		try {
			//boardImgRepository
			saveBoard=boardRepository.save(board);
			if(boardImgNos!=null) {
				for(int boardImgNo:boardImgNos) {
					Optional<BoardImgDto> boardImgOpt=boardImgRepository.findById(boardImgNo);
					if(boardImgOpt.isPresent()) {
						File imgFile=new File(imgSavePath+"/"+boardImgOpt.get().getImgPath());
						imgFile.delete();
						boardImgRepository.deleteById(boardImgNo);
					}
				}				
			}
			long imgCount=boardImgRepository.countByBoardNo(board.getBoardNo());
			//imgCount 4??? ????????? ??? ??? ??????.
			for(MultipartFile img: imgs) {
				if(imgCount>=4) {break;}
				if(!img.isEmpty()) {
					String []contentTypes=img.getContentType().split("/");
					if(contentTypes[0].equals("image")) {
						String fileName="board_"+System.currentTimeMillis()+"_"+((int)(Math.random()*10000))+"."+contentTypes[1];
						Path path=Paths.get(imgSavePath+"/"+fileName);
						img.transferTo(path);
						BoardImgDto boardImg=new BoardImgDto();
						boardImg.setBoardNo(board.getBoardNo());
						boardImg.setImgPath(fileName);
						boardImgRepository.save(boardImg);
						imgCount++;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(saveBoard!=null) {
			return "redirect:/board/detail.do?boardNo="+saveBoard.getBoardNo();
		}else {
			return "redirect:/board/update.do?boardNo="+board.getBoardNo();

		}
	}
	@GetMapping("/delete.do")
	public String delete(
			@RequestParam(required = true)int boardNo,
			@SessionAttribute(required = false)UserDto loginUser,
			HttpSession session) {
		Optional<BoardDto> boardOpt=boardRepository.findById(boardNo);
		if(boardOpt.isPresent()) {
			System.out.println(boardOpt.get());
			if(loginUser==null) {
				session.setAttribute("msg", "????????? ??? ???????????????!");
				return "redirect:/user/login.do";
			}else if(!loginUser.getUserId().equals(boardOpt.get().getUser().getUserId())){
				session.setAttribute("msg", "???????????? ????????????!");
				return "redirect:/user/login.do";
			}
			for(BoardImgDto  boardImg :boardOpt.get().getBoardImgList()) {
				File imgFile=new File(imgSavePath+"/"+boardImg.getImgPath());
				imgFile.delete(); //????????? ?????? ??????
			}
			boardImgRepository.deleteByBoardNo(boardOpt.get().getBoardNo());
			System.out.println("????????? db ?????? ??????");
			boardRepository.deleteById(boardOpt.get().getBoardNo());
			System.out.println("????????? ?????? ??????");
		}else {
			session.setAttribute("msg", "?????? ????????? ????????? ?????????.");
		}
		
		
		//boardRepository.deleteById(boardNo);
		return "redirect:/board/list.do";
	}
	
	
	@GetMapping("/prefer/{boardNo}/{preferBtn}")
	public String preferManagin(
			@PathVariable int boardNo,
			@PathVariable boolean preferBtn,
			@SessionAttribute(required = false) UserDto loginUser,
			HttpSession session
			) {
		if(loginUser==null) {
			session.setAttribute("msg", "????????? ???????????? ????????? ??? ?????? ??????!");
			return "redirect:/user/login.do";
		}
		Optional<BoardPreferDto> BoardPreferOpt=
				boardPreferRepository.getByUserIdAndBoardNo(loginUser.getUserId(), boardNo);
		try {
			if(BoardPreferOpt.isEmpty()) {
				BoardPreferDto boardPrefer=new BoardPreferDto();
				boardPrefer.setPrefer(preferBtn);
				boardPrefer.setUserId(loginUser.getUserId());
				boardPrefer.setBoardNo(boardNo);
				boardPreferRepository.save(boardPrefer);
			}else{
				//???????????? ???????????? ?????? ???????????? ?????? (??????)
				if(BoardPreferOpt.get().isPrefer()==preferBtn) {
					boardPreferRepository.deleteById(BoardPreferOpt.get().getBoardPreferNo());
				}else { //???????????? ???????????? ???????????? ??????(??????)
					BoardPreferOpt.get().setPrefer(preferBtn);
					boardPreferRepository.save(BoardPreferOpt.get());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/board/detail.do?boardNo="+boardNo;
	}
	
}









