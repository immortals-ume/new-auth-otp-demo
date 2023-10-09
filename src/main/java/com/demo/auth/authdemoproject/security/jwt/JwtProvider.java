package com.demo.auth.authdemoproject.security.jwt;


import com.demo.auth.authdemoproject.service.UserPrincipal;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;

import static com.demo.auth.authdemoproject.util.DateUtils.getLocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements Serializable {

    @Value("${auth.privateKeyFile}")
    private String privateKeyFilePath;

    @Value("${auth.publicKeyFile}")
    private String publicKeyFilePath;

    @Value("${auth.key-location}")
    private String keyLocation;

    @Value("${app.expiry-time}")
    private static int jwtExpirationMs;

    public static Date getExpirationTime() {
        return (new Date((new Date()).getTime() + jwtExpirationMs));
    }

    private static JWTClaimsSet getJwtClaimsSet(UserPrincipal userPrincipal) {
        return new JWTClaimsSet.Builder().subject(userPrincipal.getUsername()).claim("auth",userPrincipal.getAuthorities()).expirationTime(getExpirationTime()).issuer("system").issueTime(getLocalDate()).build();
    }

    public static RSAPrivateKey readPrivateKey(Path filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(filePath);

        String key = new String(keyBytes, StandardCharsets.UTF_8);

        String privateKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "").replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public static RSAPublicKey readPublicKey(Path filePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(filePath);

        String key = new String(keyBytes, StandardCharsets.UTF_8);


        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public boolean isSignatureValid(String token) {
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(readPublicKey(getFilePath(publicKeyFilePath)));
            return signedJWT.verify(verifier);
        } catch (ParseException | JOSEException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Couldn't verify signature: " + e.getMessage());
            return Boolean.FALSE;
        }
    }

    public String parseJwtGetUserName(String jwt) {
        String payload = "";
        try {
            var decodedJWT = SignedJWT.parse(decryptSignedJwt(jwt));
            payload = decodedJWT.getPayload().toString();
        } catch (Exception e) {
            log.error("Jwt Token Is Not Valid " + e.getMessage());
            throw new com.demo.auth.authdemoproject.service.exception.ParseException("Jwt Token Is Not Valid {}" + e.getMessage());
        }
        return payload;
    }

    public String generateJwtToken(Authentication authentication) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return encryptSignedJwt(generateJwtFromUserNameOrEmail(userPrincipal));

    }

    private String encryptSignedJwt(String signedJwt) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.RSA_OAEP_256,
                EncryptionMethod.A128GCM);
        JWEObject jweObject = new JWEObject(jweHeader, new Payload(signedJwt));
        jweObject.encrypt(new RSAEncrypter(readPublicKey(getFilePath(publicKeyFilePath))));
        return jweObject.serialize();
    }

    private String decryptSignedJwt(String signedEncryptedJwt) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, ParseException, JOSEException {
        JWEObject jweObject = JWEObject.parse(signedEncryptedJwt);
        jweObject.decrypt(new RSADecrypter(readPrivateKey(getFilePath(privateKeyFilePath))));
        return jweObject.getPayload().toString();
    }

    private String generateJwtFromUserNameOrEmail(UserPrincipal userPrincipal) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {

        JWSSigner signer = new RSASSASigner(readPrivateKey(getFilePath(privateKeyFilePath)));

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), getJwtClaimsSet(userPrincipal));

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private Path getFilePath(String privateKeyFilePath) {
        return Paths.get(keyLocation, privateKeyFilePath);
    }


    
}

