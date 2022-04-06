package ru.forsh.springsecuiritytest.rest;


import org.springframework.web.bind.annotation.*;
import ru.forsh.springsecuiritytest.model.Developer;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/developers")
public class DeveloperRestControllerV1 {
    private List<Developer> developers = Stream.of(
                    new Developer(1L, "Nikolay", "Forsh"),
                    new Developer(2L, "Valeria", "Forsh"),
                    new Developer(3L, "Misha", "Forsh"))
            .toList();


    @GetMapping
    public List<Developer> getAll() {
        return developers;
    }

    @GetMapping("/{id}")
    public Developer getById(@PathVariable long id) {
        return developers.stream()
                .filter(dev -> dev.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    public Developer create(@RequestBody Developer developer) {
        developers.add(developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        developers.removeIf(d -> d.getId().equals(id));
    }
}
