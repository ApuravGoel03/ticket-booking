package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Ticket;
import org.example.entities.User;
import org.example.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserBookingService {
    private User user;
    private List<User> userList;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "../localDb/users.json";
    public UserBookingService(User user) throws IOException {
        this.user = user;
        File users = new File(USERS_PATH);
        userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }

    public Boolean loginUser(){
        Optional<User> loggedUser = userList.stream().filter(user1 -> {
            return user1.getUserId().equals(user.getUserId()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();

        return loggedUser.isPresent();
    }

    public Boolean signUpUser(){
        userList.add(user);
        try {
            objectMapper.writeValue(new File(USERS_PATH), userList);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void fetchBooking() {
        user.printTicketsBooked();
    }

    public Boolean cancelBooking(String ticketId){
        try{
            Optional<Ticket> cancelledTicket = user.getTicketsBooked().stream().filter(ticket1 -> {return ticket1.getTicketId().equals(ticketId);}).findFirst();
            if(cancelledTicket.isPresent()){
                userList.remove(user);
                List<Ticket> updatedTickets = user.getTicketsBooked();
                updatedTickets.remove(cancelledTicket);
                user.setTicketsBooked(updatedTickets);
                userList.add(user);
                objectMapper.writeValue(new File(USERS_PATH), userList);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
