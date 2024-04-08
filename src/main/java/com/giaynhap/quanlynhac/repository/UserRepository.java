package com.giaynhap.quanlynhac.repository;


import com.giaynhap.quanlynhac.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(
            nativeQuery = true,
            value = "SELECT u.* FROM user u WHERE u.username = :username AND u.password = :password " )
    User selectByUserNamePassword(@Param("username") String username, @Param("password") String password);

    @Query( nativeQuery = true,
            value = "SELECT u.* FROM user u WHERE u.username = :username")
    User findByUserName(@Param("username") String username);

    @Query( nativeQuery = true,
        value = "SELECT u.* FROM user u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

}