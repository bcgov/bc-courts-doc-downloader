package ca.bc.gov.ag.courts.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.repo.JobRepository;

@RestController
public class JobController {

	Logger logger = LoggerFactory.getLogger(JobController.class);

	@Autowired
	JobRepository repo;

	@GetMapping("/jobs")
	public ResponseEntity<List<Job>> getJobs() {
		
		logger.info("get Jobs called"); 
		
		List<Job> result = 
				  StreamSupport.stream(repo.findAll().spliterator(), false)
				    .collect(Collectors.toList());
		
		return new ResponseEntity<List<Job>>(result, HttpStatus.OK);
		
	}
	
	@GetMapping(path="/jobs/{id}", produces = "application/json")
	public ResponseEntity<Optional<Job>> getJobById(@PathVariable String id) {
		
		logger.info("get Job for id " + id + " called"); 
		
		return new ResponseEntity<Optional<Job>>(repo.findById(id), HttpStatus.OK);
	}
	
	@PostMapping(path="/job", consumes = "application/json", produces = "application/json")
	public String postJob(@RequestBody Job job) {

		logger.info("Add Job called. Job being added to the repo: " + job.toString() );

		repo.findById(job.getId()).orElseThrow(() -> new IllegalArgumentException("Job already exists for id: " + job.getId()));
		repo.save(job);
		
		return "New job added";
	}
	
	@PutMapping(path="/job", consumes = "application/json", produces = "application/json")
	public String updateJob(@RequestBody Job job) {

		logger.info("Update Job called. Job being updated is : " + job.toString() );
		repo.findById(job.getId()).orElseThrow(() -> new IllegalArgumentException("Job not found for id: " + job.getId()));
		repo.save(job); // note - use of same repo method as add. 
		
		return "Job updated";
	}
	
	@DeleteMapping(path="/job/{id}", produces = "application/json")
	public String deleteJob(@PathVariable String id) {

		logger.info("Delete Job called. Job being deleted is : " + id ); 
		repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Job not found for id: " + id));
		repo.deleteById(id); // note - use of same repo method as add.
		
		return "Job deleted";
	}

}