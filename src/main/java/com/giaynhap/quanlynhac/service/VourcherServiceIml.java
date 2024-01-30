package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.model.Voucher;
import com.giaynhap.quanlynhac.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class VourcherServiceIml implements VoucherService {
    @Autowired
    VoucherRepository voucherRepository;
    @Override
    public Voucher getVoucher(String code) {
        return voucherRepository.findByCode(code);
    }
    @Override
    public List<Voucher> findAllEnable(){
        return voucherRepository.getAllEnable();
    }
    @Override
    public List<Voucher> findAll(){
        return voucherRepository.findAll();
    }
    @Override
    public void delete(Voucher v){
        voucherRepository.delete(v);
    }
    @Override
    public Voucher save(Voucher v){
        return voucherRepository.save(v);
    }
    @Override
    public Voucher get(String uuid){
        return voucherRepository.findById(uuid).get();
    }
}
