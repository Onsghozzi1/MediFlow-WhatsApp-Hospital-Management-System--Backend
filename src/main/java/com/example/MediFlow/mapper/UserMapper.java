package com.example.MediFlow.mapper;

import com.example.MediFlow.Dtos.user_dto.UserDTO;
import com.example.MediFlow.Dtos.user_dto.UserRegisterDTO;
import com.example.MediFlow.Dtos.user_dto.UserResponse;
import com.example.MediFlow.entity.User;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    // convert User Jpa Entity into UserDTO
    public UserDTO mapToUserDto(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    // Convert UserDTO to User JPA Entity
    public User mapToUser(UserRegisterDTO userRegisterDTO) {
        return modelMapper.map(userRegisterDTO, User.class);
    }
    public UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setPhoneNumber(user.getPhone()); // ✅ Ajoute ceci
        response = modelMapper.map(user, UserResponse.class);
        return response;
    }
}
