package com.gbsolutions.workfinder.controller;

import com.gbsolutions.workfinder.model.dto.JobDto;
import com.gbsolutions.workfinder.model.entity.Job;
import com.gbsolutions.workfinder.service.JobService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/positions")
public class JobController extends BaseController<Job, JobDto, Long, JobService> {

    protected JobController(JobService service) {
        super(service);
    }

    @GetMapping("/{api_key}")
    public List<JobDto> listJobsBy(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @PathVariable("api_key") String apiKey) {
        if (isValidApiKey(apiKey)) {
            return service.listJobsBy(title, location);
        }
        logger.error("Invalid API key!");
        return null;
    }

    @PostMapping("/{api_key}")
    public URL jobRegister(@Valid @RequestBody Job job,
                           @PathVariable("api_key") String apiKey) {
        if (isValidApiKey(apiKey)) {
            return service.saveAndReturnUrl(job).orElse(null);
        }
        logger.error("Invalid API key!");
        return null;
    }

    @DeleteMapping("/{api_key}/{id}")
    public Long deleteById(@PathVariable("api_key") String apiKey,
                           @PathVariable("id") Long id) {
        if (isValidApiKey(apiKey)) {
            return service.deleteById(id);
        }
        logger.error("Invalid API key!");
        return null;
    }

    private boolean isValidApiKey(String apiKey) {
        UUID uuidApiKey = UUID.fromString(apiKey);
        return service.isValidApiKey(uuidApiKey);
    }
}