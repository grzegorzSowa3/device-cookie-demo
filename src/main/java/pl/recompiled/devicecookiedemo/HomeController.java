package pl.recompiled.devicecookiedemo;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public ResponseEntity<Void> getHome() {
        return ResponseEntity.ok().build();
    }

}
