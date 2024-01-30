package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.model.PenddingBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenddingBuyRepository extends JpaRepository<PenddingBuy, String> {
}
