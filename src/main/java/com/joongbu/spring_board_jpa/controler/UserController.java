package com.joongbu.spring_board_jpa.controler;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.joongbu.spring_board_jpa.dto.UserDto;
import com.joongbu.spring_board_jpa.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/login.do")
	public void login(
			HttpServletRequest req,
			HttpSession session
			) {
			String refererPage=req.getHeader("Referer");//로그인 폼 오기 전 페이지
			session.setAttribute("redirectPage", refererPage);				
	}
	
	
	@PostMapping("/login.do")
	public String login(
			String userId,
			String pw,
			HttpSession session,
			@SessionAttribute(required=false) String redirectPage
			) {
		Optional<UserDto> loginUserOpt=null;
		//loginUserOpt=userRepository.getByUserIdAndPw(userId,pw);
		loginUserOpt=userRepository.getByUserIdAndPw(userId, pw);
		if(loginUserOpt.isEmpty()) {
			return "redirect:/user/login.do";
		}else {
			session.setAttribute("loginUser", loginUserOpt.get());
			if(redirectPage!=null) {
				return "redirect:"+redirectPage;				
			}else {
				return "redirect:/";				
			}
		}
	}
	@GetMapping("/logout.do")
	public String logout(
			HttpSession session
			) {
		session.removeAttribute("loginUser");
		return "redirect:/";
	}
}






