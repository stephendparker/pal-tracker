package io.pivotal.pal.tracker;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> map = new HashMap<Long, TimeEntry>();

    long currentId = 1L;

    public TimeEntry create(TimeEntry timeEntry) {
        map.put(new Long(Long.valueOf(currentId)), timeEntry);
        timeEntry.setId(currentId);
        currentId = currentId +1;

        return timeEntry;
    }

    public TimeEntry find(long id) {
        return map.get(Long.valueOf(id));
    }

    public List<TimeEntry> list() {
        List<TimeEntry> retVal = new ArrayList<TimeEntry>();
        for (TimeEntry te : map.values()) {
            retVal.add(te);
        }
        return retVal;
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntry.setId(Long.valueOf(id));
        map.put(Long.valueOf(id), timeEntry);
        return map.get(Long.valueOf(id));
    }

    public void delete(long id) {
        map.remove(Long.valueOf(id));
    }
}
