package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.dto.IDashboardReport;
import com.giaynhap.quanlynhac.model.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminSevice {
    Admin getByUserName(String userName);
    Admin getByUUID(String uuid);
     Page<PenddingBuy> getPagePenddingBuy(int page, int limit);
    void acceptPenddingBuy(String uuid);
    void rejectPenddingBuy(String uuid);
    PenddingBuy detailPenddingBuy(String uuid);
    IDashboardReport getReport();
    void deletePendingBuy(String uuid);
    List<Admin> getListAdmin();
    Admin updateAdmin( Admin admin);
	 void delete( String uuid);

	void deleteCategory(String uuid);
	Category updateCategory(Category category);
    Category getCategory(String uuid);

    List<Music> listByParent(String uuid);
    public void writeLog(String action,String user);

}
