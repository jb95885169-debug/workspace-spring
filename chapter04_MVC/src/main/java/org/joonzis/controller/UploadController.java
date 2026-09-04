package org.joonzis.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


import org.joonzis.domain.BoardAttachVO;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import lombok.extern.log4j.Log4j;

@Log4j
@Controller
public class UploadController {
	@GetMapping("/uploadForm")
	public String uploadForm() {
		log.info("upload form...");
		return "uploadForm";
	}
	
	@PostMapping("/uploadFormAction")
	public void uploadFormPost(MultipartFile[] uploadFile) {
		for(MultipartFile multipartFile : uploadFile){
			log.info("---------------------");
			log.info("Upload File Name : " + multipartFile.getOriginalFilename());
			log.info("Upload File Size : " + multipartFile.getSize());
	
			
		}
	}
	
	
	/*
	 *  MultipartFile의 메소드
	 *  String get Name() : 파라미터 이름
	 *  String getOriginalFileName() : 업로드 되는 파일의 이름
	 *  boolean isEmpty() : 파일이 존재하지 않는 경우 true
	 *  long getSize() : 업로드 되는 파일의 크기
	 *  byte[] getBytes() : byte[] 로 파일 데이터 반환
	 *  InputStream getInputStream() : 파일 데이터와 연결된 InputStream 반환
	 *  transferTo(File file) : 파일 저장
	 * */
	
	@GetMapping("/uploadAsync")
	public String uploadAsync() {
		log.info("upload async...");
		return "uploadAsync";
	}
	
	@ResponseBody
	@PostMapping(value = "/uploadAsyncAction",
				produces = MediaType.APPLICATION_JSON_UTF8_VALUE		)
	public ResponseEntity<List<BoardAttachVO>> uploadAsyncPost(MultipartFile[] uploadFile) {
		log.info("uploadAsyncPost...");

		List<BoardAttachVO> list = new ArrayList<BoardAttachVO>();
		
		String uploadFolder = "c:\\upload";

		// make folder----------------
		File uploadPath = new File(uploadFolder, getFolder());
		log.info("uploadPath : "+ uploadPath);

		if(!uploadPath.exists()){
			uploadPath.mkdirs();
		}


		
		for(MultipartFile multipartFile : uploadFile){
			
			// 파일 정보를 담을 AttachFileDTO 객체 생성
			BoardAttachVO attachDTO = new BoardAttachVO();
			
			log.info("---------------------");
			log.info("Upload File Name : " + multipartFile.getOriginalFilename());
			log.info("Upload File Size : " + multipartFile.getSize());
			
			String uploadFileName = multipartFile.getOriginalFilename();
			
			uploadFileName = uploadFileName.substring(
					uploadFileName.lastIndexOf("\\")+1);
			log.info("only file name : " + uploadFileName);
			
			UUID uuid = UUID.randomUUID();// 고유값 주고 싶을때 사용
			uploadFileName = uuid.toString() + "_" + uploadFileName;


			try {
				File saveFile = new File(uploadPath, uploadFileName);
				multipartFile.transferTo(saveFile);
				
				attachDTO.setUuid(uuid.toString());
				attachDTO.setUploadPath(getFolder());
				attachDTO.setFileName(multipartFile.getOriginalFilename());
				// fileName을 저장할 때 uploadFileName은 uuid까지 포함되어있기 때문에
				// 순수 파일 명만 저정
				
				// 파일 정보를 담은 attachDTO 객체를 List 에 저장
				list.add(attachDTO);
				
				
			}catch(Exception e) {
				log.error(e.getMessage());
				}
			}// end loop
		
			return new ResponseEntity<>(list, HttpStatus.OK);

		}
	// 오늘 날짜의 경로를 문자열로 생성
	private String getFolder(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String str = sdf.format(date);
		return str.replace("-", File.separator);
	}
	/*
		File.separator
		- 운영체제에 따라 파일 경로 구분자를 자동으로 맞춰주는 값
		- window : '\'
		- Mac,Linux : '/'
	*/
	
	// 파일 다운로드 메소드
	@ResponseBody
	@GetMapping(value = "/download",
				produces = MediaType.APPLICATION_OCTET_STREAM_VALUE	)
	public ResponseEntity<Resource> download(String fileName){
		log.info("download file ..." + fileName);
		org.springframework.core.io.Resource resource = 
				new FileSystemResource("C:\\upload\\" + fileName);
		log.info("resource : " + resource);
		
		String resourceName = resource.getFilename();
		HttpHeaders headers = new HttpHeaders();
		
		// 원ㄴ본 파일 이름으로 다운로드 하고 싶으면
//		1. uuid를 제거한 진짜 파일명 추출
//		String originalFileName = 
//			resourceName.substring(resourceName.indexOf("_") +1);
//		
//		2. 아래 new String(resourceName.getBytes("utf-8"))코드를
//		new String(originalFileName.getBytes("utf-8")) 로 변경
		
		try {
			headers.add("Content-Disposition",
					"attachment; fileName=" + 
					new String(resourceName.getBytes("utf-8"),
							"ISO-8859-1"));
	
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Resource>(resource, headers,HttpStatus.OK );
	}

	
	// 파일삭제
	@PostMapping("/deleteFile")
	@ResponseBody
	public ResponseEntity<String> deleteFile(
				@RequestBody String fileName){
		log.info("deleteFile.. : " + fileName);

		File file;

		try{
			file = new File("C:\\upload\\" + URLDecoder.decode(fileName, "utf-8"));
			file.delete();
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<String>("deleted", HttpStatus.OK);
		}
	
	
}








