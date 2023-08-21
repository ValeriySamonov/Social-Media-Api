package com.example.social_media_api.service.user;

import com.example.social_media_api.dto.user.CreateUserDTO;
import com.example.social_media_api.exception.UserAlreadyExistsException;
import com.example.social_media_api.model.User;
import com.example.social_media_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public Long createUser(CreateUserDTO createUserDTO) {
        String username = createUserDTO.getUsername();

        // Проверка наличия пользователя с таким именем
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = modelMapper.map(createUserDTO, User.class);
        userRepository.save(user);
        return user.getId();
    }

}
