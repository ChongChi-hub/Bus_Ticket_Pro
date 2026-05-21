package com.rikkei.busticketpro.service;

import com.rikkei.busticketpro.dto.RegisterDTO;
import com.rikkei.busticketpro.entity.Role;
import com.rikkei.busticketpro.entity.Status;
import com.rikkei.busticketpro.entity.User;
import com.rikkei.busticketpro.entity.UserProfile;
import com.rikkei.busticketpro.repository.UserProfileRepository;
import com.rikkei.busticketpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Đăng ký tài khoản mới (role = PASSENGER).
     */
    @Transactional
    public void register(RegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }

        // Tạo user
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.PASSENGER);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        // Tạo profile
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName(dto.getFullName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setEmail(dto.getEmail());
        userProfileRepository.save(profile);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
    }

    public UserProfile getProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ"));
    }

    @Transactional
    public void updateProfile(Long userId, UserProfile updatedProfile) {
        UserProfile profile = getProfile(userId);
        profile.setFullName(updatedProfile.getFullName());
        profile.setPhoneNumber(updatedProfile.getPhoneNumber());
        profile.setEmail(updatedProfile.getEmail());
        profile.setAddress(updatedProfile.getAddress());
        userProfileRepository.save(profile);
    }
}
