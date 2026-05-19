package com.samuel.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class BookingController {

    @Autowired
    private BookingServices bookingServices;

    @GetMapping("/hello")
    public String sayHello(@RequestParam(defaultValue = "Samuel") String name){
        return bookingServices.sayHello(name);
    }
//    @GetMapping("/seats/revenue")
//    public void revenue(){
//        bookingServices.revenue();
//        System.out.print(bookingServices.revenue());
//    }

    @GetMapping("/seats")
    public ResponseEntity<List<Seat>> allSeats(){
        List<Seat> seat = bookingServices.allSeats();
        return new ResponseEntity<>(seat, HttpStatus.OK);
    }

    @GetMapping("/seats/available-seats")
    public List<Seat> findByStatus(@RequestParam String status){
        return bookingServices.findByStatus(status);
    }

    @PostMapping("/seats/add-seat")
    public ResponseEntity<Seat> addSeat(@RequestBody(required = false) Seat seat){
        Seat newSeat = bookingServices.addSeat(seat);
        return new ResponseEntity<>(newSeat, HttpStatus.CREATED);
    }

    @DeleteMapping("/seats/delete-seats")
    public ResponseEntity<Void> deleteSeat(){
        bookingServices.deleteSeat();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/seats/update-seat/{id}")
    public ResponseEntity<Seat> updateSeat(@PathVariable String id, @RequestBody Seat seat){
        Long userId = seat.getUser().getId();
        seat.setId(id);
        Seat newSeat = bookingServices.updateSeat(id, seat, userId);
        return new ResponseEntity<>(newSeat, HttpStatus.OK);
    }

    @PutMapping("/seats/reset-all")
    public ResponseEntity<String> resetAll(){
        bookingServices.resetAll();
        return new ResponseEntity<>("All the seats are now resetted", HttpStatus.OK);
    }

    @PutMapping("/seats/cancel-seat/{id}")
    public ResponseEntity<Seat> cancelSeat(@PathVariable String id){
        Seat cancelledSeat = bookingServices.cancelSeat(id);
        return new ResponseEntity<>(cancelledSeat, HttpStatus.OK);
    }

    @GetMapping("/seats/user/{id}")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@PathVariable Long id){
        Map<String, Object> currentSeat = bookingServices.getCurrentUser(id);
        return new ResponseEntity<>(currentSeat, HttpStatus.OK);
    }

    @PostMapping("/seats/addUserData")
    public ResponseEntity<User> addUserData(@RequestBody User user){
        User currentUser = bookingServices.addUserData(user);
        return new ResponseEntity<>(currentUser, HttpStatus.CREATED);
    }

}

