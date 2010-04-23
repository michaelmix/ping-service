package dmitrygusev.ping.services.dao.impl.cache;

import static dmitrygusev.ping.services.dao.impl.cache.CacheHelper.getEntityCacheKey;
import net.sf.jsr107cache.Cache;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.google.appengine.api.datastore.Key;

import dmitrygusev.ping.entities.Job;
import dmitrygusev.ping.entities.Schedule;
import dmitrygusev.ping.services.dao.impl.JobDAOImpl;

public class JobDAOImplCache extends JobDAOImpl {

    @Inject private Cache cache;
    
    @Override
    public void delete(Long scheduleId, Long id) {
        super.delete(scheduleId, id);
        Object entityCacheKey = getEntityCacheKey(Job.class, getJobWideUniqueData(scheduleId, id));
        if (cache.containsKey(entityCacheKey)) {
            cache.remove(entityCacheKey);
        }
        abandonScheduleCache(scheduleId);
    }
    
    @Override
    public Job find(Key jobKey) {
        Object entityCacheKey = getEntityCacheKey(Job.class, getJobWideUniqueData(jobKey));
        if (cache.containsKey(entityCacheKey)) {
            return (Job) cache.get(entityCacheKey);
        }
        Job result = super.find(jobKey);
        if (result != null) {
            cache.put(entityCacheKey, result);
        }
        return result;
    }

    private String getJobWideUniqueData(Key jobKey) {
        return getJobWideUniqueData(jobKey.getParent().getId(), jobKey.getId());
    }

    private String getJobWideUniqueData(Long scheduleId, Long id) {
        return scheduleId + "/" + id;
    }
    
    @Override
    public Job find(Long scheduleId, Long id) {
        Object entityCacheKey = getEntityCacheKey(Job.class, getJobWideUniqueData(scheduleId, id));
        if (cache.containsKey(entityCacheKey)) {
            return (Job) cache.get(entityCacheKey);
        }
        Job result = super.find(scheduleId, id);
        if (result != null) {
            cache.put(entityCacheKey, result);
        }
        return result;
    }
    
    @Override
    public void update(Job job) {
        super.update(job);
        Object entityCacheKey = getEntityCacheKey(Job.class, getJobWideUniqueData(job.getKey()));
        if (cache.containsKey(entityCacheKey)) {
            cache.remove(entityCacheKey);
            cache.put(entityCacheKey, job);
        }
        
        updateJobInScheduleCache(job);
    }

    private void updateJobInScheduleCache(Job job) {
        Long scheduleId = job.getKey().getParent().getId();
        
        Object entityCacheKey = getEntityCacheKey(Schedule.class, scheduleId);
        if (!cache.containsKey(entityCacheKey)) {
            return; //  Nothing to update
        }
        
        Schedule schedule = (Schedule) cache.get(entityCacheKey);
        schedule.updateJob(job);
        
        cache.put(entityCacheKey, schedule);
    }

    private void abandonScheduleCache(Long scheduleId) {
        Object entityCacheKey = getEntityCacheKey(Schedule.class, scheduleId);
        cache.remove(entityCacheKey);
    }
}
