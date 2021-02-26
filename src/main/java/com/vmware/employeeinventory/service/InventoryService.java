package com.vmware.employeeinventory.service;

import com.vmware.employeeinventory.entity.Employee;
import com.vmware.employeeinventory.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InventoryService {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Autowired
    private EmployeeRepository employeeRepository;

    ConcurrentHashMap<String, String> isTaskCompleteMap = new ConcurrentHashMap();

    public boolean containsTaskEntry(String taskId) {
        if (isTaskCompleteMap.get(taskId) != null) {
            return true;
        }
        return false;
    }

    public String getTaskProgress(String taskId) {
        return isTaskCompleteMap.get(taskId);
    }

    @Async("threadPool")
    public CompletableFuture<?> process(String taskId, byte[] bytes) {

        HashMap<String, String> map = new HashMap<>();
        isTaskCompleteMap.put(taskId, "Processing");
        BufferedReader bfReader;
        List<Employee> employeeList = new ArrayList<>();
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            bfReader = new BufferedReader(new InputStreamReader(is));
            String temp = null;

            int i = 0;
            while ((temp = bfReader.readLine()) != null) {
                String[] emp = temp.split(",");
                employeeList.add(new Employee(emp[0], emp[1]));

                if (i % batchSize == 0 && i > 0) {
                    employeeRepository.saveAll(employeeList);
                    employeeList.clear();
                }
                i++;
            }

            if (employeeList.size() > 0) {
                employeeRepository.saveAll(employeeList);
                employeeList.clear();
            }

        } catch (IOException e) {
            isTaskCompleteMap.put(taskId, "Failed");
            e.printStackTrace();
        }

        isTaskCompleteMap.put(taskId, "Completed");
        map.put("status", "Success");
        log.info("AsyncManager:: completed");
        return CompletableFuture.completedFuture(map);
    }

    public Page<Employee> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public Optional<Employee> findOne(UUID id) {
        return employeeRepository.findById(id);
    }

    public Employee updateEmployee(Employee employee) {

        Optional<Employee> _e = employeeRepository.findById(employee.getId());

        if (_e.isPresent()) {
            Employee _emp = _e.get();
            _emp.setName(employee.getName());
            _emp.setAge(employee.getAge());
            return employeeRepository.save(_emp);
        }
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(UUID id) {
         employeeRepository.deleteById(id);
    }

    public void deleteAll() {
        employeeRepository.deleteAll();
    }
}
