package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.model.UserHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHistoryRepository  extends CrudRepository<UserHistory, Long> {
}
