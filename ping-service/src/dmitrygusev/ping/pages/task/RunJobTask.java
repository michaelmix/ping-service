package dmitrygusev.ping.pages.task;

import static com.google.appengine.api.datastore.KeyFactory.stringToKey;

import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Key;

import dmitrygusev.ping.entities.Job;
import dmitrygusev.ping.services.AppModule;
import dmitrygusev.ping.services.Application;
import dmitrygusev.ping.services.dao.JobDAO;

@Meta(AppModule.NO_MARKUP)
public class RunJobTask {

	private static final Logger logger = LoggerFactory.getLogger(RunJobTask.class);

	public static final String JOB_KEY_PARAMETER_NAME = "job";

	@Inject
	private Request request;
	
	@Inject
	private Application application;
	
	@Inject
	private JobDAO jobDAO;
	
	public void onActivate() {
		String encodedJobKey = request.getParameter(JOB_KEY_PARAMETER_NAME);

        Key key = stringToKey(encodedJobKey);

		try {
            logger.debug("Running job: {}", key.toString());

			Job job = jobDAO.find(key);
		
			if (job != null) {
			    application.runJob(job);
			    
			    application.updateJob(job, false);
			}
		} catch (Exception e) {
			//	Prevent to run job once again on failure
			logger.warn("Error running job", e);
		}
	}
	
}
