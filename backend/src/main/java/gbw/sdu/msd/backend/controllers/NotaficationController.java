package gbw.sdu.msd.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/notifications")
public class NotaficationController {


    @GetMapping(path="/{userId}")
    public @ResponseBody ResponseEntity<List<Object>> getAllNotifications(@PathVariable Integer userId){
        
    }
}
