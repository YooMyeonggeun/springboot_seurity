package com.example.security1.repository;

import com.example.security1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD 함수를 JpaRepository가 들고 있음
//@Repository라는 어노테이션이 없어도 IOC되요 이유는 JpaRepository를 상속했기 때문에
public interface UserRepository extends JpaRepository<User, Integer> {

    //select * from user where username = username;
    public User findByUsername(String username); // Jpa name 함수, Jpa Query method로 검색하면 나옴

    public User findByEmail(String Eamil);
}
