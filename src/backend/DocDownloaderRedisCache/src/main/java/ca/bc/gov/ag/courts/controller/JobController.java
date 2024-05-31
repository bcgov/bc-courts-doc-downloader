package ca.bc.gov.ag.courts.controller;

import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.repo.JobRepository;
import ca.bc.gov.ag.courts.util.JobValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @GetMapping(path = "/jobs/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optional<Job>> getJobById(@PathVariable String id) {

        logger.info("get Job for id " + id + " called");
        
        Optional<Job> job = repo.findById(id);
        
        if (job.isEmpty()) {
        	return new ResponseEntity<Optional<Job>>(job, HttpStatus.NOT_FOUND);
        } else {
        	return new ResponseEntity<Optional<Job>>(job, HttpStatus.OK);
        }
    }

    @PostMapping(path = "/job", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String postJob(@RequestBody Job job) {

        logger.info("Add Job called. Job being added to the repo: " + job.toString());

        if (repo.findById(job.getId()).isPresent())
            new IllegalArgumentException("Job already exists for Id: " + job.getId());

        JobValidator.validateJobDates(job);

        repo.save(job);

        return "New job added";
    }

    @PutMapping(path = "/job", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateJob(@RequestBody Job job) {

        logger.info("Update Job called. Job being updated is : " + job.toString());

        repo.findById(job.getId()).orElseThrow(() -> new IllegalArgumentException("Job not found for Id: " + job.getId()));

        JobValidator.validateJobDates(job);

        repo.save(job); // note - use of same repo method as add.

        return "Job updated";
    }

    @DeleteMapping(path = "/job/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteJob(@PathVariable String id) {

        logger.info("Delete Job called. Job being deleted is : " + id);
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Job not found for id: " + id));
        repo.deleteById(id); // note - use of same repo method as add.

        return "Job deleted";
    }

}