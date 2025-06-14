package com.expenses.tracker.service;


import com.expenses.tracker.entity.ExpenseGroup;
import com.expenses.tracker.entity.GroupInvitation;
import com.expenses.tracker.entity.User;
import com.expenses.tracker.repository.ExpenseGroupRepository;
import com.expenses.tracker.repository.GroupInvitationRepository;
import com.expenses.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InvitationService {
    @Value("${frontend.url}")
    String frontendUrl;

    String domain = "http://localhost:8080";

    private final GroupInvitationRepository groupInvitationRepository;
    private final ExpenseGroupRepository groupRepository;
    private final UserRepository userRepository;
    public InvitationService(GroupInvitationRepository groupInvitationRepository, ExpenseGroupRepository groupRepository, UserRepository userRepository) {
        this.groupInvitationRepository = groupInvitationRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public void sendGroupInvitation(Long groupId, String invitedUserEmail) {
        User invitedUser = userRepository.findByEmail(invitedUserEmail);
        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        String token = UUID.randomUUID().toString();
        GroupInvitation invitation = new GroupInvitation();
        invitation.setToken(token);
        invitation.setGroup(group);
        invitation.setInvitedUser(invitedUser);
        invitation.setCreatedAt(LocalDateTime.now());

        groupInvitationRepository.save(invitation);

        String confirmationLink = domain + "/api/group/confirm-invitation?token=" + token;

        String subject = "Group Invitation: " + group.getName();
        String body = "You have been invited to join the group \"" + group.getName() + "\".\n"
                + "Click the link to confirm: " + confirmationLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(invitedUser.getEmail());
        message.setSubject(subject);
        message.setText(body);
//        mailSender.send(message);
    }


    public ResponseEntity<String> confirmGroupInvitation(@RequestParam String token) {
        GroupInvitation invitation = groupInvitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.isAccepted()) {
            return ResponseEntity.ok("You already accepted this invitation.");
        }

        ExpenseGroup group = invitation.getGroup();
        User invitedUser = invitation.getInvitedUser();

        group.getMembers().add(invitedUser);
        invitation.setAccepted(true);

        groupRepository.save(group);
        groupInvitationRepository.save(invitation);

        return ResponseEntity.ok("Successfully joined group: " + group.getName());
    }

}
