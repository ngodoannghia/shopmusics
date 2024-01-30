package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.model.Voucher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoucherService {

    Voucher getVoucher(String code);
     List<Voucher> findAllEnable();
     List<Voucher> findAll();
     void delete(Voucher v);
     Voucher save(Voucher v);
     Voucher get(String uuid);
}
