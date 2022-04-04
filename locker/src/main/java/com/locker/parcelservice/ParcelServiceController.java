package com.locker.parcelservice;

import com.locker.boxservice.Box;
import com.locker.boxservice.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/parcel")
@RequiredArgsConstructor
public class ParcelServiceController {

    @Autowired
    ParcelService parcelService;

    @Autowired
    BoxService boxService;

    @GetMapping("/parcels")
    public ResponseEntity<List<Parcel>> getParcels() {
        return ResponseEntity.ok().body(parcelService.getParcels());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Parcel>> getActiveParcels() {
        return ResponseEntity.ok().body(parcelService.getActiveParcels());
    }

    @GetMapping("/finished")
    public ResponseEntity<List<Parcel>> getFinishedParcels() {
        return ResponseEntity.ok().body(parcelService.getFinishedParcels());
    }

    @GetMapping("/parcel")
    public ResponseEntity<Parcel> getParcelById(@RequestParam int id) {
        return ResponseEntity.ok().body(parcelService.getParcelById(id));
    }

    @GetMapping("/box")
    public ResponseEntity<List<Parcel>> getParcelsByBoxId(@RequestParam int id) {
        return ResponseEntity.ok().body(parcelService.getParcelsByBoxId(id));
    }

    @GetMapping("/from")
    public ResponseEntity<List<Parcel>> getParcelsBySender(@RequestParam int id) {
        return ResponseEntity.ok().body(parcelService.getParcelsBySender(id));
    }

    @GetMapping("/to")
    public ResponseEntity<List<Parcel>> getParcelsByRecipient(@RequestParam int id) {
        return ResponseEntity.ok().body(parcelService.getParcelsByRecipient(id));
    }


    @PostMapping("/create")
    public ResponseEntity<?> saveParcel(@RequestBody Parcel parcel) {
        Box box = boxService.getBox(parcel.getBox_id());
        //check if box is not already used
        if(box.getIsused()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Box is already used");
        }else {
            parcelService.createParcel(parcel);
            box.setIsused(true);
            boxService.updateBox(box);
            return ResponseEntity.ok().build();
        }

    }

    @PostMapping("/update")
    public ResponseEntity<?> updateParcel(@RequestBody Parcel parcel) {
        parcelService.updateParcel(parcel);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/finish")
    public ResponseEntity<?> finishParcel(@RequestParam int id) {
        Parcel parcel = parcelService.getParcelById(id);
        Box box = boxService.getBox(parcel.getBox_id());
        parcel.setIsfinished(true);
        box.setIsused(false);
        parcelService.updateParcel(parcel);
        boxService.updateBox(box);
        return ResponseEntity.ok().build();
    }


}
