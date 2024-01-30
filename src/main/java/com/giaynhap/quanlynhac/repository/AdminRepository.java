package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.dto.IDashboardReport;
import com.giaynhap.quanlynhac.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    @Query( nativeQuery = true,
            value = "SELECT u.* FROM admin u WHERE u.username = :username")
    Admin findByUserName(@Param("username") String username);

    @Query( nativeQuery = true,
            value = "SELECT \n" +
                    "(SELECT COUNT(*) FROM pendding_buy WHERE `status` = 0) AS totalNewPenddingBuy,\n" +
                    "(SELECT COUNT(*) FROM user WHERE `enable` = 1) AS totalUser,\n" +
                    "(SELECT COUNT(*) FROM pendding_buy WHERE `status` = 1) AS totalAccept,\n" +
                    "(SELECT COUNT(*) FROM pendding_buy WHERE `status` = 2) AS totalRejectPenddingBuy,\n" +
                    "(SELECT COUNT(*) FROM music WHERE TYPE = 2) AS totalMusic\n")
    IDashboardReport getDashboardReport();
}
