package com.inkbox.boot.demo.dao;

import com.inkbox.boot.demo.dos.UserDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao  extends JpaRepository<UserDo,Long> {
}
