package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/time-entries")
public class TimeEntryController {


    private TimeEntryRepository timeEntryRepository;

    @Autowired
    public TimeEntryController(TimeEntryRepository timeEntryRepository) {

        this.timeEntryRepository = timeEntryRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {

        return new ResponseEntity<>(timeEntryRepository.create(timeEntryToCreate), HttpStatus.CREATED);
    }


    @RequestMapping(value = "/time-entries/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<TimeEntry> read(@PathVariable(value = "id") long id) {

         TimeEntry retVal = timeEntryRepository.find(id);
        HttpStatus httpStatus = HttpStatus.OK;

        if (retVal == null) {
            httpStatus = HttpStatus.NOT_FOUND;
        }

        return new ResponseEntity<TimeEntry>(retVal, httpStatus);

    }

    @RequestMapping(value = "/time-entries", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<TimeEntry>> list() {

        return new ResponseEntity<List<TimeEntry>>(timeEntryRepository.list(), HttpStatus.OK);
    }

    @RequestMapping(value = "/time-entries/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity update(@PathVariable(value = "id") long l, @RequestBody TimeEntry expected) {

        TimeEntry retVal = timeEntryRepository.update(l, expected);
        HttpStatus httpStatus = HttpStatus.OK;

        if (retVal == null) {
            // why do we call this?
            httpStatus = HttpStatus.NOT_FOUND;
        }

        return new ResponseEntity<TimeEntry>(retVal, httpStatus);
    }

    @RequestMapping(value = "/time-entries/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity delete(@PathVariable(value = "id") long id) {

        timeEntryRepository.delete(id);

        return new ResponseEntity<TimeEntry>(HttpStatus.NO_CONTENT);
    }
}
