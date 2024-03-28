package com.giaynhap.quanlynhac.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;


public class FileProcess {
	public String save_file;
	public MultipartFile file;
	
	public FileProcess(String save_file, MultipartFile file) {
		this.save_file = save_file;
		this.file = file;
	}
	
	public boolean saveFile() throws IOException {
		if (!file.isEmpty()) {
			File outputFile = new File(save_file);
			
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
			
            outputStream.write(file.getBytes());
            outputStream.close();
            
			return true;
		}
		else {
			return false;
		}
	}
}
