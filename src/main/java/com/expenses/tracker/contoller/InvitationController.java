package com.expenses.tracker.contoller;

import com.expenses.tracker.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups/invite")
public class InvitationController {

    private final InvitationService invitationService;
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/confirm-invitation")
    public ResponseEntity<String> confirmGroupInvitation(@RequestParam String token) {
        return invitationService.confirmGroupInvitation(token);
    }
}
