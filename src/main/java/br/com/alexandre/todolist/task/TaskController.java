package br.com.alexandre.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alexandre.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping
  public ResponseEntity create(@RequestBody TaskModel taskRequest, HttpServletRequest request) {
    var currentDate = LocalDateTime.now();

    if(currentDate.isAfter(taskRequest.getStartAt()) || currentDate.isAfter(taskRequest.getEndAt())) {
      return ResponseEntity.status(409).body("Data de início não pode ser menor que a data atual");
    }

    if(taskRequest.getStartAt().isAfter(taskRequest.getEndAt())) {
      return ResponseEntity.status(409).body("Data de início não pode ser maior que a data de término");
    }

    var userId = (UUID) request.getAttribute("userId");

    taskRequest.setUserId(userId);

    var taskCreated = this.taskRepository.save(taskRequest);

    return ResponseEntity.created(null).body(taskCreated);
  }

  @GetMapping
  public ResponseEntity read(HttpServletRequest request) {
    var userId = (UUID) request.getAttribute("userId");

    var tasks = this.taskRepository.findByUserId(userId);

    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@RequestBody TaskModel taskRequest, HttpServletRequest request, @PathVariable UUID id) {
    var task = this.taskRepository.findById(id).orElse(null);

    if(task == null) {
      return ResponseEntity.notFound().build();
    }

    if(!task.getUserId().equals(request.getAttribute("userId"))) {
      return ResponseEntity.status(403).build();
    }

    /* var currentDate = LocalDateTime.now();

    if(currentDate.isAfter(taskRequest.getStartAt()) || currentDate.isAfter(taskRequest.getEndAt())) {
      return ResponseEntity.status(409).body("Data de início não pode ser menor que a data atual");
    }

    if(taskRequest.getStartAt().isAfter(taskRequest.getEndAt())) {
      return ResponseEntity.status(409).body("Data de início não pode ser maior que a data de término");
    } */

    Utils.copyNonNullProperties(taskRequest, task);

    var taskUpdated = this.taskRepository.save(task);

    return ResponseEntity.ok(taskUpdated);
  }
}

