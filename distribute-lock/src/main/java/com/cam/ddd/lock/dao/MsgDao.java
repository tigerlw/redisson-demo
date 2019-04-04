package com.cam.ddd.lock.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cam.ddd.lock.model.MsgModel;

@Mapper
public interface MsgDao {
	
	public List<MsgModel> findByMsg(@Param("msg") String msg);
	
	public int update(MsgModel msgModel);

}
