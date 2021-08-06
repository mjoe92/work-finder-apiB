package com.gbsolutions.workfinder.controller;

import com.gbsolutions.workfinder.model.dto.ClientDto;
import com.gbsolutions.workfinder.model.dto.JobDto;
import com.gbsolutions.workfinder.model.entity.Client;
import com.gbsolutions.workfinder.service.ClientService;
import com.gbsolutions.workfinder.service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/clients")
@Tag(name = "Client service", description = "Signing applicants, employees")
public class ClientController extends BaseController<Client, ClientDto, UUID, ClientService> {

    private final JobService jobService;

    protected ClientController(ClientService clientService, JobService jobService) {
        super(clientService);
        this.jobService = jobService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> findById(@PathVariable String id) {
        logger.info("Start 'GET' request: findById(String)");
        ClientDto clientDto = service.findById(UUID.fromString(id));
        HttpStatus status = clientDto != null ? OK : BAD_REQUEST;
        logger.info("Completed 'GET' request: findById(String)");
        return new ResponseEntity<>(clientDto, status);
    }

    @PostMapping
    public UUID saveAndReturnId(@Valid @RequestBody ClientDto clientDto) {
        logger.info("Start 'POST' request: save(ClientDto)");
        UUID id = service.saveAndReturnId(clientDto);
        logger.info("Completed 'POST' request: save(ClientDto)");
        return id;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        logger.info("Start 'DELETE' request: deleteById(String)");
        service.deleteById(UUID.fromString(id));
        logger.info("Completed 'DELETE' request: deleteById(String)");
    }

    @PutMapping("/{client_id}/job/{job_id}")
    public ResponseEntity<String> registerJobForClient(
            @PathVariable("client_id") String clientId,
            @PathVariable("job_id") Long jobId) {

        logger.info("Start 'PUT' request: registerJobForClient(String, String)");
        ResponseEntity<ClientDto> clientEntity = findById(clientId);
        HttpStatus status = clientEntity.getStatusCode();
        if (status == BAD_REQUEST) {
            return new ResponseEntity<>("No matching client id!", BAD_REQUEST);
        }
        ClientDto clientDto = clientEntity.getBody();

        JobDto jobDto = jobService.findById(jobId);
        if (jobDto == null) {
            return new ResponseEntity<>("No matching job id!", BAD_REQUEST);
        }

        service.registerJobForClient(clientDto, jobDto);

        logger.info("Completed 'PUT' request: registerJobForClient(String, String)");
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}