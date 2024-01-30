package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.model.PenddingBuyDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface PenddingBuyDetailRepository extends JpaRepository<PenddingBuyDetail, String> {
	
    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "delete from pendding_buy_detail where  p_uuid = :pid"
    )
   void deleteDetailBuyPending(@Param("pid") String p);
}
