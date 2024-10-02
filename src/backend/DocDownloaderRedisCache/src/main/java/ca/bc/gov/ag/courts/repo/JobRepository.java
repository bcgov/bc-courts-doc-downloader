package ca.bc.gov.ag.courts.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.ag.courts.model.Job;

@Repository
public interface JobRepository extends CrudRepository<Job, String> {}