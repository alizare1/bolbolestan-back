package com.marshmellow.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marshmellow.Exception.WrongPasswordError;
import com.marshmellow.model.Student;
import com.marshmellow.repository.StudentRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class AuthController {
    public static final String KEY = "bolbolestanbolbolestanbolbolestan";

    @PostMapping("token")
    public JsonNode login(@RequestBody JsonNode body) throws Exception {
        if (!body.has("email") || !body.has("password"))
            throw new Exception("Missing Parameter");

        Student student = StudentRepository.getInstance().findByEmail(body.get("email").asText());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(body.get("password").asText(), student.getPassword()))
            throw new WrongPasswordError();

        String jwt = createToken(student.getStudentId());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resp = objectMapper.createObjectNode();
        resp.put("token", jwt);
        return resp;
    }

    private String createToken(String sid) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date exp = c.getTime();

        SecretKey key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        String jws = Jwts.builder()
                .signWith(key)
                .setHeaderParam("typ", "JWT")
                .setIssuer("bolbolestan.ir")
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .claim("sid", sid)
                .compact();

        return jws;
    }
}
