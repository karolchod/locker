package com.locker.lockerservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/locker")
@RequiredArgsConstructor
public class LockerServiceController {

    @Autowired
    private LockerService lockerService;

    @GetMapping("/lockers")
    public ResponseEntity<List<Locker>> getLockers() {
        return ResponseEntity.ok().body(lockerService.getLockers());
    }

    @GetMapping("/locker")
    public ResponseEntity<Locker> getLockers(@RequestParam int id) {
        return ResponseEntity.ok().body(lockerService.getLocker(id));
    }

    @PostMapping("/addlocker") //add locker
    public ResponseEntity<?>saveLocker(@RequestBody Locker locker) {
        lockerService.saveLocker(locker);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/editlocker") //edit locker,
    public ResponseEntity<?>editLocker(@RequestBody Locker locker) {
        lockerService.updateLocker(locker);
        return ResponseEntity.ok().build();
    }

}
