package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.model.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository  extends CrudRepository<UserInfo, String> {

}
