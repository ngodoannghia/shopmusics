package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.model.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, String> {

    @Query(
            value = "select a.* from music as a where a.type = 2 and a.status = 1",
 	    countQuery="select count(*) from music as a where a.type = 2  and a.status = 1",
            nativeQuery = true
    )

    Page<Music> pageMusicDemoWithSort(Pageable pageable);

     @Query(
            value = "select a.* from music as a where a.type = 2 and category = :category",
 	    countQuery="select count(*) from music as a where a.type = 2  and category = :category",
            nativeQuery = true
    )

    Page<Music> pageMusicDemoWithSort(@Param("category") String category,Pageable pageable);
    @Query(
            value = "select a.* from music as a where a.type = 2 ",
            countQuery="select count(*) from music as a where a.type = 2 ",
            nativeQuery = true
    )

    Page<Music> pageMusicDemoAll(Pageable pageable);

    @Query(
            value = "select a.* from music as a where a.type = 1",
 	    countQuery="select count(*) from music as a where a.type = 1",
            nativeQuery = true
    )
    Page<Music> pageMusicWithSort(Pageable pageable);


    @Query(
            value = "select a.* from music as a where a.type = 1 and a.parent = :parentId",
            nativeQuery = true
    )
    List<Music> listByParentId(@Param("parentId") String category);

  
}
