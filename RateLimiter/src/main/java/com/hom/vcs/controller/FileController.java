package com.hom.vcs.controller;

import java.io.IOException;

import javax.websocket.server.PathParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/file/{fileName}")
@RequiredArgsConstructor
public class FileController {		
		
	@GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getFileContent(@PathVariable("fileName") String fileName) throws IOException 
	{		
		
		return ResponseEntity
				.ok()						
				.body("file content");
	}
	
}