package com.inkbox.boot.demo.dao;

import com.inkbox.boot.demo.dos.GoodsDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsDao extends JpaRepository<GoodsDo,Long> {
}
