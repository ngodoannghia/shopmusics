package com.giaynhap.quanlynhac.repository;
import com.giaynhap.quanlynhac.model.FileStore;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStoreRepository extends CrudRepository<FileStore, String> {
}
