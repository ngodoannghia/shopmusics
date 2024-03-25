package com.giaynhap.quanlynhac.service;


import com.giaynhap.quanlynhac.model.*;
import com.giaynhap.quanlynhac.repository.MusicRepository;
import com.giaynhap.quanlynhac.repository.UserInfoRepository;
import com.giaynhap.quanlynhac.repository.UserRepository;
import com.giaynhap.quanlynhac.repository.UserStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserServiceIml implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    MusicRepository musicRepository;
    @Autowired
    UserStoreRepository userStoreRepository;
    @Override
    public User getUser(String uuid){
        return (User)userRepository.findById(uuid).get();
    }
    @Override
    public User getUserName(String username){
        return (User)userRepository.findByUserName(username);
    }
    @Override
    public User update(User user) {
        //userInfoRepository.save(user.getInfo());
        return  userRepository.save(user);
    }

    @Override
    public UserInfo getUserInfo(String uuid) {
        return userInfoRepository.findById(uuid).get();
    }

    @Override
    public UserInfo updateUserInfo(UserInfo info) {
        userInfoRepository.save(info);
        return null;
    }

    @Override
    public Page<UserHistory> getHistory(String userUUID, int page, int limit){
        return null;
    }

    @Override
    public Page<Music> getUserStore(String userUUId, int page, int limit, Integer status){
        Pageable pageable =  PageRequest.of(page, limit, Sort.by("create_at").descending());
        Page<UserStore>  pages ;
        if ( status != null) {
             pages = userStoreRepository.getUserMusicsByStatus (userUUId,status, pageable);
        } else {
            pages =  userStoreRepository.getUserMusics(userUUId, pageable);
        }
        if (pages != null ){
            return pages.map(userStore -> {
                Music music = userStore.getMusic();
                return music;
            });
        }
        return  null;
    }

    @Override
    public UserStore updateStore(UserStore store) {
        return userStoreRepository.save(store);
    }

    @Override
    public void delStore(UserStore store) {
        userStoreRepository.delete(store);
    }

    @Override
    public UserStore getStoreMusic(String musicUUID,String userUUID){



       return  userStoreRepository.getStoreByUUID(musicUUID,userUUID);
    }

    @Override
    public List<UserStore> getAllStore(String userUUID) {
        return userStoreRepository.getAllActive(userUUID);
    }

    @Override
    public UserStore getStoreById(Long id) {
        Optional<UserStore> store = userStoreRepository.findById(id);
        if (store.isPresent()){
            return store.get();
        } else {
            return null;
        }
    }
    @Override
    public Page<User> getListUser(int page, int limit){
        @SuppressWarnings("unused")
		Sort typeSort =    Sort.by("create_at").descending();
        Pageable pageable =  PageRequest.of(page, limit);
        return userRepository.findAll(pageable);
    }

    @Override
    public void deleteUser(String uuid) {
        userRepository.delete(userRepository.getOne(uuid));
    }

}
