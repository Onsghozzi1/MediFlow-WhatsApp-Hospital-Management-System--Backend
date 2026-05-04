package com.example.MediFlow.services.impl;

import com.example.MediFlow.Dtos.*;
import com.example.MediFlow.Dtos.role_dto.RoleDto;
import com.example.MediFlow.Dtos.user_dto.*;
import com.example.MediFlow.Security.JWTService;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.entity.enums.Roles;
import com.example.MediFlow.exception.AccountNotValidatedException;
import com.example.MediFlow.exception.EmailNotFoundException;
import com.example.MediFlow.exception.UserServiceCustomException;
import com.example.MediFlow.mapper.UserMapper;
import com.example.MediFlow.repository.PasswordResetTokenRepository;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IUserQuery;
import com.example.MediFlow.services.UserService;
import com.example.MediFlow.utility.MyConstants;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AvatarGeneratorServiceImpl avatarGeneratorService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    JWTService jwtService;
    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;
    @Autowired
    private IUserQuery iUserQuery;
    private Random random = new Random();

    // You'll need to add these if using the commented code
    // @Autowired
    // private AvatarGeneratorService avatarGeneratorService;
    //
    // @Autowired
    // private EmailUtility emailUtility;

    @Override
    public UserDTO createUserAccount(UserRegisterDTO userRegisterDTO) throws Exception {

        // Business validation (NOT DTO validation)
        if (userRegisterDTO.getRoleTypes() != Roles.DOCTOR) {
            throw new UserServiceCustomException(
                    "Only DOCTOR role is allowed for registration",
                    "UNSUPPORTED_ROLE_TYPE",
                    HttpStatus.BAD_REQUEST
            );
        }

        return createAccount(userRegisterDTO);
    }
    public UserDTO createAccount(UserRegisterDTO userRegisterDTO) throws Exception {
        if (userRepository.findByEmail(userRegisterDTO.getEmail()).isPresent()) {
            throw new UserServiceCustomException(
                    "Email already exists: " + userRegisterDTO.getEmail(),
                    "EMAIL_ALREADY_EXISTS",
                    HttpStatus.CONFLICT
            );
        }
        User user = userMapper.mapToUser(userRegisterDTO);
        user.setCreatedAt(Instant.now());
        user.setRoleTypes(Roles.DOCTOR);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setIsValidated(false);
        user.setIsDeleted(false);
        user.setRoleTypes(userRegisterDTO.getRoleTypes());
        user.setTokenToValidate(generateOTPToSend());
        user.setFirstTimeLogin(true);
        user.setPhone(userRegisterDTO.getPhoneNumber());
        user.setValidateCodeCreationDate(LocalDateTime.now());

        // ✅ 1. If profile picture exists → use it
        if (userRegisterDTO.getProfilePicture() != null
                && userRegisterDTO.getProfilePicture().length > 0) {

            user.setProfilePicture(userRegisterDTO.getProfilePicture());

        } else {
            // ✅ 2. Else → generate avatar
            try {
                byte[] avatar = avatarGeneratorService.generateAvatar(
                        userRegisterDTO.getFirstName(),
                        userRegisterDTO.getLastName()
                );
                user.setProfilePicture(avatar);

            } catch (Exception e) {
                System.err.println("Failed to generate avatar: " + e.getMessage());
            }
        }

      //  sendMail(user,userRegisterDTO.getPassword(),user.getTokenToValidate());

        // ✅ 3. Save user
        User userSave = userRepository.save(user);

        return userMapper.mapToUserDto(userSave);
    }
    public void sendMail(User user,String password,Long tokenToValidate) throws Exception {
        String[] lcc = new String[2];
        if (user != null) {
            lcc[0] = user.getEmail();
            String cc = user.getEmail();
            runSend(user.getFirstName() +' '+user.getLastName(),password,tokenToValidate, cc,user.getEmail(), "inscription", "logo.png", " Bienvenue et voici vos accès !", lcc);
        }
    }
    public void runSend(String username, String password, Long tokenToValidate, String link, String to, String template, String im, String subject, String[] lcc) {
        MailDto mail = new MailDto();
        String from = "";// "project.mailhs@gmail.com";
        from = MyConstants.MY_EMAIL;
        mail.setFrom(from);// replace with your desired email
        mail.setMailTo(to);// replace with your desired email
        mail.setSubject(subject);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("email", to);
        model.put("password", password);
        model.put("token", tokenToValidate);

        mail.setProps(model);

        try {
            emailService.sendEmail(mail, template, im);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generateSd catch block
            e.printStackTrace();
        }

    }


    public UserDTO createAccountforAllsRoles(UserRegisterDTO userRegisterDTO) throws MessagingException {

        User user = userMapper.mapToUser(userRegisterDTO);

        user.setCreatedAt(Instant.now());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setIsValidated(false);
        user.setIsDeleted(false);
        user.setRoleTypes(userRegisterDTO.getRoleTypes());
        user.setTokenToValidate(generateOTPToSend());
        user.setFirstTimeLogin(true);
        user.setValidateCodeCreationDate(LocalDateTime.now());

        // ✅ 1. If profile picture exists → use it
        if (userRegisterDTO.getProfilePicture() != null
                && userRegisterDTO.getProfilePicture().length > 0) {

            user.setProfilePicture(userRegisterDTO.getProfilePicture());

        } else {
            // ✅ 2. Else → generate avatar
            try {
                byte[] avatar = avatarGeneratorService.generateAvatar(
                        userRegisterDTO.getFirstName(),
                        userRegisterDTO.getLastName()
                );
                user.setProfilePicture(avatar);

            } catch (Exception e) {
                System.err.println("Failed to generate avatar: " + e.getMessage());
            }
        }

        // ✅ 3. Save user
        User userSave = userRepository.save(user);

        return userMapper.mapToUserDto(userSave);
    }

    private Long generateOTPToSend() {
        int min = 10000;
        int max = 99999;
        return Long.valueOf(this.random.nextInt(max - min + 1) + min);
    }
    @Override
    public JwtResponse login(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserServiceCustomException("No user found with this email:" + email, "User_Not_Found"));

        if (!user.getIsValidated()) {
            throw new UserServiceCustomException("You need to validate your account first and check your email",
                    "ACCOUNT_NOT_VALID", HttpStatus.FORBIDDEN);
        }

        String token = jwtService.generateToken(email);
        UserResponse userResponse = userMapper.mapToUserResponse(user);
        user.setFirstTimeLogin(true);
        user.setEmail(email);
        userRepository.save(user);

        // Generate avatar URL
        String avatarUrl = user.getProfilePicture() != null
                ? "/api/users/" + user.getId() + "/avatar"
                : null;
        return new JwtResponse(userResponse, token, avatarUrl);
    }

    /**
     * Fetches paginated user data for administration purposes.
     * This method serves as a facade to the underlying query service,
     * applying pagination, sorting, and filtering criteria.
     *
     * @param pageNo   The page number to retrieve (zero-indexed or one-indexed based on implementation)
     * @param pageSize Number of records per page
     * @param sortBy   Field name to sort results by
     * @param sortDir  Sort direction: "asc" for ascending, "desc" for descending
     * @param filter   Filter criteria to apply to user results (null for no filtering)
     * @return AdminResponseDto containing paginated user data and metadata
     */
    @Override
    public AdminResponseDto getUserPagination(int pageNo, int pageSize, String sortBy, String sortDir, AdminFilter filter) {
        return iUserQuery.getAdminPagination( pageNo, pageSize, sortBy, sortDir, filter);
    }
    /**
     * Updates a user's role
     *
     * @param roles New role to assign (ADMIN/USER)
     * @param userId ID of user to update
     * @return User ID and new role if found, 404 if user doesn't exist
     */
    public ResponseEntity<RoleDto> changeUserRole(Roles roles, Long userId) {
        // Find user by ID
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            // Update user's role
            User existingUser = user.get();
            existingUser.setRoleTypes(roles);
            userRepository.save(existingUser);

            // Return updated user info
            RoleDto roleDto = new RoleDto(existingUser.getId(), existingUser.getRoleTypes());
            return ResponseEntity.ok(roleDto);
        }

        // User not found
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<User_validate_Dto> changeAccountStatus(
            Boolean status,
            Long userId
    ) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User existingUser = user.get();
            existingUser.setIsValidated(status);
            userRepository.save(existingUser);

            User_validate_Dto responseDto = new User_validate_Dto(existingUser.getIsValidated());

            return ResponseEntity.ok(responseDto);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @Override
    public UserUpdateResponseDto updateUserByEmail(UserUpdateResponseDto userDTO) throws IOException {
        String email = userDTO.getEmail();

        // Validate email
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank.");
        }

        // Fetch User safely
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserServiceCustomException(
                        "No user found with email: " + email,
                        "USER_NOT_FOUND"
                ));

        // Update phone if present
        if (userDTO.getPhone() != null && !userDTO.getPhone().isBlank()) {
            user.setPhone(userDTO.getPhone());
        }

        // Update firstName if present
        if (userDTO.getFirstName() != null && !userDTO.getFirstName().isBlank()) {
            user.setFirstName(userDTO.getFirstName());
        }

        // Update lastName if present
        if (userDTO.getLastName() != null && !userDTO.getLastName().isBlank()) {
            user.setLastName(userDTO.getLastName());
        }

        // Update roleTypes if present
        if (userDTO.getRoleTypes() != null) {
            user.setRoleTypes(Roles.valueOf(userDTO.getRoleTypes()));
        }

        // Handle base64 profile picture safely
        String base64Data = userDTO.getProfilePicture();
        if (base64Data != null && !base64Data.isBlank()) {
            base64Data = base64Data.replaceAll("\\s+", "").trim();

            if (base64Data.startsWith("data:image")) {
                base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            System.out.println("Decoded image size: " + imageBytes.length + " bytes");

            user.setProfilePicture(imageBytes);
        }

        // Set updated timestamp
        user.setUpdatedAt(Instant.now());

        // Save the user entity
        User savedUser = userRepository.save(user);

        // Map to DTO for clean JSON response
        return mapToDto(savedUser);
    }

    @Override
    public ResponseEmail findByEmail(String email) {
        Long id = userRepository.findUserIdByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ResponseEmail(id);
    }

    @Override
    public void change_password(Password_dto passwordDto, String email) throws MessagingException {
        // 1️⃣ Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        // 2️⃣ Validate new password (optional)
        if (!passwordDto.getNew_password().equals(passwordDto.getConfirm_password())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // 3️⃣ Encode and update password
        user.setPassword(passwordEncoder.encode(passwordDto.getNew_password()));
        userRepository.save(user);

    }


    private UserUpdateResponseDto mapToDto(User user) {
        UserUpdateResponseDto dto = new UserUpdateResponseDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRoleTypes(String.valueOf(user.getRoleTypes()));

        if (user.getProfilePicture() != null) {
            String base64 = Base64.getEncoder().encodeToString(user.getProfilePicture());
            dto.setProfilePicture("data:image/png;base64," + base64);
        }
        return dto;
    }
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%!";

        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
    public void sendWelcomeMail(User user, String password) {
        if (user == null || user.getEmail() == null) return;

        MailDto mail = new MailDto();
        mail.setFrom(MyConstants.MY_EMAIL);
        mail.setMailTo(user.getEmail());
        mail.setSubject("Bienvenue et voici vos accès !");

        Map<String, Object> model = new HashMap<>();
        model.put("email", user.getEmail());
        model.put("password", password);

        mail.setProps(model);

        try {
            emailService.sendEmail(mail, "forgot_password", "logo.png");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    public void requestPasswordReset(String email) throws Exception {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Email not found"));

        // ❌ إذا الحساب غير مفعل
        if (!user.getIsValidated()) {
            throw new AccountNotValidatedException("Account is not validated");
        }

        // 🔐 Generate password
        String newPassword = generateRandomPassword(10);

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // 📧 Send email
        MailDto mail = new MailDto();
        mail.setFrom(MyConstants.MY_EMAIL);
        mail.setMailTo(user.getEmail());
        mail.setSubject("Nouveau mot de passe");

        Map<String, Object> model = new HashMap<>();
        model.put("name", user.getFirstName());
        model.put("email", user.getEmail());
        model.put("password",newPassword);

        mail.setProps(model);

        try {
            emailService.sendEmail(mail, "forgot_password", "logo.png");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}
