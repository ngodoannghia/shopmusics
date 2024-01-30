package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.dto.IDashboardReport;
import com.giaynhap.quanlynhac.model.*;
import com.giaynhap.quanlynhac.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AdminServiceIml implements  AdminSevice {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PenddingBuyRepository penddingBuyRepository;
    @Autowired
    private UserStoreRepository userStoreRepository;
    @Autowired
    MusicRepository  musicRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    PenddingBuyDetailRepository penddingBuyDetailRepository;
    @Autowired
    TrackLogRepository trackLogRepository;

    @Override
    public Admin getByUserName(String userName) {
        return adminRepository.findByUserName(userName);
    }

    @Override
    public Admin getByUUID(String uuid) {
        return adminRepository.findById(uuid).get();
    }
    @Override
    public Page<PenddingBuy> getPagePenddingBuy(int page, int limit){
        Page<Music> pageMusics = null;
        Sort typeSort =    Sort.by("createAt").descending();
        Pageable pageable =  PageRequest.of(page, limit, typeSort );
      return   penddingBuyRepository.findAll(pageable);
    }
    @Override
    public void acceptPenddingBuy(String uuid){
         PenddingBuy penddingBuy =   penddingBuyRepository.findById(uuid).get();
          if (penddingBuy.getStatus() != 0){
              return;
          }
        List<PenddingBuyDetail> details = penddingBuy.getDetails();
          if (details == null){
              return;
          }

        penddingBuy.setStatus(1);
        penddingBuy.setUpdateAt(LocalDateTime.now());
        penddingBuyRepository.save(penddingBuy);
    }
    @Override
    public void rejectPenddingBuy(String uuid){
        PenddingBuy penddingBuy =   penddingBuyRepository.findById(uuid).get();
        if (penddingBuy.getStatus() != 0){
            return;
        }
        penddingBuy.setStatus(2);
        penddingBuy.setUpdateAt(LocalDateTime.now());
        penddingBuyRepository.save(penddingBuy);
        
    }
	@Override
    public PenddingBuy 	detailPenddingBuy(String uuid){
        PenddingBuy penddingBuy =   penddingBuyRepository.findById(uuid).get();
         return  penddingBuy ;
    }

    @Override
    public IDashboardReport getReport() {
        return adminRepository.getDashboardReport();
    }

    @Override
    public List<Admin> getListAdmin() {
        return adminRepository.findAll();
    }
    @Override
    public Admin updateAdmin( Admin admin) {
        return adminRepository.save(admin);
    }
	 @Override
    public void delete( String uuid) {
         adminRepository.delete(adminRepository.getOne(uuid));
    }

    @Override
    public void deleteCategory(String uuid) {
        categoryRepository.delete(categoryRepository.getOne(uuid));
    }

    @Override
    public Category updateCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategory(String uuid) {
        return categoryRepository.getOne(uuid);
    }

    @Override
    public List<Music> listByParent(String uuid) {
        return musicRepository.listByParentId(uuid) ;
    }
	@Override
    public void deletePendingBuy(String uuid) {
        penddingBuyDetailRepository.deleteDetailBuyPending(uuid);
        penddingBuyRepository.deleteById(uuid);

    }
    @Override
    public void writeLog(String action,String user){
        try {
            TrackLog trackLog = new TrackLog();
            trackLog.setCreateAt(LocalDateTime.now());
            trackLog.setType("admin");
            trackLog.setUser(user);
            trackLog.setAction(action);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
