package com.giaynhap.quanlynhac.repository;

import com.giaynhap.quanlynhac.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, String> {
}
