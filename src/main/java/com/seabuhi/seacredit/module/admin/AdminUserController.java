package com.seabuhi.seacredit.module.admin;

import com.seabuhi.seacredit.common.response.ApiResponse;
import com.seabuhi.seacredit.module.audit.Auditable;
import com.seabuhi.seacredit.module.user.dto.UserDto;
import com.seabuhi.seacredit.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /** Paginated user list with optional search — returns DTOs, never entities */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDto>>> listUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) String search) {

        Page<UserDto> users = adminUserService.listUsers(pageable, search)
                .map(UserMapper::toDto);
        return ResponseEntity.ok(ApiResponse.ok(users, "İstifadəçi siyahısı"));
    }

    /** Get single user detail as DTO */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.ok(UserMapper.toDto(adminUserService.getUser(id)), "İstifadəçi detalları"));
    }

    @Auditable(action = "BLOCK_USER", resource = "User")
    @PatchMapping("/{id}/block")
    public ResponseEntity<ApiResponse<Void>> blockUser(@PathVariable Long id) {
        adminUserService.blockUser(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "İstifadəçi bloklandı"));
    }

    @Auditable(action = "ACTIVATE_USER", resource = "User")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
        adminUserService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "İstifadəçi aktivləşdirildi"));
    }

    @Auditable(action = "CHANGE_ROLE", resource = "User")
    @PatchMapping("/{id}/role")
    public ResponseEntity<ApiResponse<Void>> changeRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        adminUserService.changeRole(id, body.get("role"));
        return ResponseEntity.ok(ApiResponse.ok(null, "Rol dəyişdirildi"));
    }

    @Auditable(action = "DELETE_USER", resource = "User")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "İstifadəçi silindi"));
    }
}


