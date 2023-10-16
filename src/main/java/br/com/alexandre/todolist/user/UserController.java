package br.com.alexandre.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;

  @PostMapping
  public ResponseEntity<String> create(@RequestBody UserModel userRequest) {
    var userFind = this.userRepository.findByUsername(userRequest.getUsername());

    if (userFind != null) {
      return ResponseEntity.badRequest().body("Usuário já existe!");
    }

    var passwordHash = BCrypt.withDefaults().hashToString(12, userRequest.getPassword().toCharArray());

    userRequest.setPassword(passwordHash);

     this.userRepository.save(userRequest);

     return ResponseEntity.created(null).body("Usuário criado com sucesso!");
  }
}
