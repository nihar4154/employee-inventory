package com.vmware.employeeinventory.controller;

import com.vmware.employeeinventory.entity.Employee;
import com.vmware.employeeinventory.service.InventoryService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("/api/employee")
@RestController
public class InventoryController {

    private InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> uploadFile(@RequestParam String action,
                                        @RequestPart("employeeData") MultipartFile file) throws IOException {

        final String taskId = UUID.randomUUID().toString().replace("-", "");
        if (action.equals("upload")) {
            inventoryService.process(taskId, file.getBytes());
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        HashMap<String, String> responseMap = new HashMap<>();
        responseMap.put("taskId",taskId);
        return new ResponseEntity<>(responseMap, HttpStatus.ACCEPTED);
    }



    @RequestMapping(method = RequestMethod.GET, value = "/progress/{taskId}")
    public ResponseEntity<?> getTaskProgress(@PathVariable String taskId) {

        HashMap<String, String> map = new HashMap<>();
        if (!inventoryService.containsTaskEntry(taskId)) {
            map.put("Error", "TaskId does not exist");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
        String taskProgress = inventoryService.getTaskProgress(taskId);
        if (taskProgress.equals("Success")) {
            map.put("status", "Success");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else if (taskProgress.equals("Processing")){
            map.put("status", "Processing");
            return new ResponseEntity<>(map, HttpStatus.PARTIAL_CONTENT);
        } else {
            map.put("status", "Failed");
            return new ResponseEntity<>(map, HttpStatus.I_AM_A_TEAPOT);
        }

    }

    @GetMapping
    public Page<Employee> allEmployee(Pageable pageable) {

        return inventoryService.findAll(pageable);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> employee(@PathVariable UUID id) {

        Optional<Employee> emp  = inventoryService.findOne(id);
        return new ResponseEntity<>(emp, HttpStatus.OK);
    }

    @PutMapping( consumes = MediaType.APPLICATION_JSON_VALUE )
    public Employee update(@RequestBody Employee employee) {
        return inventoryService.updateEmployee(employee);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable UUID id) {
         inventoryService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/all")
    public ResponseEntity<?> deleteAllEmployee() {
        inventoryService.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
