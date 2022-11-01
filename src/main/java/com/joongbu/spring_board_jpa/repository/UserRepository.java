package com.joongbu.spring_board_jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.joongbu.spring_board_jpa.dto.UserDto;
@Repository
public interface UserRepository extends JpaRepository<UserDto, String>{
	//findByUserIdAndPw
	//findOptionalByUserIdAndPw (1개만 반환 Optional)
	//getByUserIdAndPw (1개만 반환)
	Optional<UserDto> getByUserIdAndPw(String userId,String pw);
	//***jap를 사용하는 이유!! :db가 공동 sql을 사용하지 않고 있어서 Jpa가 db에 맞는 쿼리를 생성 => @Entity 기반 쿼리 작성
	//@Query( nativeQuery=true, value= "SELECT * FROM user u WHERE u.user_id=:userId AND u.pw=:pw")
	@Query(value = "SELECT u FROM UserDto u WHERE u.userId=:userId AND u.pw=:pw")
	Optional<UserDto> selectByUserIdAndPw(String userId,String pw);

}
