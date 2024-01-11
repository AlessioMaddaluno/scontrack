package it.alessiomaddaluno.scontrackbot.service;

import it.alessiomaddaluno.scontrackbot.model.User;
import it.alessiomaddaluno.scontrackbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public User saveOrUpdate(Long chatId, String username){
        User user = new User();
        user.setUsername(username);
        user.setChatId(chatId);
        return this.userRepository.save(user);
    }

}
