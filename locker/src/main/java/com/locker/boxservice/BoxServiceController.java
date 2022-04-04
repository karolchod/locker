package com.locker.boxservice;

import com.locker.lockerservice.Locker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/api/box")
@RequiredArgsConstructor
public class BoxServiceController {

    @Autowired
    BoxService boxService;

    @GetMapping("/boxes")
    public ResponseEntity<List<Box>> getBoxes() {
        return ResponseEntity.ok().body(boxService.getBoxes());
    }

    @GetMapping("/box")
    public ResponseEntity<Box> getBox(@RequestParam int id) {
        return ResponseEntity.ok().body(boxService.getBox(id));
    }

    @GetMapping("/lockerbybox")
    public ResponseEntity<Locker> getLockerByBox(@RequestParam int id) {
        return ResponseEntity.ok().body(boxService.getLockerByBox(id));
    }

    @GetMapping("/boxesinlocker")
    public ResponseEntity<List<Box>> getBoxesInLocker(@RequestParam int id) {
        return ResponseEntity.ok().body(boxService.getBoxesInLocker(id));
    }

    @GetMapping("/emptyinlocker")
    public ResponseEntity<Integer> getEmptyInLocker(@RequestParam int id) {
        int empty =0;
        List<Box> boxes = boxService.getBoxesInLocker(id);
        for(Box box: boxes){
            if(!box.getIsused())
            empty+=1;
        }
        return ResponseEntity.ok().body(empty);
    }


    @GetMapping("/boxesstatus")
    public ResponseEntity<String> getBoxesStatus(@RequestParam int id) {
        List<Box> boxList = boxService.getBoxesInLocker(id);
        StringBuilder status = new StringBuilder();
        boxList.forEach(
                box -> {
                    if (box.getIsopen()) //isClosed, 1 means box is closed to simplify arduino code
                        status.append(0);
                    else
                        status.append(1);
                }
        );
        return ResponseEntity.ok().body(status.toString());
    }

    @PostMapping("/addbox") //register user, not restricted by security
    public ResponseEntity<?> saveBox(@RequestBody Box box) {
        boxService.saveBox(box);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/editbox") //edit box,
    public ResponseEntity<?> editBox(@RequestBody Box box) {
        boxService.updateBox(box);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/openbox") //edit box,
    public ResponseEntity<?> openBox(@RequestParam int id) {
        Box box = boxService.getBox(id);
        box.setIsopen(true);
        boxService.updateBox(box);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/closebox") //edit box,
    public ResponseEntity<?> closeBox(@RequestParam int id) {
        Box box = boxService.getBox(id);
        box.setIsopen(false);
        boxService.updateBox(box);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/usebox") //edit box,
    public ResponseEntity<?> useBox(@RequestParam int id) {
        Box box = boxService.getBox(id);
        box.setIsused(true);
        boxService.updateBox(box);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/releasebox") //edit box,
    public ResponseEntity<?> releaseBox(@RequestParam int id) {
        Box box = boxService.getBox(id);
        box.setIsused(false);
        boxService.updateBox(box);
        return ResponseEntity.ok().build();
    }

}
