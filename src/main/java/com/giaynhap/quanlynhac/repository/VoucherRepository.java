package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, String> {
    @Query(
            nativeQuery = true,
            value = "SELECT u.* FROM voucher u WHERE u.code = :code" )
    Voucher findByCode(@Param("code") String code);

    @Query(
            nativeQuery = true,
            value = "SELECT u.* FROM voucher u WHERE u.enable = 1" )
    List<Voucher> getAllEnable();

}
