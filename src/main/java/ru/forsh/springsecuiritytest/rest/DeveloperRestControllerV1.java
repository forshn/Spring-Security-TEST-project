package ru.forsh.springsecuiritytest.rest;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.forsh.springsecuiritytest.model.Developer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/developers")
public class DeveloperRestControllerV1 {
    private List<Developer> developers = Stream.of(
                    new Developer(1L, "Nikolay", "Forsh"),
                    new Developer(2L, "Valeria", "Forsh"),
                    new Developer(3L, "Misha", "Forsh"))
            .collect(Collectors.toList());


    @GetMapping
    public List<Developer> getAll() {
        return developers;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('developers:read')") // Здесь мы прописываем неообходимость перед выполнением метода
    //проверять ауторити у клиента.
    public Developer getById(@PathVariable long id) {
        return developers.stream()
                .filter(dev -> dev.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('developers:write')")
    public Developer create(@RequestBody Developer developer) {
        developers.add(developer);
        return developer;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('developers:write')")
    public void delete(@PathVariable long id) {
        developers.removeIf(d -> d.getId().equals(id));
    }
}
