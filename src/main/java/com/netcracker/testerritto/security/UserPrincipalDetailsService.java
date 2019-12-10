package com.netcracker.testerritto.security;

import com.netcracker.testerritto.dao.UserDAO;
import com.netcracker.testerritto.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalDetailsService implements UserDetailsService {
  private UserDAO userDAO;

  public UserPrincipalDetailsService(@Lazy UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    User user = this.userDAO.getUserByEmail(s);
    UserPrincipal userPrincipal = new UserPrincipal(user);
    return userPrincipal;
  }
}
