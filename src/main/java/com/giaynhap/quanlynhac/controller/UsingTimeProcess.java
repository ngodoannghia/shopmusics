package com.giaynhap.quanlynhac.controller;

import com.giaynhap.quanlynhac.manager.MusicManager;
import com.giaynhap.quanlynhac.model.UserStore;
import com.giaynhap.quanlynhac.service.MusicService;
import com.giaynhap.quanlynhac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Component
public class UsingTimeProcess {

}
