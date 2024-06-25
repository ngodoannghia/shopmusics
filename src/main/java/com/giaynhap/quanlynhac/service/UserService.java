package com.giaynhap.quanlynhac.service;

import com.giaynhap.quanlynhac.model.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public
interface UserService{
    User getUser(String uuid);
    User getUserName(String username);
    User getEmail(String email);
    User update(User users);
    UserInfo getUserInfo(String uuid);
    UserInfo updateUserInfo(UserInfo info);
    Page<UserHistory> getHistory(String userUUID, int page, int limit);
    Page<Music> getUserStore(String userUUId, int page, int limit, Integer status);
    UserStore updateStore(UserStore store);
    void delStore(UserStore store);
    void delMusicOfUser(String musicUUID,String userUUID);
    UserStore getStoreMusic(String musicUUID,String userUUID);
    List<UserStore> getAllStore(String userUUID);
    UserStore getStoreById(Long id);
    Page<User> getListUser(int page, int limit);
    void deleteUser(String uuid);
}
