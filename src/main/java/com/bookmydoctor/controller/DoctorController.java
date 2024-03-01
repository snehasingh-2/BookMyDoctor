package com.bookmydoctor.controller;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookmydoctor.dto.CommanApiResponse;
import com.bookmydoctor.dto.DoctorRegisterDto;
import com.bookmydoctor.entity.User;
import com.bookmydoctor.service.UserService;
import com.bookmydoctor.utility.Constants.DoctorSpecialist;
import com.bookmydoctor.utility.Constants.ResponseCode;
import com.bookmydoctor.utility.Constants.UserRole;
import com.bookmydoctor.utility.Constants.UserStatus;

@RestController
@RequestMapping("api/doctor/")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {
	
	Logger LOG = LoggerFactory.getLogger(DoctorController.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserService userService;


	@PostMapping("register")
	public ResponseEntity<?> registerDoctor(DoctorRegisterDto doctorRegisterDto) {
		LOG.info("Recieved request for doctor register");

		CommanApiResponse response = new CommanApiResponse();
		
		User user = DoctorRegisterDto.toEntity(doctorRegisterDto);
		
		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPassword);
		user.setStatus(UserStatus.ACTIVE.value());

		User registerUser = userService.registerUser(user);

		if (registerUser != null) {
			response.setResponseCode(ResponseCode.SUCCESS.value());
			response.setResponseMessage(user.getRole() + " Doctor Registered Successfully");
			return new ResponseEntity(response, HttpStatus.OK);
		}

		else {
			response.setResponseCode(ResponseCode.FAILED.value());
			response.setResponseMessage("Failed to Register Doctor");
			return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("all")
	public ResponseEntity<?> getAllDoctor() {
		LOG.info("recieved request for getting ALL Customer!!!");
		
		List<User> doctors = this.userService.getAllUserByRole(UserRole.DOCTOR.value());
		
		LOG.info("response sent!!!");
		return ResponseEntity.ok(doctors);
	}
	
	@GetMapping("/specialist/all")
	public ResponseEntity<?> getAllSpecialist() {

		LOG.info("Received the request for getting as Specialist");
		
		List<String> specialists = new ArrayList<>();

		for (DoctorSpecialist s : DoctorSpecialist.values()) {
			specialists.add(s.value());
		}
		
		LOG.info("Response sent!!!");

		return new ResponseEntity(specialists, HttpStatus.OK);
	}

}
