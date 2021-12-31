package com.ggoreb.practice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ggoreb.practice.model.Answer;
import com.ggoreb.practice.model.FileAtch;
import com.ggoreb.practice.model.Question;
import com.ggoreb.practice.model.User;
import com.ggoreb.practice.repository.AnswerRepository;
import com.ggoreb.practice.repository.FileAtchRepository;
import com.ggoreb.practice.repository.QuestionRepository;
import com.ggoreb.practice.service.QuestionService;

@Controller
public class QuestionController {
	@Autowired
    QuestionRepository questionRepository;

	@Autowired
    AnswerRepository answerRepository;

    @Autowired
    QuestionService questionService;

    @Autowired
    FileAtchRepository fileAtchRepository; 
	
	@GetMapping("/question/list")
	public String question(Model model, @RequestParam(value="page", defaultValue = "1") int page) {
		List<Question> list = questionService.getQuestionList(page);
		model.addAttribute("list", list);
		return "question_list";
	}

	@GetMapping("/question/create")
	public String questionCreate() {  

		return "question_create";
	}

	@PostMapping("/question/create")
	public String questionCreatePost(@ModelAttribute Question question, HttpSession session,
	@RequestParam("image") MultipartFile image) {
		
		try {
			image.transferTo(new File("c:/study/" + image.getOriginalFilename()));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        // 질문 등록
		User user = (User) session.getAttribute("user");
        questionService.addQuestion(question, user, image.getOriginalFilename());

		// 질문의 첨부파일 등록
		

		return "redirect:/question/list";
	}

	@GetMapping("/download")
	public ResponseEntity<Resource> download(Long id) throws Exception {

		Question question = questionRepository.findById(id).get();

		List<Question> fileAtch = fileAtchRepository.findByQuestion(question);
		//String fileName = fileAtch.getFileName();
		Optional<FileAtch> opt = fileAtchRepository.findById(id);
		FileAtch fileatch = opt.get();
		
	
		File file = new File("c:/study/theman.png");
    

		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		return ResponseEntity.ok()
				.header("content-disposition",
						"filename=" + URLEncoder.encode(file.getName(), "utf-8"))
				.contentLength(file.length())
				
				//.contentType(MediaType.parseMediaType("application/octet-stream"))
				.contentType((MediaType.parseMediaType("image/png"))
				.body(resource);
	}


	@GetMapping("/question/detail")
	public String questionDetail(HttpSession session, Model model, Long id) {
		
		Optional<Question> opt = questionRepository.findById(id);

		if(opt.isPresent()) {
			model.addAttribute("question", opt.get());
			model.addAttribute("answerList", answerRepository.findByQuestion(opt.get()));
		}             
		return "question_detail";
	}

	@PostMapping("/answer/create")
	public String answerCreatePost(@ModelAttribute Answer answer, HttpSession session, @RequestParam Long question_id) {
		User user = (User) session.getAttribute("user");
		answer.setUser(user);
		answer.setCreateDate(new Date());

		Question question = questionRepository.findById(question_id).get();
		answer.setQuestion(question);

		answerRepository.save(answer);
		return "redirect:/question/detail?id=" + question_id;
	}

	

	
}
