package com.expenses.tracker.contoller;

import com.expenses.tracker.dto.GroupInvitationsDTO;
import com.expenses.tracker.entity.GroupInvitation;
import com.expenses.tracker.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group/invite")
public class InvitationController {

    private final InvitationService invitationService;
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/confirm-invitation")
    public ResponseEntity<String> confirmGroupInvitation(@RequestParam String token) {
        return invitationService.confirmGroupInvitation(token);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<GroupInvitationsDTO>> getAllInvitations() {
        return invitationService.getAllInvitations();
    }

    @DeleteMapping("/decline/{id}")
    public ResponseEntity<String> declineInvitation(@PathVariable Long id) {
        return invitationService.declineInvitation(id);
    }
}
