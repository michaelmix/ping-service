package dmitrygusev.ping.services.dao;

import java.util.List;

import org.tynamo.jpa.annotations.CommitAfter;

import dmitrygusev.ping.entities.Job;
import dmitrygusev.ping.entities.JobResult;

public interface JobResultDAO {
	@CommitAfter
	public void persistResult(JobResult result);
    @CommitAfter
	public List<JobResult> getResults(Job job, int maxResults);
	@CommitAfter
	public void delete(Long id, Integer timeout);
	@CommitAfter
    public List<JobResult> getAll();
}
