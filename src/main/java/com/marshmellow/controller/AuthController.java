package com.marshmellow.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marshmellow.Exception.WrongPasswordError;
import com.marshmellow.model.Student;
import com.marshmellow.repository.StudentRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class AuthController {
    public static final String KEY = "bolbolestanbolbolestanbolbolestan";
    private static final String MAIL_API = "http://138.197.181.131:5200/api/send_mail";

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
        resp.put("sid", student.getStudentId());
        return resp;
    }

    @PostMapping("signup")
    public JsonNode signup(@RequestBody String body) throws Exception {

        StudentRepository repo = StudentRepository.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        Student newStudent = objectMapper.readValue(body, Student.class);
        if (repo.findById(newStudent.getStudentId()) != null) {
            throw new Exception("Student with this id exists");
        }
        if (repo.findByEmail(newStudent.getEmail()) != null) {
            throw new Exception("Student with this email exists");
        }

        StudentRepository.getInstance().insert(newStudent);
        return createSuccessResponse("user was created");
    }

    @PostMapping("resetPassword")
    public JsonNode sendPasswordResetUrl(@RequestBody JsonNode body) throws Exception {
        if (!body.has("email"))
            throw new Exception("Missing Parameter");
        String resetUrl = "127.0.0.1:3000/setNewPassword/" + createPasswordResetToken(body.get("email").asText());
        sendResetPasswordMail(body.get("email").asText(), resetUrl);
        return createSuccessResponse("email sent");
    }

    @PostMapping("resetPassword/{jwt:.+}")
    public JsonNode setNewPassword(@RequestBody JsonNode body, @PathVariable("jwt") String jwt) throws Exception {
        if (!body.has("password"))
            throw new Exception("Missing Parameter");

        SecretKey key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Jws<Claims> jwsClaims;
        String email;
        try {
            jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            if (jwsClaims.getBody().getExpiration().before(new Date()))
                throw new Exception("Token is expired");
            email = jwsClaims.getBody().getSubject();
        } catch (JwtException e) {
            System.out.println(e.getMessage());
            throw new Exception("BAD TOKEN");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pw = encoder.encode(body.get("password").asText());
        StudentRepository.getInstance().updatePassword(email, pw);
        return createSuccessResponse("new password set");
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

    private String createPasswordResetToken(String email) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 10);
        Date exp = c.getTime();

        SecretKey key = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        String jws = Jwts.builder()
                .signWith(key)
                .setHeaderParam("typ", "JWT")
                .setIssuer("bolbolestan.ir")
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .setSubject(email)
                .compact();

        return jws;
    }

    private void sendResetPasswordMail(String email, String resetUrl) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode reqBody = objectMapper.createObjectNode();
        reqBody.put("email", email);
        reqBody.put("url", resetUrl);

        var client = HttpClient.newHttpClient();
        var req = HttpRequest.newBuilder()
                .uri(URI.create(MAIL_API))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reqBody.toString()))
                .build();

        HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode createSuccessResponse(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode resp = objectMapper.createObjectNode();
        resp.put("success", "true");
        resp.put("message", msg);
        return resp;
    }
}
