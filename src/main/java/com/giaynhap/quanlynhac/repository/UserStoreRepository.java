package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.model.UserStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserStoreRepository extends JpaRepository<UserStore, Long> {
    @Query(
            nativeQuery = true,
            value = "SELECT u.* FROM user_store u WHERE u.music_id = :uuid AND u.user_id = :userUUID " )
    UserStore getStoreByUUID(@Param("uuid") String uuid, @Param("userUUID") String userUUID);
    @Query(
            nativeQuery = true,
            value = "SELECT u.* FROM user_store u WHERE u.user_id = :userUUID and u.time_start is not null" )
    List<UserStore> getAllActive(@Param("userUUID") String userUUID);
  @Query(
            nativeQuery = true,
            value = "select * from user_store where  user_id = :userId",
 	    countQuery="select count(*) from user_store  where  user_id = :userId"
    )
    Page<UserStore> getUserMusics(@Param("userId") String userUUID, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select * from user_store  where  user_id = :userId and status = :status",
	   countQuery="select count(*) from user_store where  user_id = :userId and status = :status"
    )
    Page<UserStore> getUserMusicsByStatus(@Param("userId") String userUUID,@Param("status") Integer status,Pageable pageable);

    @Modifying
    @Transactional
    @Query(
            nativeQuery = true,
            value = "delete from user_store  where  music_id = :music_id "
    )
   void deleteByMusicId(@Param("music_id") String music);

    @Modifying
    @Transactional
    @Query(
        nativeQuery = true,
        value = "delete from user_store where music_id = :music_id and user_id = :user_id "
    )
    void deleteMusicOfUser(@Param("music_id") String musicUUID, @Param("user_id") String userUUID);
}
