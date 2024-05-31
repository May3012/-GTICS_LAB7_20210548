package com.example.lab7.Controller;

import com.example.lab7.Entity.Resource;
import com.example.lab7.Entity.User;
import com.example.lab7.Repository.ResourceRepository;
import com.example.lab7.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
public class controller {

    final UserRepository userRepository;
    final ResourceRepository resourceRepository;

    public controller(UserRepository userRepository, ResourceRepository resourceRepository) {
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    // Para listar
    @GetMapping(value = "/{id}")
    public ResponseEntity<HashMap<String, Object>> listarUsuario(@PathVariable("id") Integer id) {
        List<User> listarp = userRepository.listar(id);
        HashMap<String, Object> responseJson = new HashMap<>();

        if (listarp.isEmpty()) {
            responseJson.put("result", "error");
            responseJson.put("message", "Ningún usuario en este servicio");
            return ResponseEntity.badRequest().body(responseJson);
        } else {
            responseJson.put("result", "success");
            responseJson.put("users", listarp);
            return ResponseEntity.ok(responseJson);
        }
    }

    // Para agregar un usuario
    @PostMapping("")
    public ResponseEntity<HashMap<String, Object>> addUser(
            @RequestBody User usuario,
            @RequestParam(value = "fetchId", required = false) boolean fetchId) {

        HashMap<String, Object> responseJson = new HashMap<>();
        Integer authorizedResource = null;

        switch (usuario.getType().toLowerCase()) {
            case "contador":
                authorizedResource = 5;

                break;
            case "cliente":
                authorizedResource = 6;
                break;
            case "analista de promociones":
                authorizedResource = 7;
                break;
            case "analista logístico":
                authorizedResource = 8;
                break;
            default:
                responseJson.put("estado", "error");
                responseJson.put("mensaje", "Tipo de usuario no reconocido");
                return ResponseEntity.badRequest().body(responseJson);
        }

        Optional<Resource> resourceOptional = resourceRepository.findById(authorizedResource);
        if (resourceOptional.isPresent()) {
            usuario.setAuthorizedResource(resourceOptional.get());
        } else {
            responseJson.put("estado", "error");
            responseJson.put("mensaje", "Recurso no encontrado");
            return ResponseEntity.badRequest().body(responseJson);
        }

        userRepository.save(usuario);

        if (fetchId) {
            responseJson.put("id", usuario.getId());
        }
        responseJson.put("estado", "creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseJson);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, String>> gestionException(HttpServletRequest request) {
        HashMap<String, String> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST")) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar un usuario");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }


    //Para borrar
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HashMap<String, Object>> borrarProducto(@PathVariable("id") String idStr) {

        HashMap<String, Object> responseMap = new HashMap<>();

        try {
            int id = Integer.parseInt(idStr);
            Optional<User> optionalPlayer = userRepository.findById(id);
            if (optionalPlayer.isPresent()) {
                userRepository.deleteById(id);
                responseMap.put("estado", "borrado exitoso");
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "no se encontró el producto con id: " + id);
                return ResponseEntity.badRequest().body(responseMap);
            }
        } catch (NumberFormatException ex) {
            responseMap.put("estado", "error");
            responseMap.put("msg", "El ID debe ser un número");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }
    @PutMapping(value = "")
    public ResponseEntity<HashMap<String, Object>> actualizarrr(@RequestBody User user) {
        HashMap<String, Object> responseMap = new HashMap<>();
        if (user.getId() != null && user.getId() > 0) {
            Optional<User> optionalUser = userRepository.findById(user.getId());
            if (optionalUser.isPresent()) {
                User userFromDB = optionalUser.get();
                if (user.getType() != null) {
                    if (user.getType().equals("contador") || user.getType().equals("cliente") || user.getType().equals("analista de promociones") || user.getType().equals("analista logistico")) {
                        userFromDB.setType(user.getType());
                        Integer authorizedResource = null;

                        if (user.getType().equals("contador")) {
                            authorizedResource = 5;
                        } else if (user.getType().equals("cliente")) {
                            authorizedResource = 6;
                        } else if (user.getType().equals("analista de promociones")) {
                            authorizedResource = 7;
                        } else {
                            authorizedResource = 8;
                        }
                        Optional<Resource> resourceOptional = resourceRepository.findById(authorizedResource);
                        if (resourceOptional.isPresent()) {
                            userFromDB.setAuthorizedResource(resourceOptional.get());
                        } else {
                            responseMap.put("estado", "error");
                            responseMap.put("msg", "Coloque un tipo de usuario válido");
                            return ResponseEntity.badRequest().body(responseMap);
                        }
                    }
                } else {
                    responseMap.put("estado", "error");
                    responseMap.put("msg", "Debe enviar un tipo de usuario");
                    return ResponseEntity.badRequest().body(responseMap);
                }

                userRepository.save(userFromDB);
                responseMap.put("estado", "actualizado");
                responseMap.put("usuario", userFromDB);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("estado", "error");
                responseMap.put("msg", "El usuario a actualizar no existe");
                return ResponseEntity.badRequest().body(responseMap);
            }
        } else {
            responseMap.put("estado", "error");
            responseMap.put("msg", "Debe enviar un ID válido");
            return ResponseEntity.badRequest().body(responseMap);
        }
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, String>> gestionExcepcion(HttpServletRequest request) {
        HashMap<String, String> responseMap = new HashMap<>();
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            responseMap.put("estado", "error");
            responseMap.put("mensaje", "Debe enviar un producto");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }


}
