package br.com.alexandre.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.alexandre.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
        if (request.getServletPath().endsWith("/tasks")) {
          var requestTokenHeader = request.getHeader("Authorization");
          var tokenEncoder = requestTokenHeader.substring(6);
          var tokenDecoder = new String(Base64.getDecoder().decode(tokenEncoder));
          var tokenSplit = tokenDecoder.split(":");
          var tokenUserName = tokenSplit[0];
          var tokenPassword = tokenSplit[1];

          var user = this.userRepository.findByUsername(tokenUserName);

          if (user == null) {
            response.sendError(401, "Usuário não autorizado");
            return;
          } else {
            var result = BCrypt.verifyer().verify(tokenPassword.toCharArray(), user.getPassword());

            if (result.verified) {
              request.setAttribute("userId", user.getId());
              filterChain.doFilter(request, response);
            } else {
              response.sendError(401, "Usuário não autorizado");
              return;
            }
          }
        } else {
          filterChain.doFilter(request, response);
        }
  }
  
}
