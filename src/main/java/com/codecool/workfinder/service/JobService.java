package com.codecool.workfinder.service;

import com.codecool.workfinder.repository.EmployerRepository;
import com.codecool.workfinder.repository.JobRepository;
import com.codecool.workfinder.model.dto.JobDto;
import com.codecool.workfinder.model.entity.Job;
import com.codecool.workfinder.model.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobService extends BaseService<Job, JobDto, UUID> {

    @Autowired
    private final EmployerRepository employerRepository;
    @Autowired
    private final ApiFetchService apiFetchService;

    @Autowired
    public JobService(JobRepository jobRepository,
                      JobMapper jobMapper,
                      EmployerRepository employerRepository,
                      ApiFetchService apiFetchService) {

        super(jobRepository, jobMapper);
        this.employerRepository = employerRepository;
        this.apiFetchService = apiFetchService;
    }

    public List<JobDto> getJobListFromApiBy(String apiKey,
                                            String title,
                                            String location) {
        logger.info("");
        title = title == null ? "" : title;
        location = location == null ? "" : location;
        List<Job> jobList = apiFetchService
                .getJobListBy(apiKey, title, location);
        apiFetchService.logger.info("");
        List<JobDto> jobDtoList = mapper.toDtoList(jobList);
        mapper.logInfo("");
        return jobDtoList;
    }

    public Optional<URL> saveAndReturnUrl(Job job) {
        URL url = null;
        if (job.getUrl() == null) {
            String path = "http://localhost:8080/positions?title=" +
                    job.getTitle() + "&location=" + job.getLocation();
            try {
                url = new URL(path);
            } catch (MalformedURLException e) {
                logger.error("Invalid URL path!");
            }
        }
        job.setUrl(url);
        repository.save(job);
        return Optional.ofNullable(url);
    }

    public List<JobDto> listJobsBy(String apiKey, String title, String location) {
        final String titleFilter = (title == null ? "" : title);
        final String locationFilter = (location == null ? "" : location);

        List<JobDto> allJobs = getJobListFromApiBy(apiKey, title, location);
        allJobs.addAll(findAllInRepo().stream()
                .filter(job -> job.getTitle().contains(titleFilter)
                        && job.getLocation().contains(locationFilter))
                .collect(Collectors.toList()));

        return allJobs;
    }

    public JobDto deleteById(UUID id) {
        logger.info("Completed accessing repository '"
                + repository.getClass().getSimpleName() + "'");
        Job job = repository.getById(id);
        repository.deleteById(id);
        JobDto dto = mapper.toDto(job);
        logger.info("Completed coupling to controller: deleteById()");
        return dto;
    }

    public boolean isValidApiKey(String nanoId) {
        return employerRepository.existsById(nanoId);
    }
}