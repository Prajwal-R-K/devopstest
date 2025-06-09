package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@SessionAttributes("adminAuthGroups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private GiftRequestRepository giftRequestRepository;
    @Autowired
    private SecretSantaService secretSantaService;

    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Group creation - step 1
    @GetMapping("/create")
    public String createGroupForm() {
        return "createGroup";
    }

    @PostMapping("/create/basics")
    public String createGroupBasics(@RequestParam String groupName, @RequestParam int maxParticipants,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("groupName", groupName);
        redirectAttributes.addFlashAttribute("maxParticipants", maxParticipants);
        return "redirect:/create/rules-dates";
    }

    // Group creation - step 2
    @GetMapping("/create/rules-dates")
    public String createGroupRulesDates(Model model, @ModelAttribute("groupName") String groupName,
            @ModelAttribute("maxParticipants") Integer maxParticipants) {
        model.addAttribute("groupName", groupName);
        model.addAttribute("maxParticipants", maxParticipants);
        return "createGroupRulesDates";
    }

    @PostMapping("/create/complete")
    public String createGroupComplete(
            @RequestParam("groupName") String groupName,
            @RequestParam("maxParticipants") int maxParticipants,
            @RequestParam String adminPassword,
            @RequestParam double minBudget,
            @RequestParam double maxBudget,
            @RequestParam String exchangeDate,
            @RequestParam String assignGiftDate,
            @RequestParam(required = false) String revealDate,
            RedirectAttributes redirectAttributes) {

        String groupKey = java.util.UUID.randomUUID().toString();

        Group newGroup = new Group();
        newGroup.setGroupId(groupKey);
        newGroup.setGroupName(groupName);
        newGroup.setMaxParticipants(maxParticipants);
        newGroup.setMinBudget(minBudget);
        newGroup.setMaxBudget(maxBudget);
        newGroup.setAdminPassword(adminPassword);

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime exDate = LocalDateTime.parse(exchangeDate, formatter);
            newGroup.setExchangeDate(exDate);
            if (revealDate != null && !revealDate.isEmpty()) {
                LocalDateTime revDate = LocalDateTime.parse(revealDate, formatter);
                newGroup.setRevealDate(revDate);
            }
            if (assignGiftDate != null && !assignGiftDate.isEmpty()) {
                LocalDateTime assignDate = LocalDateTime.parse(assignGiftDate, formatter);
                newGroup.setAssignGiftDate(assignDate);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format.");
            return "redirect:/create";
        }

        groupRepository.save(newGroup);

        redirectAttributes.addFlashAttribute("groupKey", groupKey);
        return "redirect:/group/" + groupKey + "/dashboard";
    }

    @GetMapping("/createGroupConfirmation")
    public String createGroupConfirmation(Model model, @ModelAttribute("groupKey") String groupKey,
            @ModelAttribute("joinUrl") String joinUrl) {
        model.addAttribute("groupKey", groupKey);
        model.addAttribute("joinUrl", joinUrl);
        return "createGroupConfirmation";
    }

    // Join group - enter group key
    @GetMapping("/join")
    public String joinGroup(@RequestParam String groupKey, Model model) {
        Optional<Group> groupOpt = groupRepository.findById(groupKey);
        if (!groupOpt.isPresent()) {
            model.addAttribute("error", "Group not found.");
            return "index";
        }
        model.addAttribute("groupKey", groupKey);
        return "joinGroup";
    }

    @PostMapping("/join/confirm")
    public String confirmGroupKey(@RequestParam String groupKey, RedirectAttributes redirectAttributes) {
        Optional<Group> group = groupRepository.findById(groupKey);
        if (group.isPresent()) {
            redirectAttributes.addFlashAttribute("groupKey", groupKey);
            return "redirect:/join/createUser";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid group key.");
            return "redirect:/join";
        }
    }

    // New user registration
    @GetMapping("/join/createUser")
    public String createUserForm(@ModelAttribute("groupKey") String groupKey, Model model) {
        model.addAttribute("groupKey", groupKey);
        return "createUser";
    }

    @PostMapping("/join/createUser")
    public String createUser(
            @RequestParam String groupKey,
            @RequestParam String name,
            RedirectAttributes redirectAttributes) {

        Optional<Group> groupOpt = groupRepository.findById(groupKey);
        if (groupOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Group not found.");
            return "redirect:/join?groupKey=" + groupKey;
        }

        Group group = groupOpt.get();

        Participant participant = new Participant();
        participant.setParticipantId(generateUserId(name)); // <-- Set the ID here!
        participant.setName(name);
        participant.setGroup(group);
        participantRepository.save(participant);

        return "redirect:/join/userHome?userId=" + participant.getParticipantId() + "&groupKey=" + groupKey;
    }

    // Existing user login
    @PostMapping("/join/existing")
    public String joinGroupExistingParticipant(@RequestParam String groupKey, @RequestParam String userId,
            RedirectAttributes redirectAttributes) {
        Optional<Group> groupOptional = groupRepository.findById(groupKey);
        if (!groupOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Invalid group key.");
            return "redirect:/join";
        }
        Group group = groupOptional.get();
        Optional<Participant> participantOptional = group.getParticipants().stream()
                .filter(p -> p.getParticipantId().equals(userId))
                .findFirst();
        if (!participantOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Invalid User-ID for this group.");
            return "redirect:/join?groupKey=" + groupKey;
        }
        // Pass as URL parameters:
        return "redirect:/join/userHome?userId=" + userId + "&groupKey=" + groupKey;
    }

    // User home page
    @GetMapping("/join/userHome")
    public String userHome(
            @RequestParam("userId") String userId,
            @RequestParam("groupKey") String groupKey,
            Model model) {

        model.addAttribute("userId", userId);
        model.addAttribute("groupKey", groupKey);

        // Fetch participant by userId and groupKey
        Optional<Participant> participantOpt = participantRepository.findById(userId);
        if (participantOpt.isPresent()) {
            Participant participant = participantOpt.get();
            // Optionally check participant.getGroup().getGroupId().equals(groupKey)
            model.addAttribute("userName", participant.getName());

            // Fetch all gift requests for this participant
            List<GiftRequest> giftRequests = giftRequestRepository.findByParticipant(participant);
            model.addAttribute("giftRequests", giftRequests);
        } else {
            model.addAttribute("error", "User not found.");
        }

        return "userHome";
    }

    // Group dashboard (for creator)
    // Helper to check admin authentication for a group
    @SuppressWarnings("unchecked")
    private boolean isAdminAuthenticated(HttpSession session, String groupKey) {
        Map<String, Boolean> adminAuthGroups = (Map<String, Boolean>) session.getAttribute("adminAuthGroups");
        return adminAuthGroups != null && Boolean.TRUE.equals(adminAuthGroups.get(groupKey));
    }

    @GetMapping("/group/{groupKey}/dashboard")
    public String groupDashboard(@PathVariable String groupKey, Model model, HttpSession session) {
        Optional<Group> groupOptional = groupRepository.findById(groupKey);
        if (!groupOptional.isPresent()) {
            return "redirect:/";
        }
        Group group = groupOptional.get();
        model.addAttribute("group", group);
        boolean adminAuthenticated = isAdminAuthenticated(session, groupKey);
        model.addAttribute("adminAuthenticated", adminAuthenticated);
        if (adminAuthenticated) {
            model.addAttribute("participants", group.getParticipants());
        }
        return "groupDashboard";
    }

    @PostMapping("/group/{groupKey}/dashboard-auth")
    public String dashboardAuth(@PathVariable String groupKey,
            @RequestParam String adminPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<Group> groupOptional = groupRepository.findById(groupKey);
        if (groupOptional.isPresent() && groupOptional.get().getAdminPassword().equals(adminPassword)) {
            setAdminAuthenticated(session, groupKey, true);
        } else {
            redirectAttributes.addFlashAttribute("error", "Incorrect admin password.");
        }
        return "redirect:/group/" + groupKey + "/dashboard";
    }

    // Gift request page
    @GetMapping("/giftRequest")
    public String giftRequest(@RequestParam String userId, Model model) {
        Optional<Participant> participantOpt = participantRepository.findById(userId);
        if (!participantOpt.isPresent()) {
            model.addAttribute("error", "Invalid user.");
            return "userHome";
        }
        Participant participant = participantOpt.get();
        Group group = participant.getGroup();

        // Check if assignments are done or deadline has passed
        if (group.isAssignmentsDone() ||
                (group.getAssignGiftDate() != null && LocalDateTime.now().isAfter(group.getAssignGiftDate()))) {
            model.addAttribute("error", "Gift requesting is closed for this group.");
            return "userHome";
        }
        model.addAttribute("userId", userId);
        model.addAttribute("group", group);
        return "giftRequest";
    }

    @PostMapping("/giftRequest")
    public String submitGiftRequest(@RequestParam String userId,
            @RequestParam String giftName,
            @RequestParam String photoUrl,
            @RequestParam double approxBudget,
            @RequestParam String notes,
            @RequestParam String deliveryLocationTime,
            RedirectAttributes redirectAttributes) {
        Optional<Participant> participantOpt = participantRepository.findById(userId);
        if (!participantOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Invalid user.");
            return "redirect:/";
        }
        Participant participant = participantOpt.get();
        Group group = participant.getGroup();

        // Check if assignments are done or deadline has passed
        if (group.isAssignmentsDone() ||
                (group.getAssignGiftDate() != null && LocalDateTime.now().isAfter(group.getAssignGiftDate()))) {
            redirectAttributes.addFlashAttribute("error", "Gift requesting is closed for this group.");
            return "redirect:/join/userHome?userId=" + userId + "&groupKey=" + group.getGroupId();
        }

        GiftRequest request = new GiftRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setGiftName(giftName);
        request.setPhotoUrl(photoUrl);
        request.setApproxBudget(approxBudget);
        request.setNotes(notes);
        request.setDeliveryLocationTime(deliveryLocationTime);
        request.setParticipant(participant);
        giftRequestRepository.save(request);

        redirectAttributes.addFlashAttribute("success", "Gift request submitted!");
        return "redirect:/join/userHome?userId=" + userId + "&groupKey=" + participant.getGroup().getGroupId();
    }

    // Gift selection page
    @GetMapping("/giftSelection")
    public String giftSelection(@RequestParam String userId, Model model) {
        Optional<Participant> participantOpt = participantRepository.findById(userId);
        if (!participantOpt.isPresent()) {
            model.addAttribute("error", "Invalid user.");
            return "userHome";
        }
        Participant participant = participantOpt.get();
        Group group = participant.getGroup();
        String gifteeId = group.getAssignments().get(participant.getParticipantId());
        List<GiftRequest> gifts = new ArrayList<>();
        GiftRequest selectedGift = null;
        if (gifteeId != null) {
            Participant giftee = participantRepository.findById(gifteeId).orElse(null);
            if (giftee != null) {
                gifts = giftRequestRepository.findByParticipant(giftee);
                if (participant.getSelectedGiftId() != null) {
                    for (GiftRequest gift : gifts) {
                        if (gift.getRequestId().equals(participant.getSelectedGiftId())) {
                            selectedGift = gift;
                            break;
                        }
                    }
                }
            }
        }
        model.addAttribute("userId", userId);
        model.addAttribute("gifts", gifts);
        model.addAttribute("selectedGift", selectedGift);
        return "giftSelection";
    }

    @PostMapping("/giftSelection/accept")
    public String acceptGift(@RequestParam String userId, @RequestParam String requestId,
            RedirectAttributes redirectAttributes) {
        Optional<GiftRequest> giftOpt = giftRequestRepository.findById(requestId);
        if (!giftOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Gift not found.");
            return "redirect:/giftSelection?userId=" + userId;
        }
        GiftRequest selectedGift = giftOpt.get();
        if (selectedGift.isClaimed()) {
            redirectAttributes.addFlashAttribute("error", "Gift already claimed.");
            return "redirect:/giftSelection?userId=" + userId;
        }

        // Mark all gifts for this giftee as claimed except the selected one
        Participant giftee = selectedGift.getParticipant();
        List<GiftRequest> allGifts = giftRequestRepository.findByParticipant(giftee);
        for (GiftRequest gift : allGifts) {
            if (!gift.getRequestId().equals(requestId)) {
                gift.setClaimed(true);
                giftRequestRepository.save(gift);
            }
        }
        // Mark selected gift as claimed
        selectedGift.setClaimed(true);
        giftRequestRepository.save(selectedGift);

        // Set selectedGiftId for the selector
        Participant selector = participantRepository.findById(userId).orElse(null);
        if (selector != null) {
            selector.setSelectedGiftId(requestId);
            participantRepository.save(selector);
        }

        redirectAttributes.addFlashAttribute("success", "Gift accepted!");
        return "redirect:/join/userHome?userId=" + userId + "&groupKey=" + giftee.getGroup().getGroupId();
    }

    // Assign Secret Santa (creator only)
    @PostMapping("/group/{groupKey}/assign-santa")
    public String assignSecretSanta(@PathVariable String groupKey,
            @RequestParam String adminPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<Group> groupOptional = groupRepository.findById(groupKey);
        if (!groupOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Group not found.");
            return "redirect:/";
        }
        Group group = groupOptional.get();
        if (!group.getAdminPassword().equals(adminPassword)) {
            redirectAttributes.addFlashAttribute("error", "Incorrect admin password.");
            return "redirect:/group/" + groupKey + "/dashboard";
        }
        if (group.isAssignmentsDone())

        {
            redirectAttributes.addFlashAttribute("error", "Assignments already done.");
            return "redirect:/group/" + groupKey + "/dashboard";
        }
        List<Participant> participants = group.getParticipants();
        if (participants.size() < 2) {
            redirectAttributes.addFlashAttribute("error", "Not enough participants to assign Secret Santa.");
            return "redirect:/group/" + groupKey + "/dashboard";
        }
        try {
            Map<Participant, Participant> assignments = secretSantaService.assignGiftees(participants);
            Map<String, String> assignmentIds = new HashMap<>();
            for (Map.Entry<Participant, Participant> entry : assignments.entrySet()) {
                assignmentIds.put(entry.getKey().getParticipantId(), entry.getValue().getParticipantId());
            }
            group.setAssignments(assignmentIds);
            group.setAssignmentsDone(true);
            groupRepository.save(group);
            redirectAttributes.addFlashAttribute("success", "Secret Santa assigned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during Secret Santa assignment: " + e.getMessage());
        }
        return "redirect:/group/" + groupKey + "/dashboard";
    }

    // Optionally, clear admin authentication when leaving dashboard or logging out
    @GetMapping("/logout-admin/{groupKey}")
    public String logoutAdmin(@PathVariable String groupKey, HttpSession session) {
        Map<String, Boolean> adminAuthGroups = (Map<String, Boolean>) session.getAttribute("adminAuthGroups");
        if (adminAuthGroups != null) {
            adminAuthGroups.remove(groupKey);
            session.setAttribute("adminAuthGroups", adminAuthGroups);
        }
        return "redirect:/group/" + groupKey + "/dashboard";
    }

    // Helper: generate user ID
    private String generateUserId(String name) {
        // Generates a user ID like: NAME_RANDOM4DIGITS
        String prefix = name.replaceAll("\\s+", "").toUpperCase();
        String suffix = String.valueOf((int) (Math.random() * 9000) + 1000); // 4 random digits
        return prefix + "_" + suffix;
    }

    @SuppressWarnings("unchecked")
    private void setAdminAuthenticated(HttpSession session, String groupId, boolean authenticated) {
        if (session != null) {
            Map<String, Boolean> adminAuthGroups = (Map<String, Boolean>) session.getAttribute("adminAuthGroups");
            if (adminAuthGroups == null) {
                adminAuthGroups = new HashMap<>();
            }
            adminAuthGroups.put(groupId, authenticated);
            session.setAttribute("adminAuthGroups", adminAuthGroups);
        }
    }
}
