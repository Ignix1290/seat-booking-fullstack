package com.samuel.booking;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLOutput;
import java.util.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class BookingServices {

//    private List<Seat> myList = new ArrayList<>(Arrays.asList( new Seat("A1", "available", 500.0),
//            new Seat("A2", "booked", 1000.0),
//            new Seat("A3", "available", 1000.0) ));
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private UserRepository userRepository;

    public String sayHello(@RequestParam(defaultValue = "samuel") String name){
        return "what's up!!! " + name;
    }

//    @GetMapping("/seat/{id}")
//    public Seat getSeat(@PathVariable String id, @RequestParam String status){
//        Seat s = new Seat();
//        s.id = id;
//        if (status.equals("booked") || status.equals("available")) {
//            s.status = status;
//        } else {
//            s.status = "Unknown Status!";
//        }
//        return s;
//    }

    //@GetMapping("/check")
    public String checkVIP(@RequestParam double price){
        if(price > 500 && price <= 1000){
            return "VIP seat";
        }else if(price > 1000 && price <= 2000){
            return "10% dicount on VIP seat";
        }else{
            return "Normal seat";
        }
    }

    public List<Seat> allSeats(){
//        List myList = new ArrayList();
//        myList.add(new Seat("A1", "available", 500.0));
//        myList.add(new Seat("A2", "booked", 1000.0));
//        myList.add(new Seat("A3", "available", 1000.0));
        List<Seat> seats = seatRepository.findAll();
        for(Seat s : seats){
            if(s.getUser() != null){
                s.getUser().setSeat(null);
            }
        }

        return seats;
    }

    public List<Seat> findByStatus(String status){
        return seatRepository.findByStatus(status);
    }

//    @GetMapping("/book")
//    public String bookSeat(@RequestParam String id){
//        for(Seat s : myList){
//            if(s.getId().equalsIgnoreCase(id)){
//                if(s.getStatus().equalsIgnoreCase("booked")){
//                    return "Seat " + id + " already booked";
//                }
//                s.setStatus("booked");
//                return "Seat " + id + " is now booked";
//            }
//        }
//        return "Error : Seat " + id + " doesn't exit";
//    }

//    @GetMapping("/cancel")
//    public String cancelSeat(@RequestParam String id){
//        for(Seat s : myList){
//            if(s.getId().equalsIgnoreCase(id)){
//                if(s.getStatus().equalsIgnoreCase(("available"))){
//                    return "Seat " + id + " is available";
//                }
//                s.setStatus("available");
//                return "Seat " + id + " is now available";
//            }
//        }
//        return "Error : Seat " + id + " doesn't exist";
//    }

    public Seat addSeat(Seat seat){
        if(seat == null){
            System.out.println("New seat created");
            seat = new Seat();
            seat.setStatus("available");
            seat.setPrice(1000.00);
        }

        return seatRepository.save(seat);
    }

    public void deleteSeat(){
        seatRepository.deleteAll();
    }


    @Transactional
    public Seat updateSeat(String id, Seat seatDetails, Long userId){
        Optional<Seat> seatBox = seatRepository.findByIdWithLock(id);
        if(seatBox.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat is empty");
        }

        Seat seatInDb = seatBox.get();
        if(seatInDb.getStatus().equalsIgnoreCase("booked")){
            if(seatInDb.getUser() != null){
                seatInDb.getUser().setSeat(null);
            }
            return seatInDb;
        }

        Optional<User> userBox = userRepository.findById(userId);
        if(userBox.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id is not available");
        }

        User user = userBox.get();

        seatInDb.setStatus(seatDetails.getStatus());
        seatInDb.setUser(user);
        Seat savedSeats = seatRepository.save(seatInDb);
        if(savedSeats.getUser() != null){
            savedSeats.getUser().setSeat(null);
        }
        return savedSeats;
    }

    public void resetAll(){
        seatRepository.resetAllSeats();
    }

//    public double revenue(){
//        double revenue = 0;
//        for(int i = 0; i < myList.size(); i++){
//            if(myList.get(i).getStatus().equalsIgnoreCase("booked")){
//                revenue += myList.get(i).getPrice();
//            }
//        }
//        return revenue;
//    }

    public Seat cancelSeat(String id){
        Optional<Seat> seatBox = seatRepository.findById(id);
        if(seatBox.isPresent()){
            Seat seat = seatBox.get();
            seat.setUser(null);
            seat.setStatus("available");
            seatRepository.save(seat);
            return seat;
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Map<String, Object> getCurrentUser(Long id){
         Optional<User> userBox = userRepository.findById(id);
         User user;

         if(userBox.isPresent()){
             user = userBox.get();
         }else{
             return null;
         }

         Map<String, Object> response = new HashMap<>();
         response.put("id", user.getId());
         response.put("name", user.getName());

        return response;
    }

    public User addUserData(User user){
        Optional<User> userBox = userRepository.findByEmail(user.getEmail());
        if(userBox.isPresent()){
            return userBox.get();
        }else{
            return userRepository.save(user);
        }
    }
}
