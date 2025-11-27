package com.btg.infrastructure.web.user;

import com.btg.core.application.port.in.user.GetUserProfileUseCase;
import com.btg.core.application.port.in.user.UpdateUserProfileUseCase;
import com.btg.infrastructure.web.mapper.UserResponseMapper;
import com.btg.infrastructure.web.user.dto.request.UpdateUserRequest;
import com.btg.infrastructure.web.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final UserResponseMapper userResponseMapper;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        // TODO: userId 받아오기
        Long userId = 1L;

        GetUserProfileUseCase.UserProfileResult result = getUserProfileUseCase.getUserProfile(userId);

        return ResponseEntity.ok(userResponseMapper.toResponse(result));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(@Valid @RequestBody UpdateUserRequest request) {
        // TODO: userId 받아오기
        Long userId = 1L;

        UpdateUserProfileUseCase.UpdateUserProfileCommand command = new UpdateUserProfileUseCase.UpdateUserProfileCommand(
            userId,
            request.name(),
            request.password()
        );

        UpdateUserProfileUseCase.UserProfileResult result = updateUserProfileUseCase.updateUserProfile(command);

        return ResponseEntity.ok(userResponseMapper.toResponse(result));
    }
}
